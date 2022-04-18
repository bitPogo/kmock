/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory
import tech.antibytes.kmock.Relaxer as RelaxationAnnotation

internal interface ProcessorContract {
    data class Relaxer(
        val packageName: String,
        val functionName: String
    )

    data class Options(
        val kspDir: String,
        val isKmp: Boolean,
        val freezeOnDefault: Boolean,
        val allowInterfaces: Boolean,
        val spiesOnly: Boolean,
        val disableFactories: Boolean,
        val rootPackage: String,
        val knownSourceSets: Set<String>,
        val precedences: Map<String, Int>,
        val aliases: Map<String, String>,
        val useBuildInProxiesOn: Set<String>,
        val spyOn: Set<String>,
        val enableNewOverloadingNames: Boolean,
        val useTypePrefixFor: Map<String, String>,
        val customMethodNames: Map<String, String>,
        val uselessPrefixes: Set<String>,
    )

    fun interface OptionExtractor {
        fun convertOptions(kspRawOptions: Map<String, String>): Options
    }

    interface AggregatorFactory {
        fun getInstance(
            logger: KSPLogger,
            knownSourceSets: Set<String>,
            generics: GenericResolver,
            aliases: Map<String, String>
        ): Aggregator
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

    interface SourceSetValidator {
        fun isValidateSourceSet(sourceSet: Any?): Boolean
    }

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

    data class ProxyInfo(
        val templateName: String,
        val proxyName: String,
        val proxyId: String
    )

    data class MethodTypeInfo(
        val argumentName: String,
        val typeName: TypeName,
        val isVarArg: Boolean
    )

    data class MethodArgumentTypeInfo(
        val typeInfo: MethodTypeInfo,
        val generic: GenericDeclaration?
    )

    data class MethodReturnTypeInfo(
        val typeName: TypeName,
        val generic: GenericDeclaration?
    )

    interface ProxyNameCollector {
        fun collect(template: KSClassDeclaration)
    }

    interface ProxyNameSelector {
        fun selectPropertyName(
            qualifier: String,
            propertyName: String
        ): ProxyInfo

        fun selectBuildInMethodName(
            qualifier: String,
            methodName: String,
        ): ProxyInfo

        fun selectMethodName(
            qualifier: String,
            methodName: String,
            generics: Map<String, List<KSTypeReference>>,
            typeResolver: TypeParameterResolver,
            arguments: Array<MethodTypeInfo>
        ): ProxyInfo
    }

    interface RelaxerGenerator {
        fun buildPropertyRelaxation(relaxer: Relaxer?): String

        fun buildMethodRelaxation(
            relaxer: Relaxer?,
            methodReturnType: MethodReturnTypeInfo,
        ): String

        fun buildBuildInRelaxation(
            methodName: String,
            argument: MethodTypeInfo?,
        ): String
    }

    interface SpyGenerator {
        fun buildGetterSpy(propertyName: String): String
        fun buildSetterSpy(propertyName: String): String

        fun buildMethodSpy(
            methodName: String,
            arguments: Array<MethodTypeInfo>
        ): String

        fun buildEqualsSpy(mockName: String): String
    }

    interface NonIntrusiveInvocationGenerator {
        fun buildGetterNonIntrusiveInvocation(
            enableSpy: Boolean,
            propertyName: String,
            relaxer: Relaxer?
        ): String

        fun buildSetterNonIntrusiveInvocation(
            enableSpy: Boolean,
            propertyName: String,
        ): String

        fun buildMethodNonIntrusiveInvocation(
            enableSpy: Boolean,
            methodName: String,
            arguments: Array<MethodTypeInfo>,
            methodReturnType: MethodReturnTypeInfo,
            relaxer: Relaxer?,
        ): String

        fun buildBuildInNonIntrusiveInvocation(
            enableSpy: Boolean,
            mockName: String,
            methodName: String,
            argument: MethodTypeInfo?
        ): String
    }

    interface PropertyGenerator {
        fun buildPropertyBundle(
            qualifier: String,
            ksProperty: KSPropertyDeclaration,
            typeResolver: TypeParameterResolver,
            enableSpy: Boolean,
            relaxer: Relaxer?,
        ): Pair<PropertySpec?, PropertySpec>
    }

    interface MethodGenerator {
        fun buildMethodBundle(
            methodScope: TypeName?,
            qualifier: String,
            ksFunction: KSFunctionDeclaration,
            typeResolver: TypeParameterResolver,
            enableSpy: Boolean,
            relaxer: Relaxer?,
        ): Pair<PropertySpec?, FunSpec>
    }

    interface BuildInMethodGenerator {
        fun buildMethodBundles(
            mockName: String,
            qualifier: String,
            enableSpy: Boolean,
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
        fun generateMockFactorySignature(
            mockType: TypeVariableName,
            spyType: TypeVariableName,
            generics: List<TypeVariableName>,
        ): FunSpec.Builder

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
        const val SHARED_MOCK_FACTORY = "getMockInstance"
        const val FACTORY_FILE_NAME = "MockFactory"
        val ANNOTATION_PLATFORM_NAME: String = Mock::class.java.canonicalName
        val ANNOTATION_COMMON_NAME: String = MockCommon::class.java.canonicalName
        val ANNOTATION_SHARED_NAME: String = MockShared::class.java.canonicalName
        val RELAXATION_NAME: String = RelaxationAnnotation::class.java.canonicalName

        val COLLECTOR_NAME = ClassName(
            Collector::class.java.packageName,
            "KMockContract.${Collector::class.java.simpleName}"
        )
        val KMOCK_CONTRACT = ClassName(
            KMockContract::class.java.packageName,
            KMockContract::class.java.simpleName
        )

        val NOOP_COLLECTOR_NAME = ClassName(
            NoopCollector::class.java.packageName,
            NoopCollector::class.java.simpleName
        )

        val PROXY_FACTORY_NAME = ClassName(
            ProxyFactory::class.java.packageName,
            ProxyFactory::class.java.simpleName
        )

        val UNUSED = AnnotationSpec.builder(Suppress::class).addMember(
            "%S, %S",
            "UNUSED_PARAMETER",
            "UNUSED_EXPRESSION"
        ).build()

        const val COMMON_INDICATOR = "commonTest"

        const val KMOCK_PREFIX = "kmock_"
        const val KSP_DIR = "${KMOCK_PREFIX}kspDir"
        const val KMP_FLAG = "${KMOCK_PREFIX}isKmp"
        const val DISABLE_FACTORIES = "${KMOCK_PREFIX}disable_factories"
        const val FREEZE = "${KMOCK_PREFIX}freeze"
        const val INTERFACES = "${KMOCK_PREFIX}allowInterfaces"
        const val ROOT_PACKAGE = "${KMOCK_PREFIX}rootPackage"
        const val PRECEDENCE = "${KMOCK_PREFIX}precedence_"
        const val ALIASES = "${KMOCK_PREFIX}alias_"
        const val USE_BUILD_IN = "${KMOCK_PREFIX}buildIn_"
        const val SPY_ON = "${KMOCK_PREFIX}spyOn_"
        const val SPIES_ONLY = "${KMOCK_PREFIX}spiesOnly"
        const val OVERLOAD_NAME_FEATURE_FLAG = "${KMOCK_PREFIX}useNewOverloadedNames"
        const val USELESS_PREFIXES = "${KMOCK_PREFIX}oldNamePrefix_"
        const val TYPE_PREFIXES = "${KMOCK_PREFIX}namePrefix_"
        const val CUSTOM_METHOD_NAME = "${KMOCK_PREFIX}customMethodName_"
    }
}
