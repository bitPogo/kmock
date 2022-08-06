/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.BuildInMethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COMMON_INDICATOR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FREEZE_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.MULTI_MOCK
import tech.antibytes.kmock.processor.ProcessorContract.Companion.MULTI_MOCK_TYPE
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NOOP_COLLECTOR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NULLABLE_ANY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PROXY_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.RELAXER_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_PROPERTY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNIT_RELAXER_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.KmpCodeGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ParentFinder
import tech.antibytes.kmock.processor.ProcessorContract.PropertyGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGeneratorFactory
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameCollector
import tech.antibytes.kmock.processor.ProcessorContract.ReceiverGenerator
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.SpyContainer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.kotlinpoet.toTypeParameterResolver
import tech.antibytes.kmock.processor.utils.isPublicOpen
import tech.antibytes.kmock.processor.utils.isReceiverMethod
import tech.antibytes.kmock.proxy.NoopCollector

internal class KMockGenerator(
    private val logger: KSPLogger,
    private val enableProxyAccessMethodGenerator: Boolean,
    private val spyContainer: SpyContainer,
    private val useBuildInProxiesOn: Set<String>,
    private val codeGenerator: KmpCodeGenerator,
    private val genericsResolver: GenericResolver,
    private val nameCollector: ProxyNameCollector,
    private val parentFinder: ParentFinder,
    private val propertyGenerator: PropertyGenerator,
    private val methodGenerator: MethodGenerator,
    private val buildInGenerator: BuildInMethodGenerator,
    private val receiverGenerator: ReceiverGenerator,
    private val proxyAccessMethodGeneratorFactory: ProxyAccessMethodGeneratorFactory,
    private val preventResolvingOfAliases: Set<String>,
) : ProcessorContract.MockGenerator {
    private fun resolveSpyType(superTypes: List<TypeName>): TypeName {
        return if (superTypes.size == 1) {
            superTypes.first()
        } else {
            MULTI_MOCK_TYPE
        }
    }

    private fun buildConstructor(superTypes: List<TypeName>): FunSpec {
        val constructor = FunSpec.constructorBuilder()

        val collector = ParameterSpec.builder(COLLECTOR_ARGUMENT, COLLECTOR_NAME)
        collector.defaultValue(noopCollector)
        constructor.addParameter(collector.build())

        val spy = ParameterSpec.builder(
            SPY_ARGUMENT,
            resolveSpyType(superTypes).copy(nullable = true),
        ).addAnnotation(UNUSED_PARAMETER).defaultValue("null")
        constructor.addParameter(spy.build())

        val freeze = ParameterSpec.builder(FREEZE_ARGUMENT, Boolean::class)
        freeze.defaultValue("true")
        constructor.addParameter(freeze.build())

        val relaxUnit = ParameterSpec.builder(
            UNIT_RELAXER_ARGUMENT,
            Boolean::class,
        ).addAnnotation(UNUSED).defaultValue("false")
        constructor.addParameter(relaxUnit.build())

        val relaxed = ParameterSpec.builder(
            RELAXER_ARGUMENT,
            Boolean::class,
        ).addAnnotation(UNUSED).defaultValue("false")
        constructor.addParameter(relaxed.build())

        return constructor.build()
    }

    private fun buildClear(
        proxyNames: List<String>,
    ): FunSpec {
        val function = FunSpec
            .builder("_clearMock")

        proxyNames.forEach { name ->
            function.addStatement("$name.clear()")
        }

        return function.build()
    }

    private fun constructQualifier(
        mockName: String,
        template: KSClassDeclaration,
    ): String = "${template.packageName.asString()}.$mockName"

    private val KSFunctionDeclaration.firstParameter: String?
        get() = parameters.firstOrNull()?.type?.resolve()?.declaration?.simpleName?.asString()

    private fun KSFunctionDeclaration.isBuildInMethod(): Boolean {
        val name = simpleName.asString()

        return (name == "toString" && this.parameters.isEmpty()) ||
            (name == "hashCode" && this.parameters.isEmpty()) ||
            (name == "equals" && firstParameter == "Any")
    }

    private fun resolveSuperTypes(
        template: KSClassDeclaration,
        parents: TemplateMultiSource?,
        typeResolver: TypeParameterResolver,
    ): List<TypeName> {
        return if (parents != null) {
            val (parameterizedParent, _) = genericsResolver.remapTypes(parents.templates, parents.generics)
            parameterizedParent
        } else {
            listOf(
                genericsResolver.resolveMockClassType(template, typeResolver),
            )
        }
    }

    private fun TypeSpec.Builder.addPropertyBundle(
        spyType: TypeName,
        proxyNameCollector: MutableList<String>,
        proxyAccessMethodGenerator: ProxyAccessMethodGenerator,
        ksProperty: KSPropertyDeclaration,
        qualifier: String,
        classScopeGenerics: Map<String, List<TypeName>>?,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        relaxer: Relaxer?,
    ) {
        if (!ksProperty.isReceiverMethod()) {
            val (proxy, property) = propertyGenerator.buildPropertyBundle(
                qualifier = qualifier,
                classScopeGenerics = classScopeGenerics,
                ksProperty = ksProperty,
                classWideResolver = typeResolver,
                enableSpy = enableSpy,
                relaxer = relaxer,
            )

            this.addProperty(property)

            this.addProperty(proxy)
            proxyNameCollector.add(proxy.name)
            proxyAccessMethodGenerator.collectProperty(
                propertyName = property.name,
                propertyType = property.type,
                proxyName = proxy.name,
            )
        } else {
            val (proxyGetter, proxySetter, property) = receiverGenerator.buildPropertyBundle(
                spyType = spyType,
                qualifier = qualifier,
                classScopeGenerics = classScopeGenerics,
                ksProperty = ksProperty,
                classWideResolver = typeResolver,
                enableSpy = enableSpy,
                relaxer = relaxer,
            )

            this.addProperty(property)

            this.addProperty(proxyGetter)
            proxyNameCollector.add(proxyGetter.name)

            if (proxySetter != null) {
                this.addProperty(proxySetter)
                proxyNameCollector.add(proxySetter.name)
            }
        }
    }

    private fun TypeSpec.Builder.addMethodBundle(
        spyType: TypeName,
        proxyNameCollector: MutableList<String>,
        proxyAccessMethodGenerator: ProxyAccessMethodGenerator,
        ksFunction: KSFunctionDeclaration,
        qualifier: String,
        enableSpy: Boolean,
        classScopeGenerics: Map<String, List<TypeName>>?,
        typeResolver: TypeParameterResolver,
        relaxer: Relaxer?,
    ) {
        val (proxy, method, sideEffect) = if (ksFunction.isReceiverMethod()) {
            receiverGenerator.buildMethodBundle(
                spyType = spyType,
                qualifier = qualifier,
                classScopeGenerics = classScopeGenerics,
                ksFunction = ksFunction,
                classWideResolver = typeResolver,
                enableSpy = enableSpy,
                relaxer = relaxer,
            )
        } else {
            methodGenerator.buildMethodBundle(
                qualifier = qualifier,
                classScopeGenerics = classScopeGenerics,
                ksFunction = ksFunction,
                classWideResolver = typeResolver,
                enableSpy = enableSpy,
                relaxer = relaxer,
            )
        }

        this.addFunction(method)
        this.addProperty(proxy)

        proxyNameCollector.add(proxy.name)
        proxyAccessMethodGenerator.collectMethod(
            methodName = method.name,
            isSuspending = method.modifiers.contains(KModifier.SUSPEND),
            typeParameter = method.typeVariables,
            arguments = method.parameters,
            returnType = method.returnType,
            proxyName = proxy.name,
            proxySignature = proxy.type as ParameterizedTypeName,
            proxySideEffect = sideEffect,
        )
    }

    private fun List<TypeName>.resolveType(): Pair<TypeName, Boolean> {
        val isNullable: Boolean
        val type = when (this.size) {
            0 -> {
                isNullable = true
                NULLABLE_ANY
            }
            1 -> {
                val type = this.first()
                isNullable = type.isNullable

                type
            }
            else -> {
                isNullable = this.all { type -> type.isNullable }

                NULLABLE_ANY
            }
        }

        return Pair(type, isNullable)
    }

    private fun TypeVariableName.resolveNullableType(
        mapping: Map<String, TypeVariableName>,
    ): Pair<Boolean, TypeName> {
        var currentName = this.name
        var isNullable = false
        var currentType: TypeName = this

        while (currentName in mapping) {
            val (type, nullability) = mapping[currentName]!!.bounds.resolveType()

            currentType = type
            currentName = type.toString()
            isNullable = nullability
        }

        return Pair(
            isNullable,
            currentType.copy(nullable = false),
        )
    }

    private fun List<TypeVariableName>.collectNullableClassGenerics(): Map<String, TypeName> {
        return if (this.isEmpty() || !enableProxyAccessMethodGenerator) {
            emptyMap()
        } else {
            val nullables: MutableMap<String, TypeName> = mutableMapOf()
            val mapping = this.associateBy { type -> type.name }

            this.forEach { type ->
                val (nullability, resolvedType) = type.resolveNullableType(mapping)

                if (nullability) {
                    nullables[type.name] = resolvedType
                }
            }

            nullables
        }
    }

    private fun buildMock(
        mockName: String,
        enableSpy: Boolean,
        parents: TemplateMultiSource?,
        template: KSClassDeclaration,
        generics: Map<String, List<KSTypeReference>>?,
        relaxer: Relaxer?,
    ): TypeSpec {
        val mock = TypeSpec.classBuilder(mockName)
        val typeResolver = template.typeParameters.toTypeParameterResolver()
        val templateName = template.qualifiedName!!.asString()
        val qualifier = constructQualifier(mockName, template)

        val superTypes = resolveSuperTypes(
            template = template,
            parents = parents,
            typeResolver = typeResolver,
        )
        val proxyNameCollector: MutableList<String> = mutableListOf()

        val classScopeGenerics = genericsResolver.mapClassScopeGenerics(generics, typeResolver)
        val spyType = resolveSpyType(superTypes)
        var hasReceivers = false
        val nullableClassGenerics = genericsResolver
            .mapDeclaredGenerics(generics ?: emptyMap(), typeResolver)
            .collectNullableClassGenerics()
        val proxyAccessMethodGenerator = proxyAccessMethodGeneratorFactory.getInstance(
            enableGenerator = enableProxyAccessMethodGenerator,
            preventResolvingOfAliases = preventResolvingOfAliases,
            nullableClassGenerics = nullableClassGenerics,
            classScopeGenerics = classScopeGenerics?.keys ?: emptySet(),
        )

        mock.addSuperinterfaces(superTypes)
        mock.addModifiers(KModifier.INTERNAL)

        if (generics != null) {
            mock.typeVariables.addAll(
                genericsResolver.mapDeclaredGenerics(generics, typeResolver),
            )
        }

        if (superTypes.size > 1) {
            mock.typeVariables.add(
                TypeVariableName(MULTI_MOCK, bounds = superTypes),
            )
        }

        mock.primaryConstructor(
            buildConstructor(superTypes),
        )

        mock.addProperty(
            PropertySpec.builder(
                UNIT_RELAXER_ARGUMENT,
                Boolean::class,
                KModifier.PRIVATE,
            ).initializer(UNIT_RELAXER_ARGUMENT).build(),
        )

        mock.addProperty(
            PropertySpec.builder(
                RELAXER_ARGUMENT,
                Boolean::class,
                KModifier.PRIVATE,
            ).initializer(RELAXER_ARGUMENT).build(),
        )

        if (enableSpy) {
            mock.addProperty(
                PropertySpec.builder(
                    SPY_PROPERTY,
                    resolveSpyType(superTypes).copy(nullable = true),
                    KModifier.PRIVATE,
                ).initializer(SPY_ARGUMENT).build(),
            )
        }

        template.getAllProperties().forEach { ksProperty ->
            if (ksProperty.isPublicOpen()) {
                hasReceivers = ksProperty.isReceiverMethod()
                mock.addPropertyBundle(
                    spyType = spyType,
                    proxyNameCollector = proxyNameCollector,
                    proxyAccessMethodGenerator = proxyAccessMethodGenerator,
                    ksProperty = ksProperty,
                    qualifier = qualifier,
                    classScopeGenerics = classScopeGenerics,
                    typeResolver = typeResolver,
                    enableSpy = enableSpy,
                    relaxer = relaxer,
                )
            }
        }

        template.getAllFunctions().forEach { ksFunction ->
            if (ksFunction.isPublicOpen() && (!ksFunction.isBuildInMethod() || ksFunction.isReceiverMethod())) {
                hasReceivers = hasReceivers || ksFunction.isReceiverMethod()
                mock.addMethodBundle(
                    spyType = spyType,
                    proxyNameCollector = proxyNameCollector,
                    proxyAccessMethodGenerator = proxyAccessMethodGenerator,
                    ksFunction = ksFunction,
                    qualifier = qualifier,
                    enableSpy = enableSpy,
                    classScopeGenerics = classScopeGenerics,
                    typeResolver = typeResolver,
                    relaxer = relaxer,
                )
            }
        }

        if (enableSpy || templateName in useBuildInProxiesOn) {
            val bundle = buildInGenerator.buildMethodBundles(
                mockName = mockName,
                qualifier = qualifier,
                enableSpy = enableSpy,
            )

            bundle.forEach { (proxy, method, sideEffect) ->
                mock.addFunction(method)
                mock.addProperty(proxy)

                proxyNameCollector.add(proxy.name)
                proxyAccessMethodGenerator.collectMethod(
                    methodName = method.name,
                    isSuspending = method.modifiers.contains(KModifier.SUSPEND),
                    typeParameter = method.typeVariables,
                    arguments = method.parameters,
                    returnType = method.returnType,
                    proxyName = proxy.name,
                    proxySignature = proxy.type as ParameterizedTypeName,
                    proxySideEffect = sideEffect,
                )
            }
        }

        if (hasReceivers && enableSpy) {
            mock.addFunction(
                receiverGenerator.buildReceiverSpyContext(
                    spyType = spyType,
                    classWideResolver = typeResolver,
                ),
            )
        }

        mock.addFunction(buildClear(proxyNameCollector))

        if (enableProxyAccessMethodGenerator) {
            mock.addProperty(proxyAccessMethodGenerator.createReferenceStorage())
            mock.addFunctions(proxyAccessMethodGenerator.createAccessMethods())
        }

        return mock.build()
    }

    private fun List<KSFile>.amendRelaxer(relaxer: Relaxer?): List<KSFile> {
        return if (relaxer == null) {
            this
        } else {
            this.toMutableList().also { it.add(relaxer.source) }
        }
    }

    private fun writeMock(
        template: KSClassDeclaration,
        parents: TemplateMultiSource?,
        templateName: String,
        packageName: String,
        generics: Map<String, List<KSTypeReference>>?,
        dependencies: List<KSFile>,
        relaxer: Relaxer?,
    ) {
        val mockName = "${templateName.substringAfterLast('.')}Mock"
        val file = FileSpec.builder(
            packageName,
            mockName,
        )

        val enableSpy = spyContainer.isSpyable(
            template = template,
            templateName = templateName,
            packageName = packageName,
        )

        nameCollector.collect(template)

        val implementation = buildMock(
            mockName = mockName,
            parents = parents,
            template = template,
            enableSpy = enableSpy,
            generics = generics,
            relaxer = relaxer,
        )

        file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)
        file.addImport(PROXY_FACTORY.packageName, PROXY_FACTORY.simpleName)
        file.addImport(NOOP_COLLECTOR.packageName, NOOP_COLLECTOR.simpleName)

        if (relaxer != null) {
            file.addImport(relaxer.packageName, relaxer.functionName)
        }

        file.addType(implementation)
        file.build().writeTo(
            codeGenerator = codeGenerator,
            aggregating = true,
            originatingKSFiles = dependencies.amendRelaxer(relaxer),
        )
    }

    override fun writeCommonMocks(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        relaxer: Relaxer?,
    ) {
        templateSources.forEach { template ->
            codeGenerator.setOneTimeSourceSet(COMMON_INDICATOR)

            val parents = parentFinder.find(
                templateSource = template,
                templateMultiSources = templateMultiSources,
            )

            writeMock(
                template = template.template,
                parents = parents,
                templateName = template.templateName,
                packageName = template.packageName,
                generics = template.generics,
                dependencies = template.dependencies,
                relaxer = relaxer,
            )
        }
    }

    override fun writeSharedMocks(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        relaxer: Relaxer?,
    ) {
        templateSources.forEach { template ->
            codeGenerator.setOneTimeSourceSet(template.indicator)

            val parents = parentFinder.find(
                templateSource = template,
                templateMultiSources = templateMultiSources,
            )

            writeMock(
                template = template.template,
                parents = parents,
                templateName = template.templateName,
                packageName = template.packageName,
                generics = template.generics,
                dependencies = template.dependencies,
                relaxer = relaxer,
            )
        }
    }

    override fun writePlatformMocks(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        relaxer: Relaxer?,
    ) {
        templateSources.forEach { template ->
            val parents = parentFinder.find(
                templateSource = template,
                templateMultiSources = templateMultiSources,
            )

            writeMock(
                template = template.template,
                parents = parents,
                templateName = template.templateName,
                packageName = template.packageName,
                generics = template.generics,
                dependencies = template.dependencies,
                relaxer = relaxer,
            )
        }
    }

    private companion object {
        private val noopCollector = NoopCollector::class.asClassName().simpleName
        private val UNUSED_PARAMETER = AnnotationSpec.builder(Suppress::class).addMember("%S", "UNUSED_PARAMETER").build()
        private val UNUSED = AnnotationSpec.builder(Suppress::class).addMember("%S", "unused").build()
    }
}
