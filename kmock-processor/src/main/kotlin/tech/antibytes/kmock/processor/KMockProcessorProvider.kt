/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import tech.antibytes.kmock.processor.ProcessorContract.KmpCodeGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryEntryPointGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGenerator
import tech.antibytes.kmock.processor.ProcessorContract.Options
import tech.antibytes.kmock.processor.factory.KMockFactoryEntryPointGenerator
import tech.antibytes.kmock.processor.factory.KMockFactoryGenerator
import tech.antibytes.kmock.processor.factory.KMockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.factory.KMockFactoryWithGenerics
import tech.antibytes.kmock.processor.factory.KMockFactoryWithoutGenerics
import tech.antibytes.kmock.processor.factory.NoopFactoryGenerator
import tech.antibytes.kmock.processor.mock.KMockBuildInMethodGenerator
import tech.antibytes.kmock.processor.mock.KMockGenerator
import tech.antibytes.kmock.processor.mock.KMockMethodGenerator
import tech.antibytes.kmock.processor.mock.KMockNonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.mock.KMockPropertyGenerator
import tech.antibytes.kmock.processor.mock.KMockRelaxerGenerator
import tech.antibytes.kmock.processor.mock.KMockSpyGenerator
import tech.antibytes.kmock.processor.mock.KmockProxyNameSelector
import tech.antibytes.kmock.processor.utils.AnnotationFilter
import tech.antibytes.kmock.processor.utils.SourceFilter
import tech.antibytes.kmock.processor.utils.SourceSetValidator

class KMockProcessorProvider : SymbolProcessorProvider {
    private fun determineFactoryGenerator(
        options: Options,
        logger: KSPLogger,
        codeGenerator: KmpCodeGenerator
    ): Pair<MockFactoryGenerator, MockFactoryEntryPointGenerator> {
        return if (options.disableFactories) {
            Pair(NoopFactoryGenerator, NoopFactoryGenerator)
        } else {
            val factoryUtils = KMockFactoryGeneratorUtil(
                freezeOnDefault = options.freezeOnDefault,
                genericResolver = KMockGenerics,
            )

            Pair(
                KMockFactoryGenerator(
                    logger = logger,
                    isKmp = options.isKmp,
                    rootPackage = options.rootPackage,
                    allowInterfaces = options.allowInterfaces,
                    nonGenericGenerator = KMockFactoryWithoutGenerics(
                        isKmp = options.isKmp,
                        allowInterfaces = options.allowInterfaces,
                        utils = factoryUtils,
                    ),
                    genericGenerator = KMockFactoryWithGenerics(
                        isKmp = options.isKmp,
                        allowInterfaces = options.allowInterfaces,
                        utils = factoryUtils,
                        genericResolver = KMockGenerics,
                    ),
                    spyOn = options.spyOn,
                    spiesOnly = options.spiesOnly,
                    utils = factoryUtils,
                    genericResolver = KMockGenerics,
                    codeGenerator = codeGenerator,
                ),
                KMockFactoryEntryPointGenerator(
                    isKmp = options.isKmp,
                    rootPackage = options.rootPackage,
                    utils = factoryUtils,
                    spyOn = options.spyOn,
                    spiesOnly = options.spiesOnly,
                    genericResolver = KMockGenerics,
                    codeGenerator = codeGenerator,
                )
            )
        }
    }

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val logger = environment.logger

        val options = KMockOptionExtractor.convertOptions(environment.options)

        val codeGenerator = KMockCodeGenerator(
            kspDir = options.kspDir,
            kspGenerator = environment.codeGenerator
        )

        val sourceSetValidator = SourceSetValidator(
            logger = logger,
            knownSharedSourceSets = options.knownSharedSourceSets
        )

        val annotationFilter = AnnotationFilter(
            logger = logger,
            knownSharedSourceSets = options.knownSharedSourceSets
        )

        val relaxerGenerator = KMockRelaxerGenerator()
        val nameSelector = KmockProxyNameSelector(
            enableNewOverloadingNames = options.enableNewOverloadingNames,
            customMethodNames = options.customMethodNames,
            useTypePrefixFor = options.useTypePrefixFor,
            uselessPrefixes = options.uselessPrefixes,
        )

        val nonIntrusiveInvocationGenerator = KMockNonIntrusiveInvocationGenerator(
            spyGenerator = KMockSpyGenerator,
            relaxerGenerator = relaxerGenerator,
        )

        val propertyGenerator = KMockPropertyGenerator(
            nameSelector = nameSelector,
            nonIntrusiveInvocationGenerator = nonIntrusiveInvocationGenerator,
        )
        val buildInGenerator = KMockBuildInMethodGenerator(
            nameSelector = nameSelector,
            nonIntrusiveInvocationGenerator = nonIntrusiveInvocationGenerator,
        )
        val methodGenerator = KMockMethodGenerator(
            nameSelector = nameSelector,
            nonIntrusiveInvocationGenerator = nonIntrusiveInvocationGenerator,
            genericResolver = KMockGenerics,
        )

        val (factoryGenerator, entryPointGenerator) = determineFactoryGenerator(
            options = options,
            logger = logger,
            codeGenerator = codeGenerator
        )

        return KMockProcessor(
            isKmp = options.isKmp,
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
            factoryGenerator = factoryGenerator,
            entryPointGenerator = entryPointGenerator,
            aggregator = KMockAggregator.getInstance(
                logger = logger,
                annotationFilter = annotationFilter,
                sourceSetValidator = sourceSetValidator,
                generics = KMockGenerics,
                customAnnotations = options.customAnnotations,
                aliases = options.aliases,
            ),
            filter = SourceFilter(options.precedences, logger)
        )
    }
}
