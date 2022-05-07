/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeVariableName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SHARED_MOCK_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.FactoryBundle
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryWithGenerics
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal class KMockFactoryWithGenerics(
    private val isKmp: Boolean,
    private val allowInterfaces: Boolean,
    private val utils: MockFactoryGeneratorUtil,
    private val genericResolver: GenericResolver,
) : MockFactoryWithGenerics {
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

    private fun resolveModifier(): KModifier? {
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
        val modifier = resolveModifier()

        return utils.generateKmockSignature(
            type = type,
            generics = generics,
            hasDefault = !isKmp,
            modifier = modifier
        ).addCode(factoryInvocationWithTemplate)
    }

    private fun fillSpyFactory(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        generics: List<TypeVariableName>,
        isKmp: Boolean,
    ): FunSpec.Builder {
        val modifier = resolveModifier()

        return utils.generateKspySignature(
            mockType = mockType,
            spyType = spyType,
            generics = generics,
            hasDefault = !isKmp,
            modifier = modifier
        ).addCode(spyFactoryInvocationWithTemplate)
    }

    private fun buildGenericMockFactory(templateSource: TemplateSource): FunSpec {
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

    private fun buildGenericSpyFactory(templateSource: TemplateSource): FunSpec {
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

    private fun buildGenericSharedMockFactory(
        templateSource: TemplateSource,
        relaxer: Relaxer?
    ): FunSpec {
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
        ).build()
    }

    override fun buildGenericFactories(
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): List<FactoryBundle> {
        val factories: MutableList<FactoryBundle> = mutableListOf()

        templateSources.forEach { source ->
            val kmock = buildGenericMockFactory(source)
            val kspy = buildGenericSpyFactory(source)
            val shared = buildGenericSharedMockFactory(
                templateSource = source,
                relaxer = relaxer
            )

            factories.add(
                FactoryBundle(
                    kmock = kmock,
                    kspy = kspy,
                    shared = shared
                )
            )
        }

        return factories
    }

    private companion object {
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
    }
}
