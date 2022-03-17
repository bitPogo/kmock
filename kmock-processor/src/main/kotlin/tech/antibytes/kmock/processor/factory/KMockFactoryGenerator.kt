/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

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
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

internal class KMockFactoryGenerator(
    private val logger: KSPLogger,
    private val utils: ProcessorContract.MockFactoryGeneratorUtil,
    private val genericResolver: ProcessorContract.GenericResolver,
    private val codeGenerator: CodeGenerator,
) : ProcessorContract.MockFactoryGenerator {
    private val unused = AnnotationSpec.builder(Suppress::class).addMember("%S", "UNUSED_PARAMETER").build()
    
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
        val typeInfo = buildGenericsInfo(generics)

        functionFactory.beginControlFlow("return when (${KMOCK_FACTORY_TYPE_NAME}::class)")
        templateSources.forEach { source ->
            val qualifiedName = source.template.qualifiedName!!.asString()
            val interfaceName = "${source.template.packageName.asString()}.${source.template.simpleName.asString()}"

            if (relaxer == null) {
                functionFactory.addStatement(
                    "%L::class -> %LMock%L(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as $KMOCK_FACTORY_TYPE_NAME",
                    qualifiedName,
                    interfaceName,
                    typeInfo,
                )

                functionFactory.addStatement(
                    "%LMock::class -> %LMock%L(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as $KMOCK_FACTORY_TYPE_NAME",
                    interfaceName,
                    interfaceName,
                    typeInfo,
                )
            } else {
                functionFactory.addStatement(
                    "%L::class -> %LMock%L(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze) as $KMOCK_FACTORY_TYPE_NAME",
                    qualifiedName,
                    interfaceName,
                    typeInfo,
                )
                functionFactory.addStatement(
                    "%LMock::class -> %LMock%L(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze) as $KMOCK_FACTORY_TYPE_NAME",
                    interfaceName,
                    interfaceName,
                    typeInfo,
                )
            }
        }
        functionFactory.addStatement("else -> throw RuntimeException(\"Unknown Interface \${${KMOCK_FACTORY_TYPE_NAME}::class.simpleName}.\")")
        functionFactory.endControlFlow()

        return functionFactory
    }

    private fun resolveModifier(isKmp: Boolean): KModifier? {
        return if (isKmp) {
            KModifier.ACTUAL
        } else {
            null
        }
    }

    private fun fillMockFactory(
        type: TypeVariableName,
        generics: List<TypeName>,
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val modifier = resolveModifier(isKmp)
        val functionFactory = utils.generateKmockSignature(
            type = type,
            hasDefault = !isKmp,
            modifier = modifier
        )

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
        val type = TypeVariableName(KMOCK_FACTORY_TYPE_NAME)

        return fillMockFactory(
            type = type.copy(reified = true),
            generics = emptyList(),
            isKmp = isKmp,
            templateSources = templateSources,
            relaxer = relaxer
        ).build()
    }

    private fun buildSpySelector(
        function: FunSpec.Builder,
        generics: List<TypeName>,
        templateSources: List<TemplateSource>,
    ): FunSpec.Builder {
        val typeInfo = buildGenericsInfo(generics)

        function.beginControlFlow("return when (${KMOCK_FACTORY_TYPE_NAME}::class)")
        templateSources.forEach { source ->
            val qualifiedName = source.template.qualifiedName!!.asString()
            val interfaceName = "${source.template.packageName.asString()}.${source.template.simpleName.asString()}"
            function.addStatement(
                "%L::class -> %LMock(verifier = verifier, freeze = freeze, spyOn = spyOn as %L%L) as $KMOCK_FACTORY_TYPE_NAME",
                qualifiedName,
                interfaceName,
                qualifiedName,
                typeInfo,
            )
            function.addStatement(
                "%LMock::class -> %LMock(verifier = verifier, freeze = freeze, spyOn = spyOn as %L%L) as $KMOCK_FACTORY_TYPE_NAME",
                interfaceName,
                interfaceName,
                qualifiedName,
                typeInfo,
            )
        }
        function.addStatement("else -> throw RuntimeException(\"Unknown Interface \${${KMOCK_FACTORY_TYPE_NAME}::class.simpleName}.\")")
        function.endControlFlow()

        return function
    }

    private fun fillSpyFactory(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        generics: List<TypeName>,
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
    ): FunSpec.Builder {
        val modifier = resolveModifier(isKmp)
        val spyFactory = utils.generateKspySignature(
            mockType = mockType,
            spyType = spyType,
            hasDefault = !isKmp,
            modifier = modifier
        )

        return buildSpySelector(spyFactory, generics, templateSources)
    }

    private fun buildSpyFactory(
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
    ): FunSpec {
        val spyType = TypeVariableName(KSPY_FACTORY_TYPE_NAME)
        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return fillSpyFactory(
            mockType = mockType,
            spyType = spyType,
            generics = emptyList(),
            templateSources = templateSources,
            isKmp = isKmp,
        ).build()
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
                ).addAnnotation(unused).defaultValue("null").build()
            )
            
            counter += 1
        }

        return this
    }

    private fun resolveGenerics(
        templateSource: TemplateSource,
    ): List<TypeVariableName> {
        val template = templateSource.template
        val typeResolver = template.typeParameters.toTypeParameterResolver()

        return genericResolver.mapDeclaredGenerics(
            generics = templateSource.generics!!,
            typeResolver = typeResolver
        )
    }

    private fun buildGenericMockFactory(
        isKmp: Boolean,
        templateSource: TemplateSource,
        relaxer: Relaxer?
    ): FunSpec {
        val generics = resolveGenerics(templateSource)

        val type = genericResolver.resolveKMockFactoryType(
            KMOCK_FACTORY_TYPE_NAME,
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

    private fun buildGenericSpyFactory(
        isKmp: Boolean,
        templateSource: TemplateSource,
    ): FunSpec {
        val generics = resolveGenerics(templateSource)

        val spyType = genericResolver.resolveKMockFactoryType(
            KSPY_FACTORY_TYPE_NAME,
            templateSource
        )

        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return fillSpyFactory(
            mockType = mockType,
            spyType = spyType,
            templateSources = listOf(templateSource),
            generics = generics,
            isKmp = isKmp,
        ).addTypeVariables(generics).amendIgnorableValues(generics).build()
    }

    private fun buildGenericFactories(
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): List<Pair<FunSpec, FunSpec>> {
        return templateSources.map { template ->
            Pair(
                buildGenericMockFactory(isKmp, template, relaxer),
                buildGenericSpyFactory(isKmp, template)
            )
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

            val genericFactories = buildGenericFactories(
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

            file.addFunction(
                buildSpyFactory(
                    templateSources = regular,
                    isKmp = options.isKmp,
                )
            )

            genericFactories.forEach { factories ->
                val (mockFactory, spyFactory) = factories

                file.addFunction(mockFactory)
                file.addFunction(spyFactory)
            }

            file.build().writeTo(
                codeGenerator = codeGenerator,
                aggregating = false,
                originatingKSFiles = dependencies
            )
        }
    }
}
