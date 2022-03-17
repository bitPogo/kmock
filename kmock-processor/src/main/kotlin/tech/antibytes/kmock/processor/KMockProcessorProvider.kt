/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import tech.antibytes.kmock.processor.factory.KMockFactoryEntryPointGenerator
import tech.antibytes.kmock.processor.factory.KMockFactoryGenerator
import tech.antibytes.kmock.processor.factory.KMockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.mock.KMockBuildInFunctionGenerator
import tech.antibytes.kmock.processor.mock.KMockFunctionGenerator
import tech.antibytes.kmock.processor.mock.KMockGenerator
import tech.antibytes.kmock.processor.mock.KMockPropertyGenerator
import tech.antibytes.kmock.processor.mock.KMockRelaxerGenerator

class KMockProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val logger = environment.logger
        val generator = environment.codeGenerator

        val relaxerGenerator = KMockRelaxerGenerator()

        val propertyGenerator = KMockPropertyGenerator(relaxerGenerator)
        val functionGenerator = KMockFunctionGenerator(KMockGenerics, relaxerGenerator)

        return KMockProcessor(
            KMockGenerator(
                logger = logger,
                codeGenerator = generator,
                genericsResolver = KMockGenerics,
                propertyGenerator = propertyGenerator,
                functionGenerator = functionGenerator,
                buildInGenerator = KMockBuildInFunctionGenerator
            ),
            KMockFactoryGenerator(
                logger = logger,
                utils = KMockFactoryGeneratorUtil,
                genericResolver = KMockGenerics,
                codeGenerator = generator
            ),
            KMockFactoryEntryPointGenerator(KMockFactoryGeneratorUtil, generator),
            KMockAggregator(logger, KMockGenerics),
            ProcessorContract.Options(
                isKmp = environment.options["isKmp"] == true.toString(),
                rootPackage = environment.options["rootPackage"]!!
            ),
            SourceFilter(environment.options, logger)
        )
    }
}
