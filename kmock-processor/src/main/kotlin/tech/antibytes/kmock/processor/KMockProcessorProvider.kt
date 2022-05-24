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
import tech.antibytes.kmock.processor.aggregation.KMockMultiSourceAggregator
import tech.antibytes.kmock.processor.aggregation.KMockRelaxationAggregator
import tech.antibytes.kmock.processor.aggregation.KMockSingleSourceAggregator
import tech.antibytes.kmock.processor.factory.KMockFactoryEntryPointGenerator
import tech.antibytes.kmock.processor.factory.KMockFactoryGenerator
import tech.antibytes.kmock.processor.factory.KMockFactoryGeneratorUtil
import tech.antibytes.kmock.processor.factory.KMockFactoryMultiInterfaceGenerator
import tech.antibytes.kmock.processor.factory.KMockFactoryWithGenerics
import tech.antibytes.kmock.processor.factory.KMockFactoryWithoutGenerics
import tech.antibytes.kmock.processor.factory.NoopFactoryGenerator
import tech.antibytes.kmock.processor.mock.KMockBuildInMethodGenerator
import tech.antibytes.kmock.processor.mock.KMockGenerator
import tech.antibytes.kmock.processor.mock.KMockMethodGenerator
import tech.antibytes.kmock.processor.mock.KMockNonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.mock.KMockPropertyGenerator
import tech.antibytes.kmock.processor.mock.KMockProxyAccessMethodGenerator
import tech.antibytes.kmock.processor.mock.KMockProxyNameSelector
import tech.antibytes.kmock.processor.mock.KMockReceiverGenerator
import tech.antibytes.kmock.processor.mock.KMockRelaxerGenerator
import tech.antibytes.kmock.processor.mock.KMockSpyGenerator
import tech.antibytes.kmock.processor.mock.MethodGeneratorHelper
import tech.antibytes.kmock.processor.multi.KMockMultiInterfaceBinder
import tech.antibytes.kmock.processor.multi.KMockParentFinder
import tech.antibytes.kmock.processor.utils.AnnotationFilter
import tech.antibytes.kmock.processor.utils.SourceFilter
import tech.antibytes.kmock.processor.utils.SourceSetValidator
import tech.antibytes.kmock.processor.utils.SpyContainer

class KMockProcessorProvider(
    private val isUnderCompilerTest: Boolean = false
) : SymbolProcessorProvider {
    private fun determineFactoryGenerator(
        options: Options,
        logger: KSPLogger,
        spyContainer: SpyContainer,
        codeGenerator: KmpCodeGenerator
    ): Pair<MockFactoryGenerator, MockFactoryEntryPointGenerator> {
        return if (options.disableFactories) {
            Pair(NoopFactoryGenerator, NoopFactoryGenerator)
        } else {
            val factoryUtils = KMockFactoryGeneratorUtil(
                isKmp = options.isKmp,
                freezeOnDefault = options.freezeOnDefault,
                genericResolver = KMockGenerics,
            )

            Pair(
                KMockFactoryGenerator(
                    logger = logger,
                    isKmp = options.isKmp,
                    rootPackage = options.rootPackage,
                    nonGenericGenerator = KMockFactoryWithoutGenerics(
                        isKmp = options.isKmp,
                        allowInterfaces = options.allowInterfaces,
                        utils = factoryUtils,
                    ),
                    genericGenerator = KMockFactoryWithGenerics(
                        isKmp = options.isKmp,
                        spyContainer = spyContainer,
                        allowInterfaces = options.allowInterfaces,
                        utils = factoryUtils,
                        genericResolver = KMockGenerics,
                    ),
                    multiInterfaceGenerator = KMockFactoryMultiInterfaceGenerator(
                        isKmp = options.isKmp,
                        spyContainer = spyContainer,
                        utils = factoryUtils,
                        genericResolver = KMockGenerics,
                    ),
                    spyContainer = spyContainer,
                    spiesOnly = options.spiesOnly,
                    utils = factoryUtils,
                    codeGenerator = codeGenerator,
                ),
                KMockFactoryEntryPointGenerator(
                    isKmp = options.isKmp,
                    rootPackage = options.rootPackage,
                    utils = factoryUtils,
                    spyContainer = spyContainer,
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

        val spyContainer = SpyContainer(
            spyOn = options.spyOn,
            spyAll = options.spyAll,
            spiesOnly = options.spiesOnly,
        )

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
        val nameSelector = KMockProxyNameSelector(
            enableNewOverloadingNames = options.enableNewOverloadingNames,
            enableFineGrainedNames = options.enableFineGrainedNames,
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
        val methodeGeneratorHelper = MethodGeneratorHelper(
            genericResolver = KMockGenerics,
        )
        val methodGenerator = KMockMethodGenerator(
            utils = methodeGeneratorHelper,
            nameSelector = nameSelector,
            nonIntrusiveInvocationGenerator = nonIntrusiveInvocationGenerator,
            genericResolver = KMockGenerics,
        )
        val receiverGenerator = KMockReceiverGenerator(
            utils = methodeGeneratorHelper,
            nameSelector = nameSelector,
            nonIntrusiveInvocationGenerator = nonIntrusiveInvocationGenerator,
            genericResolver = KMockGenerics,
        )

        val (factoryGenerator, entryPointGenerator) = determineFactoryGenerator(
            options = options,
            logger = logger,
            spyContainer = spyContainer,
            codeGenerator = codeGenerator,
        )

        return KMockProcessor(
            logger = logger,
            isUnderCompilerTest = isUnderCompilerTest,
            isKmp = options.isKmp,
            codeGenerator = codeGenerator,
            interfaceGenerator = KMockMultiInterfaceBinder(
                logger = logger,
                rootPackage = options.rootPackage,
                genericResolver = KMockGenerics,
                codeGenerator = codeGenerator,
            ),
            mockGenerator = KMockGenerator(
                logger = logger,
                enableProxyAccessMethodGenerator = options.allowExperimentalProxyAccess,
                spyContainer = spyContainer,
                useBuildInProxiesOn = options.useBuildInProxiesOn,
                codeGenerator = codeGenerator,
                genericsResolver = KMockGenerics,
                nameCollector = nameSelector,
                parentFinder = KMockParentFinder,
                propertyGenerator = propertyGenerator,
                methodGenerator = methodGenerator,
                buildInGenerator = buildInGenerator,
                receiverGenerator = receiverGenerator,
                proxyAccessMethodGeneratorFactory = KMockProxyAccessMethodGenerator
            ),
            factoryGenerator = factoryGenerator,
            entryPointGenerator = entryPointGenerator,
            multiSourceAggregator = KMockMultiSourceAggregator.getInstance(
                logger = logger,
                rootPackage = options.rootPackage,
                annotationFilter = annotationFilter,
                sourceSetValidator = sourceSetValidator,
                generics = KMockGenerics,
                customAnnotations = options.customAnnotations,
                aliases = options.aliases,
            ),
            singleSourceAggregator = KMockSingleSourceAggregator.getInstance(
                logger = logger,
                rootPackage = options.rootPackage,
                annotationFilter = annotationFilter,
                sourceSetValidator = sourceSetValidator,
                generics = KMockGenerics,
                customAnnotations = options.customAnnotations,
                aliases = options.aliases,
            ),
            relaxationAggregator = KMockRelaxationAggregator(logger),
            filter = SourceFilter(
                dependencies = options.dependencies,
                logger = logger
            )
        )
    }
}
