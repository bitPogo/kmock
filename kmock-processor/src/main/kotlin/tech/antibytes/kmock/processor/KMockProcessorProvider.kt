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

        val options = KMockKSPDelegationExtractor.convertOptions(environment.options)

        val genericResolver = KMockGenerics(options.allowedRecursiveTypes)
        val relaxerGenerator = KMockRelaxerGenerator()

        val propertyGenerator = KMockPropertyGenerator(relaxerGenerator)
        val functionGenerator = KMockFunctionGenerator(
            allowedRecursiveTypes = options.allowedRecursiveTypes,
            uselessPrefixes = options.uselessPrefixes,
            genericResolver = genericResolver,
            relaxerGenerator = relaxerGenerator
        )

        val factoryUtils = KMockFactoryGeneratorUtil(genericResolver)

        return KMockProcessor(
            mockGenerator = KMockGenerator(
                logger = logger,
                useBuildInProxiesOn = options.useBuildInProxiesOn,
                codeGenerator = generator,
                genericsResolver = genericResolver,
                propertyGenerator = propertyGenerator,
                functionGenerator = functionGenerator,
                buildInGenerator = KMockBuildInFunctionGenerator
            ),
            factoryGenerator = KMockFactoryGenerator(
                logger = logger,
                utils = factoryUtils,
                genericResolver = genericResolver,
                codeGenerator = generator
            ),
            entryPointGenerator = KMockFactoryEntryPointGenerator(
                utils = factoryUtils,
                genericResolver = genericResolver,
                codeGenerator = generator
            ),
            aggregator = KMockAggregator(
                logger = logger,
                generics = genericResolver,
                aliases = options.aliases
            ),
            options = options,
            filter = SourceFilter(options.precedences, logger)
        )
    }
}
