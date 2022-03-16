/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

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
                generics = KMockGenerics,
                propertyGenerator = propertyGenerator,
                functionGenerator = functionGenerator,
                buildInGenerator = KMockBuildInFunctionGenerator
            ),
            KMockFactoryGenerator(logger, generator),
            KMockCommonEntryPointGenerator(generator),
            KMockAggregator(logger, KMockGenerics),
            ProcessorContract.Options(
                isKmp = environment.options["isKmp"] == true.toString(),
                rootPackage = environment.options["rootPackage"]!!
            ),
            SourceFilter(environment.options, logger)
        )
    }
}
