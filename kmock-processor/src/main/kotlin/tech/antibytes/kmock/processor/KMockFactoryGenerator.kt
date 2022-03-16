/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

internal class KMockFactoryGenerator(
    private val logger: KSPLogger,
    private val genericResolver: ProcessorContract.GenericResolver,
    private val codeGenerator: CodeGenerator,
) : ProcessorContract.MockFactoryGenerator {
    private val unused = AnnotationSpec.builder(Suppress::class).addMember("%S", "UNUSED_PARAMETER").build()
    
    private fun buildRelaxedParameter(
        isKmp: Boolean
    ): ParameterSpec {
        val parameter = ParameterSpec.builder("relaxed", Boolean::class)
        if (!isKmp) {
            parameter.defaultValue("false")
        }

        return parameter.addAnnotation(unused).build()
    }

    private fun buildUnitRelaxedParameter(
        isKmp: Boolean
    ): ParameterSpec {
        val parameter = ParameterSpec.builder("relaxUnitFun", Boolean::class)

        if (!isKmp) {
            parameter.defaultValue("false")
        }

        return parameter.addAnnotation(unused).build()
    }

    private fun buildVerifierParameter(
        isKmp: Boolean
    ): ParameterSpec {
        val parameter = ParameterSpec.builder("verifier", COLLECTOR_NAME)
        if (!isKmp) {
            parameter.defaultValue("Collector { _, _ -> Unit }")
        }
        return parameter.build()
    }

    private fun buildFreezeParameter(
        isKmp: Boolean
    ): ParameterSpec {
        val parameter = ParameterSpec.builder("freeze", Boolean::class)
        if (!isKmp) {
            parameter.defaultValue("true")
        }
        return parameter.build()
    }

    private fun buildGenericsInfo(
        generics: List<TypeName>
    ): String {
        return if (generics.isEmpty()) {
            ""
        } else {
            "<${generics.joinToString(", ")}>"
        }
    }

    private fun buildMockSelector(
        functionFactory: FunSpec.Builder,
        templateSources: List<TemplateSource>,
        generics: List<TypeName>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        functionFactory.beginControlFlow("return when (T::class)")
        val typeInfo = buildGenericsInfo(generics)

        templateSources.forEach { source ->
            val qualifiedName = source.template.qualifiedName!!.asString()
            val interfaceName = "${source.template.packageName.asString()}.${source.template.simpleName.asString()}"

            if (relaxer == null) {
                functionFactory.addStatement(
                    "%L::class -> %LMock%L(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as T",
                    qualifiedName,
                    interfaceName,
                    typeInfo,
                )

                functionFactory.addStatement(
                    "%LMock::class -> %LMock%L(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as T",
                    interfaceName,
                    interfaceName,
                    typeInfo,
                )
            } else {
                functionFactory.addStatement(
                    "%L::class -> %LMock%L(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze) as T",
                    qualifiedName,
                    interfaceName,
                    typeInfo,
                )
                functionFactory.addStatement(
                    "%LMock::class -> %LMock%L(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze) as T",
                    interfaceName,
                    interfaceName,
                    typeInfo,
                )
            }
        }
        functionFactory.addStatement("else -> throw RuntimeException(\"Unknown Interface \${T::class.simpleName}.\")")
        functionFactory.endControlFlow()

        return functionFactory
    }

    private fun fillMockFactory(
        type: TypeVariableName,
        generics: List<TypeName>,
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val functionFactory = FunSpec.builder("kmock")

        functionFactory.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addParameter(buildVerifierParameter(isKmp))
            .addParameter(buildRelaxedParameter(isKmp))
            .addParameter(buildUnitRelaxedParameter(isKmp))
            .addParameter(buildFreezeParameter(isKmp))
            .returns(type).addTypeVariable(type)

        if (isKmp) {
            functionFactory.addModifiers(KModifier.ACTUAL)
        }
        
        return buildMockSelector(
            functionFactory = functionFactory,
            templateSources = templateSources,
            generics = generics,
            relaxer = relaxer
        )
    }

    private fun buildMockFactory(
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): FunSpec {
        val type = TypeVariableName("T")

        return fillMockFactory(
            type = type,
            generics = emptyList(),
            isKmp = isKmp,
            templateSources = templateSources,
            relaxer = relaxer
        ).build()
    }

    private fun buildSpyParameter(): ParameterSpec {
        return ParameterSpec.builder("spyOn", TypeVariableName("SpyOn"))
            .build()
    }

    private fun buildSpySelector(
        function: FunSpec.Builder,
        templateSources: List<TemplateSource>,
    ): FunSpec.Builder {
        function.beginControlFlow("return when (Mock::class)")

        templateSources.forEach { source ->
            val qualifiedName = source.template.qualifiedName!!.asString()
            val interfaceName = "${source.template.packageName.asString()}.${source.template.simpleName.asString()}"
            function.addStatement(
                "%L::class -> %LMock(verifier = verifier, freeze = freeze, spyOn = spyOn as %L) as Mock",
                qualifiedName,
                interfaceName,
                qualifiedName,
            )
            function.addStatement(
                "%LMock::class -> %LMock(verifier = verifier, freeze = freeze, spyOn = spyOn as %L) as Mock",
                interfaceName,
                interfaceName,
                qualifiedName,
            )
        }
        function.addStatement("else -> throw RuntimeException(\"Unknown Interface \${Mock::class.simpleName}.\")")
        function.endControlFlow()

        return function
    }

    private fun buildSpyFactory(
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
    ): FunSpec {
        val factory = FunSpec.builder("kspy")
        val spy = TypeVariableName("SpyOn")
        val mock = TypeVariableName("Mock").copy(bounds = listOf(spy))

        factory.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addTypeVariable(mock.copy(reified = true))
            .addTypeVariable(spy.copy(reified = true))
            .returns(mock)
            .addParameter(buildSpyParameter())
            .addParameter(buildVerifierParameter(isKmp))
            .addParameter(buildFreezeParameter(isKmp))

        if (isKmp) {
            factory.addModifiers(KModifier.ACTUAL)
        }

        return buildSpySelector(factory, templateSources).build()
    }

    private fun buildGenericMockFactoryType(
        templateSource: TemplateSource,
    ): TypeVariableName {
        val template = templateSource.template
        val typeResolver = template.typeParameters.toTypeParameterResolver()
        val boundary = genericResolver.resolveMockClassType(template, typeResolver)

        return TypeVariableName("T", bounds = listOf(boundary)).copy(reified = true)
    }
    
    private fun FunSpec.Builder.amendIgnorableValues(
        types: List<TypeName>
    ): FunSpec.Builder {
        var counter = 0
        types.forEach { type ->
            this.addParameter(
                ParameterSpec.builder(
                    name = "ignoreMe$counter", 
                    type = type.copy(nullable = true)
                ).addAnnotation(unused).build()
            )
            
            counter += 1
        }

        return this
    }

    private fun buildGenericMockFactory(
        isKmp: Boolean,
        templateSource: TemplateSource,
        relaxer: Relaxer?
    ): FunSpec {
        val template = templateSource.template
        val typeResolver = template.typeParameters.toTypeParameterResolver()
        val generics = genericResolver.mapDeclaredGenerics(
            generics = templateSource.generics!!,
            typeResolver = typeResolver
        )

        val type = buildGenericMockFactoryType(
            templateSource
        )

        return fillMockFactory(
            templateSources = listOf(templateSource),
            generics = generics,
            type = type,
            isKmp = isKmp,
            relaxer = relaxer
        ).addTypeVariables(generics).amendIgnorableValues(generics).build()
    }

    private fun buildGenericMockFactories(
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): List<FunSpec> {
        return templateSources.map { template ->
            buildGenericMockFactory(isKmp, template, relaxer)
        }
    }

    private fun splitInterfacesIntoRegularAndGenerics(
        templateSources: List<TemplateSource>
    ): Pair<List<TemplateSource>, List<TemplateSource>> {
        val regular: MutableList<TemplateSource> = mutableListOf()
        val generics: MutableList<TemplateSource> = mutableListOf()

        templateSources.forEach { source ->
            if (source.generics == null) {
                regular.add(source)
            } else {
                generics.add(source)
            }
        }

        return Pair(regular, generics)
    }

    override fun writeFactories(
        options: ProcessorContract.Options,
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        if (templateSources.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            val file = FileSpec.builder(
                options.rootPackage,
                "MockFactory"
            )
            file.addImport(ProcessorContract.KMOCK_CONTRACT.packageName, ProcessorContract.KMOCK_CONTRACT.simpleName)

            val (regular, generics) = splitInterfacesIntoRegularAndGenerics(templateSources)

            val genericFactories = buildGenericMockFactories(
                templateSources = generics,
                relaxer = relaxer,
                isKmp = options.isKmp,
            )

            file.addFunction(
                buildMockFactory(
                    templateSources = regular,
                    relaxer = relaxer,
                    isKmp = options.isKmp,
                )
            )

            genericFactories.forEach { factory ->
                file.addFunction(factory)
            }

            file.addFunction(
                buildSpyFactory(
                    templateSources = regular,
                    isKmp = options.isKmp,
                )
            )

            file.build().writeTo(
                codeGenerator = codeGenerator,
                aggregating = false,
                originatingKSFiles = dependencies
            )
        }
    }
}
