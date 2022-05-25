/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
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
import tech.antibytes.kmock.processor.ProcessorContract.FactoryMultiBundle
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryMultiInterface
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.SpyContainer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.multi.hasGenerics

internal class KMockFactoryMultiInterfaceGenerator(
    private val isKmp: Boolean,
    private val spyContainer: SpyContainer,
    private val genericResolver: GenericResolver,
    private val utils: MockFactoryGeneratorUtil,
) : MockFactoryMultiInterface {
    private fun buildMockSelectorFlow(
        mockFactory: FunSpec.Builder,
        mockName: String,
        addItems: FunSpec.Builder.() -> Unit,
    ): FunSpec.Builder {
        mockFactory.beginControlFlow("return if ($KMOCK_FACTORY_TYPE_NAME::class == $mockName::class)")
        addItems(mockFactory)
        mockFactory.nextControlFlow("else")
        mockFactory.addCode(UNKNOWN_INTERFACE)
        mockFactory.endControlFlow()

        return mockFactory
    }

    private fun determineMockTemplate(
        relaxer: Relaxer?
    ): String {
        return if (relaxer == null) {
            "%LMock($MULTI_INTERFACE_ARGUMENTS as $KSPY_FACTORY_TYPE_NAME?) as $KMOCK_FACTORY_TYPE_NAME"
        } else {
            "%LMock($ARGUMENTS_WITH_RELAXER as $KSPY_FACTORY_TYPE_NAME?) as $KMOCK_FACTORY_TYPE_NAME"
        }
    }

    private fun addMock(
        mockFactory: FunSpec.Builder,
        qualifiedName: String,
        relaxer: Relaxer?
    ) {
        mockFactory.addStatement(
            determineMockTemplate(relaxer),
            qualifiedName,
        )
    }

    private fun buildMockSelector(
        mockFactory: FunSpec.Builder,
        templateSource: TemplateMultiSource,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val qualifiedName = "${templateSource.packageName}.${templateSource.templateName}"

        return buildMockSelectorFlow(mockFactory, "${qualifiedName}Mock") {
            addMock(
                mockFactory = this,
                qualifiedName = qualifiedName,
                relaxer = relaxer
            )
        }
    }

    private fun fillSpyMockFactory(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        templateSource: TemplateMultiSource,
        generics: List<TypeVariableName>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val mockFactory = utils.generateKspySignature(
            mockType = mockType,
            spyType = spyType,
            generics = generics,
            hasDefault = false,
            modifier = utils.resolveModifier(templateSource)
        )

        return buildMockSelector(
            mockFactory = mockFactory,
            templateSource = templateSource,
            relaxer = relaxer
        )
    }

    private fun resolveBounds(
        templateMultiSource: TemplateMultiSource,
    ): Pair<List<TypeName>, List<TypeVariableName>> {
        return genericResolver.remapTypes(templateMultiSource.templates, templateMultiSource.generics)
    }

    private fun buildSpyMockFactory(
        templateSource: TemplateMultiSource,
        relaxer: Relaxer?
    ): FunSpec {
        val (bounds, _) = resolveBounds(templateSource)

        val spyType = TypeVariableName(
            KSPY_FACTORY_TYPE_NAME,
            bounds = bounds
        )

        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return fillSpyMockFactory(
            mockType = mockType,
            spyType = spyType,
            templateSource = templateSource,
            generics = emptyList(),
            relaxer = relaxer
        ).build()
    }

    private fun fillMockFactory(
        templateSource: TemplateMultiSource,
        type: TypeVariableName,
        generics: List<TypeVariableName>,
    ): FunSpec.Builder {
        val modifier = utils.resolveModifier(templateSource)

        return utils.generateKmockSignature(
            type = type,
            generics = generics,
            hasDefault = !isKmp,
            modifier = modifier
        ).addTypeVariables(generics)
    }

    private fun fillMockFactory(
        templateSource: TemplateMultiSource,
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
        ).addTypeVariables(generics)
    }

    private fun generateTypeArgument(
        bounds: List<TypeName>
    ): String {
        val typeArgumentBuilder = StringBuilder()

        bounds.forEachIndexed { idx, _ ->
            typeArgumentBuilder.append("$TEMPLATE_TYPE_ARGUMENT$idx = $TEMPLATE_TYPE_ARGUMENT$idx,\n")
        }

        return typeArgumentBuilder.toString().trimEnd()
    }

    private fun buildGenericKmockFactory(
        templateSource: TemplateMultiSource,
        bounds: List<TypeName>,
        generics: List<TypeVariableName>,
    ): FunSpec {
        val type = TypeVariableName(
            KMOCK_FACTORY_TYPE_NAME,
            bounds = bounds
        )

        return fillMockFactory(
            templateSource = templateSource,
            generics = generics,
            type = type,
        ).addCode(
            factoryInvocationWithTemplate,
            generateTypeArgument(bounds)
        ).build()
    }

    private fun buildGenericKSpyFactory(
        templateSource: TemplateMultiSource,
        bounds: List<TypeName>,
        generics: List<TypeVariableName>,
    ): FunSpec {
        val spyType = TypeVariableName(
            KSPY_FACTORY_TYPE_NAME,
            bounds = bounds
        )

        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return fillMockFactory(
            templateSource = templateSource,
            mockType = mockType,
            spyType = spyType,
            generics = generics,
        ).addCode(
            spyFactoryInvocationWithTemplate,
            generateTypeArgument(bounds)
        ).build()
    }

    private fun fillSharedMockFactory(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        templateSource: TemplateMultiSource,
        generics: List<TypeVariableName>,
        relaxer: Relaxer?
    ): FunSpec.Builder {
        val mockFactory = utils.generateSharedMockFactorySignature(
            mockType = mockType,
            spyType = spyType,
            generics = generics,
        )

        mockFactory.addTypeVariables(generics)

        return buildMockSelector(
            mockFactory = mockFactory,
            templateSource = templateSource,
            relaxer = relaxer
        )
    }

    private fun buildGenericSharedMockFactory(
        templateSource: TemplateMultiSource,
        bounds: List<TypeName>,
        generics: List<TypeVariableName>,
        relaxer: Relaxer?
    ): FunSpec {
        val spyType = TypeVariableName(
            KSPY_FACTORY_TYPE_NAME,
            bounds = bounds,
        )

        val mockType = TypeVariableName(KMOCK_FACTORY_TYPE_NAME, bounds = listOf(spyType))

        return fillSharedMockFactory(
            mockType = mockType,
            spyType = spyType,
            templateSource = templateSource,
            generics = generics,
            relaxer = relaxer
        ).build()
    }

    private fun TemplateMultiSource.isSpyable(): Boolean {
        return spyContainer.isSpyable(null, this.packageName, this.templateName)
    }

    private fun resolveGenericSpyFactory(
        templateSource: TemplateMultiSource,
        bounds: List<TypeName>,
        generics: List<TypeVariableName>,
    ): FunSpec? {
        return if (templateSource.isSpyable()) {
            buildGenericKSpyFactory(
                templateSource = templateSource,
                bounds = bounds,
                generics = generics,
            )
        } else {
            null
        }
    }

    private fun buildGenericFactories(
        templateSource: TemplateMultiSource,
        relaxer: Relaxer?
    ): FactoryMultiBundle {
        val (bounds, generics) = resolveBounds(templateSource)

        return FactoryMultiBundle(
            shared = buildGenericSharedMockFactory(
                templateSource = templateSource,
                bounds = bounds,
                generics = generics,
                relaxer = relaxer
            ),
            kmock = buildGenericKmockFactory(
                templateSource = templateSource,
                bounds = bounds,
                generics = generics,
            ),
            kspy = resolveGenericSpyFactory(
                templateSource = templateSource,
                bounds = bounds,
                generics = generics,
            ),
        )
    }

    override fun buildFactories(
        templateMultiSources: List<TemplateMultiSource>,
        relaxer: Relaxer?
    ): List<FactoryMultiBundle> {
        val factories: MutableList<FactoryMultiBundle> = mutableListOf()

        templateMultiSources.forEach { source ->
            if (source.isSpyable() && !source.hasGenerics()) {
                factories.add(
                    FactoryMultiBundle(
                        kmock = null,
                        shared = null,
                        kspy = buildSpyMockFactory(source, relaxer)
                    )
                )
            }

            if (source.hasGenerics()) {
                factories.add(buildGenericFactories(source, relaxer))
            }
        }

        return factories
    }

    private companion object {
        private const val MULTI_INTERFACE_ARGUMENTS = "$COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT, $FREEZE_ARGUMENT = $FREEZE_ARGUMENT, $SPY_ARGUMENT = $SPY_ARGUMENT"
        private val factoryInvocationWithTemplate = """
                |return $SHARED_MOCK_FACTORY(
                |   $SPY_ARGUMENT = null,
                |   $COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT,
                |   $RELAXER_ARGUMENT = $RELAXER_ARGUMENT,
                |   $UNIT_RELAXER_ARGUMENT = $UNIT_RELAXER_ARGUMENT,
                |   $FREEZE_ARGUMENT = $FREEZE_ARGUMENT,
                |   %L
                |)
        """.trimMargin()

        private val spyFactoryInvocationWithTemplate = """
                |return $SHARED_MOCK_FACTORY(
                |   $SPY_ARGUMENT = $SPY_ARGUMENT,
                |   $COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT,
                |   $RELAXER_ARGUMENT = false,
                |   $UNIT_RELAXER_ARGUMENT = false,
                |   $FREEZE_ARGUMENT = $FREEZE_ARGUMENT,
                |   %L
                |)
        """.trimMargin()
    }
}
