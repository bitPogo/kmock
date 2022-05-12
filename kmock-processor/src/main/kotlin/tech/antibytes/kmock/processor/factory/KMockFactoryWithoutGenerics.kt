/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeVariableName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SHARED_MOCK_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryWithoutGenerics
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
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
        relaxer: Relaxer?
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
        relaxer: Relaxer?
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
        relaxer: Relaxer?
    ): String {
        return if (relaxer == null) {
            "%LMock::class -> %LMock<%LMock<*>>(verifier = verifier, relaxUnitFun = relaxUnitFun, freeze = freeze) as $KMOCK_FACTORY_TYPE_NAME"
        } else {
            "%LMock::class -> %LMock<%LMock<*>>(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze) as $KMOCK_FACTORY_TYPE_NAME"
        }
    }

    private fun addMultiMock(
        mockFactory: FunSpec.Builder,
        qualifiedName: String,
        relaxer: Relaxer?
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
        relaxer: Relaxer?
    ) {
        val mockName = "${templateSource.packageName}.${templateSource.templateName}"

        addMultiMock(
            mockFactory = mockFactory,
            qualifiedName = mockName,
            relaxer = relaxer
        )
    }

    private fun FunSpec.Builder.buildMockSelectors(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        relaxer: Relaxer?
    ) {
        buildMockSelectorFlow(this) {
            templateSources.forEach { source ->
                amendSource(
                    mockFactory = this,
                    templateSource = source,
                    relaxer = relaxer
                )
            }

            templateMultiSources.forEach { source ->
                if (!source.hasGenerics()) {
                    amendMultiSource(
                        mockFactory = this,
                        templateSource = source,
                        relaxer = relaxer
                    )
                }
            }
        }
    }

    private fun FunSpec.Builder.buildShallowMock() {
        this.addCode(
            "throw RuntimeException(\"Unknown Interface \${$KMOCK_FACTORY_TYPE_NAME::class.simpleName}.\")"
        )
    }

    override fun buildSharedMockFactory(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        relaxer: Relaxer?
    ): FunSpec {
        val mockFactory = utils.generateSharedMockFactorySignature(
            mockType = kspyMockType,
            spyType = kspyType,
            generics = emptyList(),
        )

        if (templateSources.isEmpty() && templateMultiSources.isEmpty()) {
            mockFactory.buildShallowMock()
        } else {
            mockFactory.buildMockSelectors(
                templateSources = templateSources,
                templateMultiSources = templateMultiSources,
                relaxer = relaxer
            )
        }

        return mockFactory.build()
    }

    private fun fillMockFactory(): FunSpec.Builder {
        val modifier = utils.resolveModifier()

        return utils.generateKmockSignature(
            type = kmockType,
            generics = emptyList(),
            hasDefault = !isKmp,
            modifier = modifier
        ).addCode(factoryInvocation)
    }

    override fun buildKMockFactory(): FunSpec = fillMockFactory().build()

    private fun fillSpyFactory(): FunSpec.Builder {
        val modifier = utils.resolveModifier()

        return utils.generateKspySignature(
            mockType = kspyMockType,
            spyType = kspyType,
            generics = emptyList(),
            hasDefault = !isKmp,
            modifier = modifier
        ).addCode(spyFactoryInvocation)
    }

    override fun buildSpyFactory(): FunSpec = fillSpyFactory().build()

    private companion object {
        private val kmockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME)
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
