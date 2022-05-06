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
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FACTORY_FILE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_CONTRACT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NOOP_COLLECTOR_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNUSED
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryWithGenerics
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryWithoutGenerics
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal class KMockFactoryGenerator(
    private val logger: KSPLogger,
    spyOn: Set<String>,
    private val rootPackage: String,
    private val isKmp: Boolean,
    private val spiesOnly: Boolean,
    private val nonGenericGenerator: MockFactoryWithoutGenerics,
    private val genericGenerator: MockFactoryWithGenerics,
    private val utils: MockFactoryGeneratorUtil,
    private val codeGenerator: CodeGenerator,
) : ProcessorContract.MockFactoryGenerator {
    private val hasSpies = spyOn.isNotEmpty() || spiesOnly

    private fun writeFactoryImplementation(
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        val file = FileSpec.builder(
            rootPackage,
            FACTORY_FILE_NAME
        )
        file.addAnnotation(UNUSED)
        file.addImport(KMOCK_CONTRACT.packageName, KMOCK_CONTRACT.simpleName)

        if (!isKmp) {
            file.addImport(NOOP_COLLECTOR_NAME.packageName, NOOP_COLLECTOR_NAME.simpleName)
        }

        val (regular, generics) = utils.splitInterfacesIntoRegularAndGenerics(templateSources)

        val genericFactories = genericGenerator.buildGenericFactories(
            templateSources = generics,
            relaxer = relaxer
        )

        file.addFunction(
            nonGenericGenerator.buildSharedMockFactory(
                templateSources = regular,
                relaxer = relaxer
            )
        )

        if (!spiesOnly) {
            file.addFunction(
                nonGenericGenerator.buildKMockFactory()
            )
        }

        if (hasSpies) {
            file.addFunction(
                nonGenericGenerator.buildSpyFactory()
            )
        }

        // Shell
        genericFactories.forEach { factories ->
            file.addFunction(factories.shared)

            if (!spiesOnly) {
                file.addFunction(factories.kmock)
            }

            if (hasSpies) {
                file.addFunction(factories.kspy)
            }
        }

        file.build().writeTo(
            codeGenerator = codeGenerator,
            aggregating = false,
            originatingKSFiles = dependencies
        )
    }

    override fun writeFactories(
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
        relaxer: Relaxer?
    ) {
        if (templateSources.isNotEmpty()) { // TODO: Solve multi Rounds in a better way
            writeFactoryImplementation(
                templateSources = templateSources,
                dependencies = dependencies,
                relaxer = relaxer
            )
        }
    }
}
