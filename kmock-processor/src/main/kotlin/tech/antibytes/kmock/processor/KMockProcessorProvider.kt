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
import tech.antibytes.kmock.processor.mock.KmockProxyNameSelector

class KMockProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val logger = environment.logger

        val options = KMockKSPDelegationExtractor.convertOptions(environment.options)

        val codeGenerator = KMockCodeGenerator(
            kspDir = options.kspDir,
            kspGenerator = environment.codeGenerator
        )
        val genericResolver = KMockGenerics(options.allowedRecursiveTypes)
        val relaxerGenerator = KMockRelaxerGenerator()
        val nameSelector = KmockProxyNameSelector(
            enableNewOverloadingNames = options.enableNewOverloadingNames,
            useTypePrefixFor = options.useTypePrefixFor,
            uselessPrefixes = options.uselessPrefixes,
        )

        val propertyGenerator = KMockPropertyGenerator(
            nameSelector = nameSelector,
            relaxerGenerator = relaxerGenerator
        )
        val buildInGenerator = KMockBuildInMethodGenerator(
            nameSelector = nameSelector,
        )
        val methodGenerator = KMockMethodGenerator(
            allowedRecursiveTypes = options.allowedRecursiveTypes,
            nameSelector = nameSelector,
            genericResolver = genericResolver,
            relaxerGenerator = relaxerGenerator
        )

        val factoryUtils = KMockFactoryGeneratorUtil(
            freezeOnDefault = options.freezeOnDefault,
            genericResolver = genericResolver,
        )

        return KMockProcessor(
            codeGenerator = codeGenerator,
            mockGenerator = KMockGenerator(
                logger = logger,
                spyOn = options.spyOn,
                useBuildInProxiesOn = options.useBuildInProxiesOn,
                codeGenerator = codeGenerator,
                genericsResolver = genericResolver,
                nameCollector = nameSelector,
                propertyGenerator = propertyGenerator,
                methodGenerator = methodGenerator,
                buildInGenerator = buildInGenerator
            ),
            factoryGenerator = KMockFactoryGenerator(
                logger = logger,
                allowInterfacesOnKmock = options.allowInterfacesOnKmock,
                allowInterfacesOnKspy = options.allowInterfacesOnKspy,
                spyOn = options.spyOn,
                spiesOnly = options.spiesOnly,
                utils = factoryUtils,
                genericResolver = genericResolver,
                codeGenerator = codeGenerator
            ),
            entryPointGenerator = KMockFactoryEntryPointGenerator(
                utils = factoryUtils,
                spiesOnly = options.spiesOnly,
                genericResolver = genericResolver,
                codeGenerator = codeGenerator
            ),
            aggregator = KMockAggregator(
                logger = logger,
                knownSourceSets = options.knownSourceSets,
                generics = genericResolver,
                aliases = options.aliases
            ),
            options = options,
            filter = SourceFilter(options.precedences, logger)
        )
    }
}
