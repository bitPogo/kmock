/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeVariableName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ARGUMENTS_WITHOUT_RELAXER
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ARGUMENTS_WITH_RELAXER
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FREEZE_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.RELAXER_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SHARED_MOCK_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNIT_RELAXER_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNKNOWN_INTERFACE
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryWithoutGenerics
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.Source
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.multi.hasGenerics
import tech.antibytes.kmock.processor.utils.ensureNotNullClassName

internal class KMockFactoryWithoutGenerics(
    private val isKmp: Boolean,
    private val allowInterfaces: Boolean,
    private val utils: MockFactoryGeneratorUtil,
) : MockFactoryWithoutGenerics {
    private fun buildMockSelectorFlow(
        mockFactory: FunSpec.Builder,
        addItems: FunSpec.Builder.() -> Unit,
    ): FunSpec.Builder {
        mockFactory.beginControlFlow("return when ($KMOCK_FACTORY_TYPE_NAME::class)")

        addItems(mockFactory)

        mockFactory.addStatement("else -> $UNKNOWN_INTERFACE")
        mockFactory.endControlFlow()

        return mockFactory
    }

    private fun determineMockTemplate(
        relaxer: Relaxer?,
    ): Pair<String, String> {
        return if (relaxer == null) {
            Pair(
                "%L::class -> %LMock($ARGUMENTS_WITHOUT_RELAXER as %L?) as $KMOCK_FACTORY_TYPE_NAME",
                "%LMock::class -> %LMock($ARGUMENTS_WITHOUT_RELAXER as %L?) as $KMOCK_FACTORY_TYPE_NAME",
            )
        } else {
            Pair(
                "%L::class -> %LMock($ARGUMENTS_WITH_RELAXER as %L?) as $KMOCK_FACTORY_TYPE_NAME",
                "%LMock::class -> %LMock($ARGUMENTS_WITH_RELAXER as %L?) as $KMOCK_FACTORY_TYPE_NAME",
            )
        }
    }

    private fun addMock(
        mockFactory: FunSpec.Builder,
        qualifiedName: String,
        interfaceName: String,
        relaxer: Relaxer?,
    ) {
        val (interfaceInvocationTemplate, mockInvocationTemplate) = determineMockTemplate(relaxer)
        if (allowInterfaces) {
            mockFactory.addStatement(
                interfaceInvocationTemplate,
                qualifiedName,
                interfaceName,
                qualifiedName,
            )
        }

        mockFactory.addStatement(
            mockInvocationTemplate,
            interfaceName,
            interfaceName,
            qualifiedName,
        )
    }

    private fun amendSource(
        mockFactory: FunSpec.Builder,
        templateSource: TemplateSource,
        relaxer: Relaxer?,
    ) {
        val packageName = templateSource.packageName
        val qualifiedName = ensureNotNullClassName(templateSource.template.qualifiedName?.asString())
        val interfaceName = "$packageName.${templateSource.templateName.substringAfterLast('.')}"

        addMock(
            mockFactory = mockFactory,
            qualifiedName = qualifiedName,
            interfaceName = interfaceName,
            relaxer = relaxer,
        )
    }

    private fun determineMultiMockTemplate(
        relaxer: Relaxer?,
    ): String {
        return if (relaxer == null) {
            "%LMock::class -> %LMock<%LMock<*>>($ARGUMENTS_WITHOUT_RELAXER_AND_SPY) as $KMOCK_FACTORY_TYPE_NAME"
        } else {
            "%LMock::class -> %LMock<%LMock<*>>($ARGUMENTS_WITHOUT_SPY) as $KMOCK_FACTORY_TYPE_NAME"
        }
    }

    private fun addMultiMock(
        mockFactory: FunSpec.Builder,
        qualifiedName: String,
        relaxer: Relaxer?,
    ) {
        mockFactory.addStatement(
            determineMultiMockTemplate(relaxer),
            qualifiedName,
            qualifiedName,
            qualifiedName,
        )
    }

    private fun amendMultiSource(
        mockFactory: FunSpec.Builder,
        templateSource: TemplateMultiSource,
        relaxer: Relaxer?,
    ) {
        val mockName = "${templateSource.packageName}.${templateSource.templateName}"

        addMultiMock(
            mockFactory = mockFactory,
            qualifiedName = mockName,
            relaxer = relaxer,
        )
    }

    private fun FunSpec.Builder.buildMockSelectors(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        relaxer: Relaxer?,
    ) {
        buildMockSelectorFlow(this) {
            templateSources.forEach { source ->
                amendSource(
                    mockFactory = this,
                    templateSource = source,
                    relaxer = relaxer,
                )
            }

            templateMultiSources.forEach { source ->
                if (!source.hasGenerics()) {
                    amendMultiSource(
                        mockFactory = this,
                        templateSource = source,
                        relaxer = relaxer,
                    )
                }
            }
        }
    }

    private fun FunSpec.Builder.buildShallowMock() {
        this.addCode(UNKNOWN_INTERFACE)
    }

    override fun buildSharedMockFactory(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        relaxer: Relaxer?,
    ): FunSpec {
        val mockFactory = utils.generateSharedMockFactorySignature(
            mockType = kspyMockType,
            spyType = kspyType,
        )

        if (templateSources.isEmpty() && templateMultiSources.isEmpty()) {
            mockFactory.buildShallowMock()
        } else {
            mockFactory.buildMockSelectors(
                templateSources = templateSources,
                templateMultiSources = templateMultiSources,
                relaxer = relaxer,
            )
        }

        return mockFactory.build()
    }

    private fun fillMockFactory(): FunSpec.Builder {
        val modifier = utils.resolveModifier<Source>()

        return utils.generateKmockSignature(
            type = kmockType,
            hasDefault = !isKmp,
            modifier = modifier,
        ).addCode(factoryInvocation)
    }

    override fun buildKMockFactory(): FunSpec = fillMockFactory().build()

    private fun fillSpyFactory(): FunSpec.Builder {
        val modifier = utils.resolveModifier<Source>()

        return utils.generateKspySignature(
            mockType = kspyMockType,
            spyType = kspyType,
            hasDefault = !isKmp,
            modifier = modifier,
        ).addCode(spyFactoryInvocation)
    }

    override fun buildSpyFactory(): FunSpec = fillSpyFactory().build()

    private companion object {
        private val kmockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME)
        private val kspyType = TypeVariableName(KSPY_FACTORY_TYPE_NAME)
        private val kspyMockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(kspyType))
        private const val ARGUMENTS_WITHOUT_RELAXER_AND_SPY = "$COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT, $UNIT_RELAXER_ARGUMENT = $UNIT_RELAXER_ARGUMENT, $FREEZE_ARGUMENT = $FREEZE_ARGUMENT"
        private const val ARGUMENTS_WITHOUT_SPY = "$COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT, $RELAXER_ARGUMENT = $RELAXER_ARGUMENT, $UNIT_RELAXER_ARGUMENT = $UNIT_RELAXER_ARGUMENT, $FREEZE_ARGUMENT = $FREEZE_ARGUMENT"

        private val factoryInvocation = """
                |return $SHARED_MOCK_FACTORY(
                |   $SPY_ARGUMENT = null,
                |   $COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT,
                |   $RELAXER_ARGUMENT = $RELAXER_ARGUMENT,
                |   $UNIT_RELAXER_ARGUMENT = $UNIT_RELAXER_ARGUMENT,
                |   $FREEZE_ARGUMENT = $FREEZE_ARGUMENT,
                |)
        """.trimMargin()

        private val spyFactoryInvocation = """
                |return $SHARED_MOCK_FACTORY(
                |   $SPY_ARGUMENT = $SPY_ARGUMENT,
                |   $COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT,
                |   $RELAXER_ARGUMENT = false,
                |   $UNIT_RELAXER_ARGUMENT = false,
                |   $FREEZE_ARGUMENT = $FREEZE_ARGUMENT,
                |)
        """.trimMargin()
    }
}
