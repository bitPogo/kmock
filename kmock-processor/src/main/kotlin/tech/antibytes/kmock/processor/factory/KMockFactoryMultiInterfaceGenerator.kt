/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.toClassName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryMultiInterface
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.SpyContainer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource

internal class KMockFactoryMultiInterfaceGenerator(
    private val spyContainer: SpyContainer,
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
        mockFactory.addCode(
            "throw RuntimeException(\"Unknown Interface \${$KMOCK_FACTORY_TYPE_NAME::class.simpleName}.\")"
        )
        mockFactory.endControlFlow()

        return mockFactory
    }

    private fun determineMockTemplate(
        relaxer: Relaxer?
    ): String {
        return if (relaxer == null) {
            "%LMock(verifier = verifier, freeze = freeze, spyOn = spyOn as $KSPY_FACTORY_TYPE_NAME?) as $KMOCK_FACTORY_TYPE_NAME"
        } else {
            "%LMock(verifier = verifier, relaxed = relaxed, relaxUnitFun = relaxUnitFun, freeze = freeze, spyOn = spyOn as $KSPY_FACTORY_TYPE_NAME?) as $KMOCK_FACTORY_TYPE_NAME"
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
            modifier = utils.resolveModifier()
        )

        return buildMockSelector(
            mockFactory = mockFactory,
            templateSource = templateSource,
            relaxer = relaxer
        )
    }

    private fun resolveBounds(
        templateMultiSource: TemplateMultiSource,
    ): List<TypeName> = templateMultiSource.templates.map { source -> source.toClassName() }

    private fun buildSpyMockFactory(
        templateSource: TemplateMultiSource,
        relaxer: Relaxer?
    ): FunSpec {
        val spyType = TypeVariableName(
            KSPY_FACTORY_TYPE_NAME,
            bounds = resolveBounds(templateSource)
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

    override fun buildSpyFactory(
        templateMultiSources: List<TemplateMultiSource>,
        relaxer: Relaxer?
    ): List<FunSpec> {
        val factories: MutableList<FunSpec> = mutableListOf()

        templateMultiSources.forEach { source ->
            if (spyContainer.isSpyable(null, source.packageName, source.templateName)) {
                factories.add(buildSpyMockFactory(source, relaxer))
            }
        }

        return factories
    }
}
