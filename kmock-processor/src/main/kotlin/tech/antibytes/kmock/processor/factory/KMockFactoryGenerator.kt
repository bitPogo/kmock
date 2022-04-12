/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FACTORY_FILE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NOOP_COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SHARED_MOCK_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNUSED
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal class KMockFactoryGenerator(
    private val logger: KSPLogger,
    spyOn: Set<String>,
    private val spiesOnly: Boolean,
    private val allowInterfaces: Boolean,
    private val utils: ProcessorContract.MockFactoryGeneratorUtil,
    private val genericResolver: ProcessorContract.GenericResolver,
    private val codeGenerator: CodeGenerator,
) : ProcessorContract.MockFactoryGenerator {
    private val hasSpies = spyOn.isNotEmpty() || spiesOnly
    private val kmockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME).copy(reified = true)
    private val kspyType = TypeVariableName(KSPY_FACTORY_TYPE_NAME)
    private val kspyMockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(kspyType))
    private val factoryInvocation = """
                |return $SHARED_MOCK_FACTORY(
                |   spyOn = null,
                |   verifier = verifier,
                |   relaxed = relaxed,
                |   relaxUnitFun = relaxUnitFun,
                |   freeze = freeze,
                |)
    """.trimMargin()
    private val factoryInvocationWithTemplate = """
                |return $SHARED_MOCK_FACTORY(
                |   spyOn = null,
                |   verifier = verifier,
                |   relaxed = relaxed,
                |   relaxUnitFun = relaxUnitFun,
                |   freeze = freeze,
                |   templateType = templateType,
                |)
    """.trimMargin()

    private val spyFactoryInvocation = """
                |return $SHARED_MOCK_FACTORY(
                |   spyOn = spyOn,
                |   verifier = verifier,
                |   relaxed = false,
                |   relaxUnitFun = false,
                |   freeze = freeze,
                |)
    """.trimMargin()

    private val spyFactoryInvocationWithTemplate = """
                |return $SHARED_MOCK_FACTORY(
                |   spyOn = spyOn,
                |   verifier = verifier,
                |   relaxed = false,
                |   relaxUnitFun = false,
                |   freeze = freeze,
                |   templateType = templateType,
                |)
    """.trimMargin()

    private fun createAliasName(
        alias: String?,
        packageName: String
    ): String? {
        return if (alias != null) {
            "$packageName.$alias"
        } else {
            null
        }
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
        generics: List<TypeVariableName>,
        isKmp: Boolean,
    ): FunSpec.Builder {
        val modifier = resolveModifier(isKmp)

        val invocation = if (generics.isEmpty()) {
            factoryInvocation
        } else {
            factoryInvocationWithTemplate
        }

        return utils.generateKmockSignature(
            type = type,
            generics = generics,
            hasDefault = !isKmp,
            modifier = modifier
        ).addCode(invocation)
    }

    private fun buildKMock(
        isKmp: Boolean,
    ): FunSpec = fillMockFactory(
        type = kmockType,
        generics = emptyList(),
        isKmp = isKmp,
    ).build()

    private fun fillSpyFactory(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        generics: List<TypeVariableName>,
        isKmp: Boolean,
    ): FunSpec.Builder {
        val modifier = resolveModifier(isKmp)

        val invocation = if (generics.isEmpty()) {
            spyFactoryInvocation
        } else {
            spyFactoryInvocationWithTemplate
        }

        return utils.generateKspySignature(
            mockType = mockType,
            spyType = spyType,
            generics = generics,
            hasDefault = !isKmp,
            modifier = modifier
        ).addCode(invocation)
    }

    private fun buildSpyFactory(
        isKmp: Boolean
    ): FunSpec = fillSpyFactory(
        mockType = kspyMockType,
        spyType = kspyType,
        generics = emptyList(),
        isKmp = isKmp,
    ).build()

    private fun buildGenericMockFactory(
        isKmp: Boolean,
        templateSource: TemplateSource,
    ): FunSpec {
        val generics = utils.resolveGenerics(templateSource)

        val type = genericResolver.resolveKMockFactoryType(
            KMOCK_FACTORY_TYPE_NAME,
            templateSource
        )

        return fillMockFactory(
            generics = generics,
            type = type,
            isKmp = isKmp,
        ).build()
    }

    private fun buildGenericSpyFactory(
        isKmp: Boolean,
        templateSource: TemplateSource,
    ): FunSpec {
        val generics = utils.resolveGenerics(templateSource)

        val spyType = genericResolver.resolveKMockFactoryType(
            KSPY_FACTORY_TYPE_NAME,
            templateSource
        )

        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return fillSpyFactory(
            mockType = mockType,
            spyType = spyType,
            generics = generics,
            isKmp = isKmp,
        ).build()
    }

    private fun buildGenericFactories(
        isKmp: Boolean,
        templateSources: List<TemplateSource>,
    ): List<Pair<FunSpec, FunSpec>> {
        return templateSources.map { template ->
            Pair(
                buildGenericMockFactory(isKmp, template),
                buildGenericSpyFactory(isKmp, template)
            )
        }
    }

    private fun determineMockTemplate(
        relaxer: Relaxer?
    ): Pair<String, String> {
        return if (relaxer == null) {
            Pair(
                "%L::class -> %LMock%L(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as %L%L?) as $KMOCK_FACTORY_TYPE_NAME",
                "%LMock::class -> %LMock%L(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as %L%L?) as $KMOCK_FACTORY_TYPE_NAME",
            )
        } else {
            Pair(
                "%L::class -> %LMock%L(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as %L%L?) as $KMOCK_FACTORY_TYPE_NAME",
                "%LMock::class -> %LMock%L(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as %L%L?) as $KMOCK_FACTORY_TYPE_NAME",
            )
        }
    }

    private fun addMock(
        mockFactory: FunSpec.Builder,
        qualifiedName: String,
        interfaceName: String,
        aliasInterfaceName: String?,
        typeInfo: String,
        relaxer: Relaxer?
    ) {
        val (interfaceInvocationTemplate, mockInvocationTemplate) = determineMockTemplate(relaxer)
        if (allowInterfaces) {
            mockFactory.addStatement(
                interfaceInvocationTemplate,
                qualifiedName,
                aliasInterfaceName ?: interfaceName,
                typeInfo,
                qualifiedName,
                typeInfo,
            )
        }

        mockFactory.addStatement(
            mockInvocationTemplate,
            aliasInterfaceName ?: interfaceName,
            aliasInterfaceName ?: interfaceName,
            typeInfo,
            qualifiedName,
            typeInfo,
        )
    }

    private fun buildMockSelectorFlow(
        mockFactory: FunSpec.Builder,
        addItems: FunSpec.Builder.() -> Unit,
    ): FunSpec.Builder {
        mockFactory.beginControlFlow("return when ($KMOCK_FACTORY_TYPE_NAME::class)")

        addItems(mockFactory)

        mockFactory.addStatement("else -> throw RuntimeException(\"Unknown Interface \${$KMOCK_FACTORY_TYPE_NAME::class.simpleName}.\")")
        mockFactory.endControlFlow()

        return mockFactory
    }

    private fun amendSource(
        mockFactory: FunSpec.Builder,
        typeInfo: String,
        templateSource: TemplateSource,
        relaxer: Relaxer?
    ) {
        val packageName = templateSource.template.packageName.asString()
        val qualifiedName = templateSource.template.qualifiedName!!.asString()
        val aliasInterfaceName = createAliasName(templateSource.alias, packageName)
        val interfaceName = "$packageName.${templateSource.template.simpleName.asString()}"

        addMock(
            mockFactory = mockFactory,
            qualifiedName = qualifiedName,
            aliasInterfaceName = aliasInterfaceName,
            interfaceName = interfaceName,
            typeInfo = typeInfo,
            relaxer = relaxer,
        )
    }

    private fun buildShallowMock(
        mockFactory: FunSpec.Builder,
    ): FunSpec.Builder {
        return mockFactory.addCode(
            "throw RuntimeException(\"Unknown Interface \${$KMOCK_FACTORY_TYPE_NAME::class.simpleName}.\")"
        )
    }

    private fun buildMockSelector(
        mockFactory: FunSpec.Builder,
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        return buildMockSelectorFlow(mockFactory) {
            templateSources.forEach { source ->
                amendSource(
                    mockFactory = this,
                    typeInfo = "",
                    templateSource = source,
                    relaxer = relaxer
                )
            }
        }
    }

    private fun buildMockSelector(
        mockFactory: FunSpec.Builder,
        templateSource: TemplateSource,
        generics: List<TypeVariableName>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val typeInfo = "<${generics.joinToString(", ")}>"

        return buildMockSelectorFlow(mockFactory) {
            amendSource(
                mockFactory = this,
                typeInfo = typeInfo,
                templateSource = templateSource,
                relaxer = relaxer
            )
        }
    }

    private fun fillMockFactory(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val mockFactory = utils.generateMockFactorySignature(
            mockType = mockType,
            spyType = spyType,
            generics = emptyList(),
        )

        return if (templateSources.isEmpty()) {
            buildShallowMock(mockFactory)
        } else {
            buildMockSelector(
                mockFactory = mockFactory,
                templateSources = templateSources,
                relaxer = relaxer
            )
        }
    }

    private fun fillMockFactory(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        templateSource: TemplateSource,
        generics: List<TypeVariableName>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val mockFactory = utils.generateMockFactorySignature(
            mockType = mockType,
            spyType = spyType,
            generics = generics,
        )

        return buildMockSelector(
            mockFactory = mockFactory,
            templateSource = templateSource,
            generics = generics,
            relaxer = relaxer
        )
    }

    private fun buildGenericMockFactory(
        templateSource: TemplateSource,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val genericTypes = utils.resolveGenerics(templateSource)

        val spyType = genericResolver.resolveKMockFactoryType(
            KSPY_FACTORY_TYPE_NAME,
            templateSource
        )

        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return fillMockFactory(
            mockType = mockType,
            spyType = spyType,
            templateSource = templateSource,
            generics = genericTypes,
            relaxer = relaxer
        )
    }

    private fun buildMockFactory(
        regular: List<TemplateSource>,
        generics: List<TemplateSource>,
        relaxer: Relaxer?
    ): List<FunSpec> {
        val factories: MutableList<FunSpec> = mutableListOf()

        factories.add(
            fillMockFactory(
                mockType = kspyMockType,
                spyType = kspyType,
                templateSources = regular,
                relaxer = relaxer
            ).build()
        )

        generics.forEach { source ->
            factories.add(
                buildGenericMockFactory(
                    templateSource = source,
                    relaxer = relaxer,
                ).build()
            )
        }

        return factories
    }

    private fun writeFactoryImplementation(
        options: ProcessorContract.Options,
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        val file = FileSpec.builder(
            options.rootPackage,
            FACTORY_FILE_NAME
        )
        file.addAnnotation(UNUSED)
        file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)

        if (!options.isKmp) {
            file.addImport(NOOP_COLLECTOR_NAME.packageName, NOOP_COLLECTOR_NAME.simpleName)
        }

        val (regular, generics) = utils.splitInterfacesIntoRegularAndGenerics(templateSources)

        val genericFactories = buildGenericFactories(
            templateSources = generics,
            isKmp = options.isKmp,
        )

        val actualFactory = buildMockFactory(
            regular = regular,
            generics = generics,
            relaxer = relaxer,
        )

        actualFactory.forEach { factory ->
            file.addFunction(factory)
        }

        if (!spiesOnly) {
            file.addFunction(
                buildKMock(
                    isKmp = options.isKmp,
                )
            )
        }

        if (hasSpies) {
            file.addFunction(
                buildSpyFactory(
                    isKmp = options.isKmp,
                )
            )
        }

        genericFactories.forEach { factories ->
            val (kmock, kspy) = factories

            if (!spiesOnly) {
                file.addFunction(kmock)
            }

            if (hasSpies) {
                file.addFunction(kspy)
            }
        }

        file.build().writeTo(
            codeGenerator = codeGenerator,
            aggregating = false,
            originatingKSFiles = dependencies
        )
    }

    override fun writeFactories(
        options: ProcessorContract.Options,
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        if (templateSources.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            writeFactoryImplementation(
                options = options,
                templateSources = templateSources,
                dependencies = dependencies,
                relaxer = relaxer
            )
        }
    }
}
