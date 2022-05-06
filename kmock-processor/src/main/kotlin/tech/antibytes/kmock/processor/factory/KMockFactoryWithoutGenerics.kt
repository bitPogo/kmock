/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeVariableName
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SHARED_MOCK_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryWithoutGenerics

internal class KMockFactoryWithoutGenerics(
    private val isKmp: Boolean,
    private val allowInterfaces: Boolean,
    private val utils: MockFactoryGeneratorUtil,
) : MockFactoryWithoutGenerics {
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

    private fun determineMockTemplate(
        relaxer: Relaxer?
    ): Pair<String, String> {
        return if (relaxer == null) {
            Pair(
                "%L::class -> %LMock(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as %L?) as $KMOCK_FACTORY_TYPE_NAME",
                "%LMock::class -> %LMock(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as %L?) as $KMOCK_FACTORY_TYPE_NAME",
            )
        } else {
            Pair(
                "%L::class -> %LMock(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as %L?) as $KMOCK_FACTORY_TYPE_NAME",
                "%LMock::class -> %LMock(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as %L?) as $KMOCK_FACTORY_TYPE_NAME",
            )
        }
    }

    private fun addMock(
        mockFactory: FunSpec.Builder,
        qualifiedName: String,
        interfaceName: String,
        aliasInterfaceName: String?,
        relaxer: Relaxer?
    ) {
        val (interfaceInvocationTemplate, mockInvocationTemplate) = determineMockTemplate(relaxer)
        if (allowInterfaces) {
            mockFactory.addStatement(
                interfaceInvocationTemplate,
                qualifiedName,
                aliasInterfaceName ?: interfaceName,
                qualifiedName,
            )
        }

        mockFactory.addStatement(
            mockInvocationTemplate,
            aliasInterfaceName ?: interfaceName,
            aliasInterfaceName ?: interfaceName,
            qualifiedName,
        )
    }

    private fun amendSource(
        mockFactory: FunSpec.Builder,
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
            relaxer = relaxer,
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
                    templateSource = source,
                    relaxer = relaxer
                )
            }
        }
    }

    private fun buildShallowMock(
        mockFactory: FunSpec.Builder,
    ): FunSpec.Builder {
        return mockFactory.addCode(
            "throw RuntimeException(\"Unknown Interface \${$KMOCK_FACTORY_TYPE_NAME::class.simpleName}.\")"
        )
    }

    private fun resolveModifier(): KModifier? {
        return if (isKmp) {
            KModifier.ACTUAL
        } else {
            null
        }
    }

    private fun fillMockFactory(): FunSpec.Builder {
        val modifier = resolveModifier()

        return utils.generateKmockSignature(
            type = kmockType,
            generics = emptyList(),
            hasDefault = !isKmp,
            modifier = modifier
        ).addCode(factoryInvocation)
    }

    override fun buildKMockFactory(): FunSpec = fillMockFactory().build()

    private fun fillSpyFactory(): FunSpec.Builder {
        val modifier = resolveModifier()

        return utils.generateKspySignature(
            mockType = kspyMockType,
            spyType = kspyType,
            generics = emptyList(),
            hasDefault = !isKmp,
            modifier = modifier
        ).addCode(spyFactoryInvocation)
    }

    override fun buildSpyFactory(): FunSpec = fillSpyFactory().build()

    override fun buildSharedFactory(
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): FunSpec {
        val mockFactory = utils.generateMockFactorySignature(
            mockType = kspyMockType,
            spyType = kspyType,
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
        }.build()
    }

    private companion object {
        private val kmockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME).copy(reified = true)
        private val kspyType = TypeVariableName(KSPY_FACTORY_TYPE_NAME)
        private val kspyMockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(kspyType))

        private val factoryInvocation = """
                |return ${SHARED_MOCK_FACTORY}(
                |   spyOn = null,
                |   verifier = verifier,
                |   relaxed = relaxed,
                |   relaxUnitFun = relaxUnitFun,
                |   freeze = freeze,
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
    }
}
