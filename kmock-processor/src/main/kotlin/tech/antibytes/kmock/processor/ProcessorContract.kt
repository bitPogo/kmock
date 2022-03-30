/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.AsyncFunProxy
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.SyncFunProxy
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal interface ProcessorContract {
    data class Relaxer(
        val packageName: String,
        val functionName: String
    )

    data class Options(
        val kspDir: String,
        val isKmp: Boolean,
        val allowInterfacesOnKmock: Boolean,
        val allowInterfacesOnKspy: Boolean,
        val spiesOnly: Boolean,
        val rootPackage: String,
        val knownSourceSets: Set<String>,
        val precedences: Map<String, Int>,
        val aliases: Map<String, String>,
        val allowedRecursiveTypes: Set<String>,
        val useBuildInProxiesOn: Set<String>,
        val spyOn: Set<String>,
        val uselessPrefixes: Set<String>,
    )

    fun interface KSPDelegationExtractor {
        fun convertOptions(kspRawOptions: Map<String, String>): Options
    }

    interface Aggregator {
        fun extractInterfaces(annotated: Sequence<KSAnnotated>): Aggregated
        fun extractRelaxer(annotated: Sequence<KSAnnotated>): Relaxer?
    }

    data class TemplateSource(
        val indicator: String,
        val template: KSClassDeclaration,
        val alias: String?,
        val generics: Map<String, List<KSTypeReference>>?
    )

    data class Aggregated(
        val illFormed: List<KSAnnotated>,
        val extractedTemplates: List<TemplateSource>,
        val dependencies: List<KSFile>
    )

    interface SourceFilter {
        fun filter(
            templateSources: List<TemplateSource>,
            filteredBy: List<TemplateSource>
        ): List<TemplateSource>

        fun filterSharedSources(
            templateSources: List<TemplateSource>
        ): List<TemplateSource>
    }

    data class GenericDeclaration(
        val types: List<TypeName>,
        val recursive: Boolean,
        val nullable: Boolean,
        val castReturnType: Boolean = false
    )

    interface GenericResolver {
        fun extractGenerics(
            template: KSDeclaration,
            resolver: TypeParameterResolver
        ): Map<String, List<KSTypeReference>>?

        fun resolveMockClassType(
            template: KSClassDeclaration,
            resolver: TypeParameterResolver
        ): TypeName

        fun resolveKMockFactoryType(
            name: String,
            templateSource: TemplateSource,
        ): TypeVariableName

        fun mapDeclaredGenerics(
            generics: Map<String, List<KSTypeReference>>,
            typeResolver: TypeParameterResolver
        ): List<TypeVariableName>

        fun mapProxyGenerics(
            generics: Map<String, List<KSTypeReference>>,
            typeResolver: TypeParameterResolver
        ): Map<String, GenericDeclaration>
    }

    interface KmpCodeGenerator : CodeGenerator {
        fun setOneTimeSourceSet(sourceSet: String)
        fun closeFiles()
    }

    interface RelaxerGenerator {
        fun buildRelaxers(relaxer: Relaxer?, useUnitFunRelaxer: Boolean): String
    }

    interface PropertyGenerator {
        fun buildPropertyBundle(
            qualifier: String,
            ksProperty: KSPropertyDeclaration,
            typeResolver: TypeParameterResolver,
            enableSpy: Boolean,
            relaxer: Relaxer?,
        ): Pair<PropertySpec, PropertySpec>
    }

    interface MethodGenerator {
        fun buildMethodBundle(
            qualifier: String,
            ksFunction: KSFunctionDeclaration,
            typeResolver: TypeParameterResolver,
            existingProxies: Set<String>,
            enableSpy: Boolean,
            relaxer: Relaxer?,
        ): Pair<PropertySpec, FunSpec>
    }

    interface BuildInMethodGenerator {
        fun buildMethodBundles(
            mockName: String,
            qualifier: String,
            existingProxies: Set<String>,
            amountOfGenerics: Int,
        ): Pair<List<PropertySpec>, List<FunSpec>>
    }

    interface MockGenerator {
        fun writePlatformMocks(
            templateSources: List<TemplateSource>,
            dependencies: List<KSFile>,
            relaxer: Relaxer?
        )

        fun writeSharedMocks(
            templateSources: List<TemplateSource>,
            dependencies: List<KSFile>,
            relaxer: Relaxer?
        )

        fun writeCommonMocks(
            templateSources: List<TemplateSource>,
            dependencies: List<KSFile>,
            relaxer: Relaxer?
        )
    }

    interface MockFactoryGeneratorUtil {
        fun generateKmockSignature(
            type: TypeVariableName,
            generics: List<TypeVariableName>,
            hasDefault: Boolean,
            modifier: KModifier?
        ): FunSpec.Builder

        fun generateKspySignature(
            mockType: TypeVariableName,
            spyType: TypeVariableName,
            generics: List<TypeVariableName>,
            hasDefault: Boolean,
            modifier: KModifier?
        ): FunSpec.Builder

        fun splitInterfacesIntoRegularAndGenerics(
            templateSources: List<TemplateSource>
        ): Pair<List<TemplateSource>, List<TemplateSource>>

        fun resolveGenerics(templateSource: TemplateSource): List<TypeVariableName>
    }

    interface MockFactoryGenerator {
        fun writeFactories(
            options: Options,
            templateSources: List<TemplateSource>,
            dependencies: List<KSFile>,
            relaxer: Relaxer?,
        )
    }

    interface MockFactoryEntryPointGenerator {
        fun generateCommon(
            options: Options,
            templateSources: List<TemplateSource>,
        )

        fun generateShared(
            options: Options,
            templateSources: List<TemplateSource>,
        )
    }

    companion object {
        const val KMOCK_FACTORY_TYPE_NAME = "Mock"
        const val KSPY_FACTORY_TYPE_NAME = "SpyOn"
        val ANNOTATION_NAME: String = Mock::class.java.canonicalName
        val ANNOTATION_COMMON_NAME: String = MockCommon::class.java.canonicalName
        val ANNOTATION_SHARED_NAME: String = MockShared::class.java.canonicalName
        val COLLECTOR_NAME = ClassName(
            Collector::class.java.packageName,
            "KMockContract.${Collector::class.java.simpleName}"
        )
        val KMOCK_CONTRACT = ClassName(
            KMockContract::class.java.packageName,
            KMockContract::class.java.simpleName
        )

        val UNIT_RELAXER = ClassName(
            NoopCollector::class.java.packageName,
            "relaxVoidFunction"
        )

        val NOOP_COLLECTOR_NAME = ClassName(
            NoopCollector::class.java.packageName,
            NoopCollector::class.java.simpleName
        )

        val PROXY_FACTORY_NAME = ClassName(
            ProxyFactory::class.java.packageName,
            ProxyFactory::class.java.simpleName
        )

        val SYNC_FUN_NAME = ClassName(
            KMockContract::class.java.packageName,
            "KMockContract.${SyncFunProxy::class.java.simpleName}"
        )
        val ASYNC_FUN_NAME = ClassName(
            KMockContract::class.java.packageName,
            "KMockContract.${AsyncFunProxy::class.java.simpleName}"
        )
        val PROP_NAME = ClassName(
            KMockContract::class.java.packageName,
            "KMockContract.${PropertyProxy::class.java.simpleName}"
        )

        const val COMMON_INDICATOR = "commonTest"
        const val KMOCK_PREFIX = "kmock_"
        const val KSP_DIR = "${KMOCK_PREFIX}kspDir"
        const val KMP_FLAG = "${KMOCK_PREFIX}isKmp"
        const val INTERFACES_KMOCK = "${KMOCK_PREFIX}allowInterfacesOnKmock"
        const val INTERFACES_KSPY = "${KMOCK_PREFIX}allowInterfacesOnKspy"
        const val ROOT_PACKAGE = "${KMOCK_PREFIX}rootPackage"
        const val PRECEDENCE_PREFIX = "${KMOCK_PREFIX}precedence_"
        const val ALIAS_PREFIX = "${KMOCK_PREFIX}alias_"
        const val RECURSIVE_PREFIX = "${KMOCK_PREFIX}recursive_"
        const val BUILD_IN_PREFIX = "${KMOCK_PREFIX}buildIn_"
        const val SPY_ON = "${KMOCK_PREFIX}spyOn_"
        const val SPIES_ONLY = "${KMOCK_PREFIX}spiesOnly"
        const val USELESS_PREFIXES_PREFIX = "${KMOCK_PREFIX}namePrefix_"
    }
}
