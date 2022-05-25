/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
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
import tech.antibytes.kmock.processor.ProcessorContract.Companion.TEMPLATE_TYPE_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNIT_RELAXER_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNKNOWN_INTERFACE
import tech.antibytes.kmock.processor.ProcessorContract.FactoryBundle
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryWithGenerics
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.SpyContainer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.utils.ensureNotNullClassName

internal class KMockFactoryWithGenerics(
    private val isKmp: Boolean,
    private val spyContainer: SpyContainer,
    private val allowInterfaces: Boolean,
    private val utils: MockFactoryGeneratorUtil,
    private val genericResolver: GenericResolver,
) : MockFactoryWithGenerics {
    private fun fillMockFactory(
        templateSource: TemplateSource,
        type: TypeVariableName,
        generics: List<TypeVariableName>,
    ): FunSpec.Builder {
        val modifier = utils.resolveModifier(templateSource)

        return utils.generateKmockSignature(
            type = type,
            generics = generics,
            hasDefault = !isKmp,
            modifier = modifier
        ).addCode(factoryInvocationWithTemplate)
    }

    private fun fillSpyFactory(
        templateSource: TemplateSource,
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        generics: List<TypeVariableName>,
    ): FunSpec.Builder {
        val modifier = utils.resolveModifier(templateSource)

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
            templateSource = templateSource,
            generics = generics,
            type = type,
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
            templateSource = templateSource,
            mockType = mockType,
            spyType = spyType,
            generics = generics,
        ).build()
    }

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
        relaxer: Relaxer?
    ): Pair<String, String> {
        return if (relaxer == null) {
            Pair(
                "%L::class -> %LMock%L($ARGUMENTS_WITHOUT_RELAXER as %L%L?) as $KMOCK_FACTORY_TYPE_NAME",
                "%LMock::class -> %LMock%L($ARGUMENTS_WITHOUT_RELAXER as %L%L?) as $KMOCK_FACTORY_TYPE_NAME",
            )
        } else {
            Pair(
                "%L::class -> %LMock%L($ARGUMENTS_WITH_RELAXER as %L%L?) as $KMOCK_FACTORY_TYPE_NAME",
                "%LMock::class -> %LMock%L($ARGUMENTS_WITH_RELAXER as %L%L?) as $KMOCK_FACTORY_TYPE_NAME",
            )
        }
    }

    private fun addMock(
        mockFactory: FunSpec.Builder,
        qualifiedName: String,
        interfaceName: String,
        typeInfo: String,
        relaxer: Relaxer?
    ) {
        val (interfaceInvocationTemplate, mockInvocationTemplate) = determineMockTemplate(relaxer)
        if (allowInterfaces) {
            mockFactory.addStatement(
                interfaceInvocationTemplate,
                qualifiedName,
                interfaceName,
                typeInfo,
                qualifiedName,
                typeInfo,
            )
        }

        mockFactory.addStatement(
            mockInvocationTemplate,
            interfaceName,
            interfaceName,
            typeInfo,
            qualifiedName,
            typeInfo,
        )
    }

    private fun amendSource(
        mockFactory: FunSpec.Builder,
        typeInfo: String,
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
            typeInfo = typeInfo,
            relaxer = relaxer,
        )
    }

    private fun buildSharedMockSelector(
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

    private fun fillSharedMockFactory(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        templateSource: TemplateSource,
        generics: List<TypeVariableName>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val mockFactory = utils.generateSharedMockFactorySignature(
            mockType = mockType,
            spyType = spyType,
            generics = generics,
        )

        return buildSharedMockSelector(
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

        return fillSharedMockFactory(
            mockType = mockType,
            spyType = spyType,
            templateSource = templateSource,
            generics = genericTypes,
            relaxer = relaxer
        ).build()
    }

    private fun resolveKSpyFactory(
        source: TemplateSource
    ): FunSpec? {
        return if (spyContainer.isSpyable(source.template, source.packageName, source.templateName)) {
            buildGenericSpyFactory(source)
        } else {
            null
        }
    }

    override fun buildGenericFactories(
        templateSources: List<TemplateSource>,
        relaxer: Relaxer?
    ): List<FactoryBundle> {
        val factories: MutableList<FactoryBundle> = mutableListOf()

        templateSources.forEach { source ->
            val kmock = buildGenericMockFactory(source)

            val kspy = resolveKSpyFactory(source)

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
                |   $SPY_ARGUMENT = null,
                |   $COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT,
                |   $RELAXER_ARGUMENT = $RELAXER_ARGUMENT,
                |   $UNIT_RELAXER_ARGUMENT = $UNIT_RELAXER_ARGUMENT,
                |   $FREEZE_ARGUMENT = $FREEZE_ARGUMENT,
                |   $TEMPLATE_TYPE_ARGUMENT = $TEMPLATE_TYPE_ARGUMENT,
                |)
        """.trimMargin()

        private val spyFactoryInvocationWithTemplate = """
                |return $SHARED_MOCK_FACTORY(
                |   $SPY_ARGUMENT = $SPY_ARGUMENT,
                |   $COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT,
                |   $RELAXER_ARGUMENT = false,
                |   $UNIT_RELAXER_ARGUMENT = false,
                |   $FREEZE_ARGUMENT = $FREEZE_ARGUMENT,
                |   $TEMPLATE_TYPE_ARGUMENT = $TEMPLATE_TYPE_ARGUMENT,
                |)
        """.trimMargin()
    }
}
