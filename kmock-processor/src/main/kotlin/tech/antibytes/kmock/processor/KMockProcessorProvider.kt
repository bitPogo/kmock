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
import tech.antibytes.kmock.processor.mock.KMockBuildInMethodGenerator
import tech.antibytes.kmock.processor.mock.KMockGenerator
import tech.antibytes.kmock.processor.mock.KMockMethodGenerator
import tech.antibytes.kmock.processor.mock.KMockPropertyGenerator
import tech.antibytes.kmock.processor.mock.KMockRelaxerGenerator
import tech.antibytes.kmock.processor.mock.KMockSpyGenerator
import tech.antibytes.kmock.processor.mock.KmockProxyNameSelector

class KMockProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val logger = environment.logger

        val options = KMockKSPDelegationExtractor.convertOptions(environment.options)

        val codeGenerator = KMockCodeGenerator(
            kspDir = options.kspDir,
            kspGenerator = environment.codeGenerator
        )

        val relaxerGenerator = KMockRelaxerGenerator()
        val nameSelector = KmockProxyNameSelector(
            enableNewOverloadingNames = options.enableNewOverloadingNames,
            customMethodNames = options.customMethodNames,
            useTypePrefixFor = options.useTypePrefixFor,
            uselessPrefixes = options.uselessPrefixes,
        )

        val propertyGenerator = KMockPropertyGenerator(
            spyGenerator = KMockSpyGenerator,
            nameSelector = nameSelector,
            relaxerGenerator = relaxerGenerator,
        )
        val buildInGenerator = KMockBuildInMethodGenerator(
            spyGenerator = KMockSpyGenerator,
            nameSelector = nameSelector,
            relaxerGenerator = relaxerGenerator,
        )
        val methodGenerator = KMockMethodGenerator(
            spyGenerator = KMockSpyGenerator,
            nameSelector = nameSelector,
            genericResolver = KMockGenerics,
            relaxerGenerator = relaxerGenerator,
        )

        val factoryUtils = KMockFactoryGeneratorUtil(
            freezeOnDefault = options.freezeOnDefault,
            genericResolver = KMockGenerics,
        )

        return KMockProcessor(
            codeGenerator = codeGenerator,
            mockGenerator = KMockGenerator(
                logger = logger,
                spyOn = options.spyOn,
                useBuildInProxiesOn = options.useBuildInProxiesOn,
                codeGenerator = codeGenerator,
                genericsResolver = KMockGenerics,
                nameCollector = nameSelector,
                propertyGenerator = propertyGenerator,
                methodGenerator = methodGenerator,
                buildInGenerator = buildInGenerator,
            ),
            factoryGenerator = KMockFactoryGenerator(
                logger = logger,
                allowInterfaces = options.allowInterfaces,
                spyOn = options.spyOn,
                spiesOnly = options.spiesOnly,
                utils = factoryUtils,
                genericResolver = KMockGenerics,
                codeGenerator = codeGenerator,
            ),
            entryPointGenerator = KMockFactoryEntryPointGenerator(
                utils = factoryUtils,
                spyOn = options.spyOn,
                spiesOnly = options.spiesOnly,
                genericResolver = KMockGenerics,
                codeGenerator = codeGenerator,
            ),
            aggregator = KMockAggregator(
                logger = logger,
                knownSourceSets = options.knownSourceSets,
                generics = KMockGenerics,
                aliases = options.aliases,
            ),
            options = options,
            filter = SourceFilter(options.precedences, logger)
        )
    }
}
