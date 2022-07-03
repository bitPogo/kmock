/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import java.util.SortedSet
import tech.antibytes.kmock.KMock
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.KMockMulti
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.MultiMock
import tech.antibytes.kmock.MultiMockCommon
import tech.antibytes.kmock.MultiMockShared
import tech.antibytes.kmock.Relaxer as RelaxationAnnotation
import tech.antibytes.kmock.proxy.NoopCollector
import tech.antibytes.kmock.proxy.ProxyFactory

internal interface ProcessorContract {
    data class Relaxer(
        val packageName: String,
        val functionName: String,
        val source: KSFile,
    )

    data class Options(
        val kspDir: String,
        val isKmp: Boolean,
        val customAnnotations: Map<String, String>,
        val freezeOnDefault: Boolean,
        val allowInterfaces: Boolean,
        val spiesOnly: Boolean,
        val spyAll: Boolean,
        val disableFactories: Boolean,
        val rootPackage: String,
        val knownSharedSourceSets: Set<String>,
        val dependencies: Map<String, SortedSet<String>>,
        val aliases: Map<String, String>,
        val useBuildInProxiesOn: Set<String>,
        val spyOn: Set<String>,
        val useTypePrefixFor: Map<String, String>,
        val customMethodNames: Map<String, String>,
        val allowExperimentalProxyAccess: Boolean,
        val enableFineGrainedNames: Boolean,
        val preventResolvingOfAliases: Set<String>,
    )

    fun interface OptionExtractor {
        fun convertOptions(kspRawOptions: Map<String, String>): Options
    }

    interface SpyContainer {
        fun isSpyable(
            template: KSClassDeclaration?,
            packageName: String,
            templateName: String,
        ): Boolean

        fun hasSpies(filter: List<Source> = emptyList()): Boolean
    }

    sealed interface Source {
        val indicator: String
        val templateName: String
        val packageName: String
        val dependencies: List<KSFile>
    }

    data class TemplateSource(
        override val indicator: String,
        override val templateName: String,
        override val packageName: String,
        override val dependencies: List<KSFile>,
        val template: KSClassDeclaration,
        val generics: Map<String, List<KSTypeReference>>?,
    ) : Source

    data class TemplateMultiSource(
        override val indicator: String,
        override val templateName: String,
        override val packageName: String,
        override val dependencies: List<KSFile>,
        val templates: List<KSClassDeclaration>,
        val generics: List<Map<String, List<KSTypeReference>>?>,
    ) : Source

    data class Aggregated<out T : Source>(
        val illFormed: List<KSAnnotated>,
        val extractedTemplates: List<T>,
        val totalDependencies: List<KSFile>,
    )

    data class AnnotationContainer(
        val common: List<KSAnnotated>,
        val shared: Map<String, List<KSAnnotated>>,
        val platform: List<KSAnnotated>,
    )

    interface SourceSetValidator {
        fun isValidateSourceSet(sourceSet: Any?): Boolean
    }

    interface AnnotationFilter {
        fun filterAnnotation(
            annotations: Map<String, String>,
        ): Map<String, String>

        fun isApplicableSingleSourceAnnotation(
            annotation: KSAnnotation,
        ): Boolean

        fun isApplicableMultiSourceAnnotation(
            annotation: KSAnnotation,
        ): Boolean
    }

    interface SourceFilter {
        fun <T : Source> filter(
            templateSources: List<T>,
            filteredBy: List<T>,
        ): List<T>

        fun <T : Source> filterByDependencies(
            templateSources: List<T>,
        ): List<T>
    }

    interface Aggregator

    interface RelaxationAggregator : Aggregator {
        fun extractRelaxer(resolver: Resolver): Relaxer?
    }

    interface SingleSourceAggregator : Aggregator {
        fun extractKmockInterfaces(resolver: Resolver): AnnotationContainer

        fun extractCommonInterfaces(
            kmockAnnotated: List<KSAnnotated>,
            resolver: Resolver,
        ): Aggregated<TemplateSource>

        fun extractSharedInterfaces(
            kmockAnnotated: Map<String, List<KSAnnotated>>,
            resolver: Resolver,
        ): Aggregated<TemplateSource>

        fun extractPlatformInterfaces(
            kmockAnnotated: List<KSAnnotated>,
            resolver: Resolver,
        ): Aggregated<TemplateSource>
    }

    interface MultiSourceAggregator : Aggregator {
        fun extractKmockInterfaces(resolver: Resolver): AnnotationContainer

        fun extractCommonInterfaces(
            kmockAnnotated: List<KSAnnotated>,
            resolver: Resolver,
        ): Aggregated<TemplateMultiSource>

        fun extractSharedInterfaces(
            kmockAnnotated: Map<String, List<KSAnnotated>>,
            resolver: Resolver,
        ): Aggregated<TemplateMultiSource>

        fun extractPlatformInterfaces(
            kmockAnnotated: List<KSAnnotated>,
            resolver: Resolver,
        ): Aggregated<TemplateMultiSource>
    }

    interface AggregatorFactory<T : Aggregator> {
        fun getInstance(
            logger: KSPLogger,
            rootPackage: String,
            sourceSetValidator: SourceSetValidator,
            annotationFilter: AnnotationFilter,
            generics: GenericResolver,
            customAnnotations: Map<String, String>,
            aliases: Map<String, String>,
        ): T
    }

    data class GenericDeclaration(
        val types: List<TypeName>,
        val isRecursive: Boolean,
        val isNullable: Boolean,
        val doCastReturnType: Boolean = false,
    )

    interface GenericResolver {
        fun extractGenerics(template: KSDeclaration): Map<String, List<KSTypeReference>>?

        fun mapClassScopeGenerics(
            generics: Map<String, List<KSTypeReference>>?,
            typeParameterResolver: TypeParameterResolver,
        ): Map<String, List<TypeName>>?

        fun resolveMockClassType(
            template: KSClassDeclaration,
            typeParameterResolver: TypeParameterResolver,
        ): TypeName

        fun resolveKMockFactoryType(
            name: String,
            templateSource: TemplateSource,
        ): TypeVariableName

        fun mapDeclaredGenerics(
            generics: Map<String, List<KSTypeReference>>,
            typeParameterResolver: TypeParameterResolver,
        ): List<TypeVariableName>

        fun remapTypes(
            templates: List<KSClassDeclaration>,
            generics: List<Map<String, List<KSTypeReference>>?>,
        ): Pair<List<TypeName>, List<TypeVariableName>>

        fun mapProxyGenerics(
            classScope: Map<String, List<TypeName>>?,
            generics: Map<String, List<KSTypeReference>>,
            typeParameterResolver: TypeParameterResolver,
        ): Map<String, GenericDeclaration>
    }

    interface KmpCodeGenerator : CodeGenerator {
        fun setOneTimeSourceSet(sourceSet: String)
        fun closeFiles()
    }

    data class ProxyInfo(
        val templateName: String,
        val proxyName: String,
        val proxyId: String,
    )

    data class MemberArgumentTypeInfo(
        val argumentName: String,
        val methodTypeName: TypeName,
        val proxyTypeName: TypeName,
        val isVarArg: Boolean,
    )

    data class MemberReturnTypeInfo(
        val methodTypeName: TypeName,
        val proxyTypeName: TypeName,
        val generic: GenericDeclaration?,
        val classScope: Map<String, List<TypeName>>?,
    )

    data class ProxyBundle(
        val proxy: PropertySpec,
        val returnType: MemberReturnTypeInfo,
        val sideEffect: LambdaTypeName,
    )

    interface ProxyNameCollector {
        fun collect(template: KSClassDeclaration)
    }

    interface ProxyNameSelector {
        fun selectPropertyName(
            qualifier: String,
            propertyName: String,
        ): ProxyInfo

        fun selectBuildInMethodName(
            qualifier: String,
            methodName: String,
        ): ProxyInfo

        fun selectMethodName(
            qualifier: String,
            methodName: String,
            generics: Map<String, GenericDeclaration>,
            arguments: Array<MemberArgumentTypeInfo>,
        ): ProxyInfo

        fun selectReceiverGetterName(
            qualifier: String,
            propertyName: String,
            receiver: MemberArgumentTypeInfo,
            generics: Map<String, GenericDeclaration>,
        ): ProxyInfo

        fun selectReceiverSetterName(
            qualifier: String,
            propertyName: String,
            receiver: MemberArgumentTypeInfo,
            generics: Map<String, GenericDeclaration>,
        ): ProxyInfo

        fun selectReceiverMethodName(
            qualifier: String,
            methodName: String,
            generics: Map<String, GenericDeclaration>,
            arguments: Array<MemberArgumentTypeInfo>,
        ): ProxyInfo
    }

    interface RelaxerGenerator {
        fun buildPropertyRelaxation(
            propertyType: MemberReturnTypeInfo,
            relaxer: Relaxer?,
        ): String

        fun buildMethodRelaxation(
            methodReturnType: MemberReturnTypeInfo,
            relaxer: Relaxer?,
        ): String

        fun buildBuildInRelaxation(
            methodName: String,
            argument: MemberArgumentTypeInfo?,
        ): String
    }

    interface SpyGenerator {
        fun buildGetterSpy(propertyName: String): String
        fun buildSetterSpy(propertyName: String): String

        fun buildReceiverGetterSpy(propertyName: String, propertyType: MemberReturnTypeInfo): String
        fun buildReceiverSetterSpy(propertyName: String): String

        fun buildMethodSpy(
            methodName: String,
            parameter: List<TypeName>,
            arguments: Array<MemberArgumentTypeInfo>,
            methodReturnType: MemberReturnTypeInfo,
        ): String

        fun buildReceiverMethodSpy(
            methodName: String,
            parameter: List<TypeName>,
            arguments: Array<MemberArgumentTypeInfo>,
            methodReturnType: MemberReturnTypeInfo,
        ): String

        fun buildEqualsSpy(mockName: String): String
    }

    interface NonIntrusiveInvocationGenerator {
        fun buildGetterNonIntrusiveInvocation(
            enableSpy: Boolean,
            propertyName: String,
            propertyType: MemberReturnTypeInfo,
            relaxer: Relaxer?,
        ): String

        fun buildSetterNonIntrusiveInvocation(
            enableSpy: Boolean,
            propertyName: String,
        ): String

        fun buildMethodNonIntrusiveInvocation(
            enableSpy: Boolean,
            methodName: String,
            typeParameter: List<TypeName>,
            arguments: Array<MemberArgumentTypeInfo>,
            methodReturnType: MemberReturnTypeInfo,
            relaxer: Relaxer?,
        ): String

        fun buildBuildInNonIntrusiveInvocation(
            enableSpy: Boolean,
            mockName: String,
            methodName: String,
            argument: MemberArgumentTypeInfo?,
        ): String

        fun buildReceiverGetterNonIntrusiveInvocation(
            enableSpy: Boolean,
            propertyName: String,
            propertyType: MemberReturnTypeInfo,
            relaxer: Relaxer?,
        ): String

        fun buildReceiverSetterNonIntrusiveInvocation(
            enableSpy: Boolean,
            propertyName: String,
        ): String

        fun buildReceiverMethodNonIntrusiveInvocation(
            enableSpy: Boolean,
            methodName: String,
            typeParameter: List<TypeName>,
            arguments: Array<MemberArgumentTypeInfo>,
            methodReturnType: MemberReturnTypeInfo,
            relaxer: Relaxer?,
        ): String
    }

    interface MethodGeneratorHelper {
        fun determineArguments(
            inherited: Boolean,
            generics: Map<String, GenericDeclaration>?,
            arguments: List<KSValueParameter>,
            methodWideResolver: TypeParameterResolver,
        ): Array<MemberArgumentTypeInfo>

        fun resolveTypeParameter(
            parameter: List<KSTypeParameter>,
            methodWideResolver: TypeParameterResolver,
        ): List<TypeName>

        fun resolveProxyGenerics(
            classScope: Map<String, List<TypeName>>?,
            generics: Map<String, List<KSTypeReference>>?,
            methodWideResolver: TypeParameterResolver,
        ): Map<String, GenericDeclaration>?

        fun buildProxy(
            proxyInfo: ProxyInfo,
            arguments: Array<MemberArgumentTypeInfo>,
            suspending: Boolean,
            classScopeGenerics: Map<String, List<TypeName>>?,
            generics: Map<String, GenericDeclaration>?,
            methodReturnType: TypeName,
            proxyReturnType: TypeName,
            methodWideResolver: TypeParameterResolver,
        ): ProxyBundle
    }

    interface PropertyGenerator {
        fun buildPropertyBundle(
            qualifier: String,
            classScopeGenerics: Map<String, List<TypeName>>?,
            ksProperty: KSPropertyDeclaration,
            classWideResolver: TypeParameterResolver,
            enableSpy: Boolean,
            relaxer: Relaxer?,
        ): Pair<PropertySpec, PropertySpec>
    }

    interface MethodGenerator {
        fun buildMethodBundle(
            qualifier: String,
            classScopeGenerics: Map<String, List<TypeName>>?,
            ksFunction: KSFunctionDeclaration,
            classWideResolver: TypeParameterResolver,
            enableSpy: Boolean,
            inherited: Boolean,
            relaxer: Relaxer?,
        ): Triple<PropertySpec, FunSpec, LambdaTypeName>
    }

    interface BuildInMethodGenerator {
        fun buildMethodBundles(
            mockName: String,
            qualifier: String,
            enableSpy: Boolean,
        ): List<Triple<PropertySpec, FunSpec, LambdaTypeName>>
    }

    interface ReceiverGenerator {
        fun buildPropertyBundle(
            spyType: TypeName,
            qualifier: String,
            classScopeGenerics: Map<String, List<TypeName>>?,
            ksProperty: KSPropertyDeclaration,
            classWideResolver: TypeParameterResolver,
            enableSpy: Boolean,
            relaxer: Relaxer?,
        ): Triple<PropertySpec, PropertySpec?, PropertySpec>

        fun buildMethodBundle(
            spyType: TypeName,
            qualifier: String,
            classScopeGenerics: Map<String, List<TypeName>>?,
            ksFunction: KSFunctionDeclaration,
            classWideResolver: TypeParameterResolver,
            enableSpy: Boolean,
            inherited: Boolean,
            relaxer: Relaxer?,
        ): Triple<PropertySpec, FunSpec, LambdaTypeName>

        fun buildReceiverSpyContext(
            spyType: TypeName,
            classWideResolver: TypeParameterResolver,
        ): FunSpec
    }

    interface ProxyAccessMethodGenerator {
        fun collectProperty(
            propertyName: String,
            propertyType: TypeName,
            proxyName: String,
        )

        fun collectMethod(
            methodName: String,
            isSuspending: Boolean,
            typeParameter: List<TypeVariableName>,
            arguments: List<ParameterSpec>,
            returnType: TypeName?,
            proxyName: String,
            proxySignature: ParameterizedTypeName,
            proxySideEffect: LambdaTypeName,
        )

        fun createReferenceStorage(): PropertySpec
        fun createAccessMethods(): List<FunSpec>
    }

    interface ProxyAccessMethodGeneratorFactory {
        fun getInstance(
            enableGenerator: Boolean,
            preventResolvingOfAliases: Set<String>,
            nullableClassGenerics: Map<String, TypeName>,
        ): ProxyAccessMethodGenerator
    }

    interface MockGenerator {
        fun writePlatformMocks(
            templateSources: List<TemplateSource>,
            templateMultiSources: List<TemplateMultiSource>,
            relaxer: Relaxer?,
        )

        fun writeSharedMocks(
            templateSources: List<TemplateSource>,
            templateMultiSources: List<TemplateMultiSource>,
            relaxer: Relaxer?,
        )

        fun writeCommonMocks(
            templateSources: List<TemplateSource>,
            templateMultiSources: List<TemplateMultiSource>,
            relaxer: Relaxer?,
        )
    }

    fun interface ParentFinder {
        fun find(
            templateSource: TemplateSource,
            templateMultiSources: List<TemplateMultiSource>,
        ): TemplateMultiSource?
    }

    interface MultiInterfaceBinder {
        fun bind(
            templateSources: List<TemplateMultiSource>,
            dependencies: List<KSFile>,
        )
    }

    interface MockFactoryGeneratorUtil {
        fun generateSharedMockFactorySignature(
            mockType: TypeVariableName,
            spyType: TypeVariableName,
            boundaries: List<TypeName> = emptyList(),
            generics: List<TypeVariableName> = emptyList(),
        ): FunSpec.Builder

        fun generateKmockSignature(
            type: TypeVariableName,
            boundaries: List<TypeName> = emptyList(),
            generics: List<TypeVariableName> = emptyList(),
            hasDefault: Boolean,
            modifier: KModifier?,
        ): FunSpec.Builder

        fun generateKspySignature(
            mockType: TypeVariableName,
            spyType: TypeVariableName,
            boundaries: List<TypeName> = emptyList(),
            generics: List<TypeVariableName> = emptyList(),
            hasDefault: Boolean,
            modifier: KModifier?,
        ): FunSpec.Builder

        fun splitInterfacesIntoRegularAndGenerics(
            templateSources: List<TemplateSource>,
        ): Pair<List<TemplateSource>, List<TemplateSource>>

        fun resolveGenerics(templateSource: TemplateSource): List<TypeVariableName>

        fun <T : Source> resolveModifier(templateSource: T? = null): KModifier?

        fun resolveMockType(
            templateSource: TemplateMultiSource,
            parameter: List<TypeName>,
        ): TypeName
    }

    interface MockFactoryWithoutGenerics {
        fun buildKMockFactory(): FunSpec
        fun buildSpyFactory(): FunSpec

        fun buildSharedMockFactory(
            templateSources: List<TemplateSource>,
            templateMultiSources: List<TemplateMultiSource>,
            relaxer: Relaxer?,
        ): FunSpec
    }

    data class FactoryBundle(
        val kmock: FunSpec,
        val kspy: FunSpec?,
        val shared: FunSpec,
    )

    data class FactoryMultiBundle(
        val kmock: FunSpec?,
        val kspy: FunSpec?,
        val shared: FunSpec?,
    )

    interface MockFactoryWithGenerics {
        fun buildGenericFactories(
            templateSources: List<TemplateSource>,
            relaxer: Relaxer?,
        ): List<FactoryBundle>
    }

    interface MockFactoryMultiInterface {
        fun buildFactories(
            templateMultiSources: List<TemplateMultiSource>,
            relaxer: Relaxer?,
        ): List<FactoryMultiBundle>
    }

    interface MockFactoryGenerator {
        fun writeFactories(
            templateSources: List<TemplateSource>,
            templateMultiSources: List<TemplateMultiSource>,
            relaxer: Relaxer?,
            dependencies: List<KSFile>,
        )
    }

    interface MockFactoryEntryPointGenerator {
        fun generateCommon(
            templateSources: List<TemplateSource>,
            templateMultiSources: List<TemplateMultiSource>,
            totalTemplates: List<TemplateSource>,
            totalMultiSources: List<TemplateMultiSource>,
            dependencies: List<KSFile>,
        )

        fun generateShared(
            templateSources: List<TemplateSource>,
            templateMultiSources: List<TemplateMultiSource>,
            dependencies: List<KSFile>,
        )
    }

    companion object {
        const val KMOCK_FACTORY_TYPE_NAME = "Mock"
        const val KSPY_FACTORY_TYPE_NAME = "SpyOn"
        const val SHARED_MOCK_FACTORY = "getMockInstance"
        const val FACTORY_FILE_NAME = "MockFactory"
        const val KMOCK_FACTORY = "kmock"
        const val KSPY_FACTORY = "kspy"
        const val INTERMEDIATE_INTERFACES_FILE_NAME = "KMockMultiInterfaceArtifacts"

        const val CREATE_PROPERTY_PROXY = "createPropertyProxy"
        const val CREATE_ASYNC_PROXY = "createAsyncFunProxy"
        const val CREATE_SYNC_PROXY = "createSyncFunProxy"
        const val COLLECTOR_ARGUMENT = "collector"
        const val FREEZE_ARGUMENT = "freeze"
        const val SPY_ARGUMENT = "spyOn"
        const val RELAXER_ARGUMENT = "relaxed"
        const val UNIT_RELAXER_ARGUMENT = "relaxUnitFun"
        const val IGNORE_ARGUMENT = "ignorableForVerification"

        const val TEMPLATE_TYPE_ARGUMENT = "templateType"
        const val TYPE_PARAMETER = "KMockTypeParameter"

        const val ARGUMENTS_WITH_RELAXER = "$COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT, $RELAXER_ARGUMENT = $RELAXER_ARGUMENT, $UNIT_RELAXER_ARGUMENT = $UNIT_RELAXER_ARGUMENT, $FREEZE_ARGUMENT = $FREEZE_ARGUMENT, $SPY_ARGUMENT = $SPY_ARGUMENT"
        const val ARGUMENTS_WITHOUT_RELAXER = "$COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT, $UNIT_RELAXER_ARGUMENT = $UNIT_RELAXER_ARGUMENT, $FREEZE_ARGUMENT = $FREEZE_ARGUMENT, $SPY_ARGUMENT = $SPY_ARGUMENT"
        const val UNKNOWN_INTERFACE = "throw RuntimeException(\"Unknown Interface \${$KMOCK_FACTORY_TYPE_NAME::class.simpleName}.\")"

        const val VALUE = "value"
        const val EQUALS = "equals"
        const val SPY_CONTEXT = "spyContext"
        const val SPY_PROPERTY = "__spyOn"

        @OptIn(KMockExperimental::class)
        val ANNOTATION_KMOCK_NAME: String = KMock::class.java.canonicalName

        @OptIn(KMockExperimental::class)
        val ANNOTATION_KMOCK_MULTI_NAME: String = KMockMulti::class.java.canonicalName

        val ANNOTATION_PLATFORM_NAME: String = Mock::class.java.canonicalName
        val ANNOTATION_PLATFORM_MULTI_NAME: String = MultiMock::class.java.canonicalName
        val ANNOTATION_COMMON_NAME: String = MockCommon::class.java.canonicalName
        val ANNOTATION_COMMON_MULTI_NAME: String = MultiMockCommon::class.java.canonicalName
        val ANNOTATION_SHARED_NAME: String = MockShared::class.java.canonicalName
        val ANNOTATION_SHARED_MULTI_NAME: String = MultiMockShared::class.java.canonicalName
        val RELAXATION_NAME: String = RelaxationAnnotation::class.java.canonicalName

        val supportedPlatforms = sortedSetOf(
            "androidTest",
            "androidAndroidTest",
            "jsTest",
            "jvmTest",
            "linuxX64Test",
            "iosArm64",
            "iosX64",
            "iosSimulatorArm64Test",
            "test",
        )

        val COLLECTOR_NAME = Collector::class.asClassName()
        val KMOCK_CONTRACT = KMockContract::class.asClassName()
        val NOOP_COLLECTOR = NoopCollector::class.asClassName()
        val PROXY_FACTORY = ProxyFactory::class.asClassName()

        val UNUSED = AnnotationSpec.builder(Suppress::class).addMember(
            "%S, %S",
            "UNUSED_PARAMETER",
            "UNUSED_EXPRESSION",
        ).build()

        val UNCHECKED = AnnotationSpec.builder(Suppress::class).addMember("%S", "UNCHECKED_CAST").build()

        const val MULTI_MOCK = "MultiMock"
        val MULTI_MOCK_TYPE = TypeVariableName(MULTI_MOCK)
        val UNIT = Unit::class.asClassName().copy(nullable = false) as ClassName
        val ANY = Any::class.asClassName().copy(nullable = false)
        val NULLABLE_ANY = Any::class.asClassName().copy(nullable = true)
        val ARRAY = Array::class.asClassName()
        val MULTI_BOUNDED = TypeVariableName("multiboundedKmock")

        const val COMMON_INDICATOR = "commonTest"
        const val PLATFORM_INDICATOR = ""

        const val KMOCK_PREFIX = "kmock_"
        const val KSP_DIR = "${KMOCK_PREFIX}kspDir"
        const val KMP_FLAG = "${KMOCK_PREFIX}isKmp"
        const val DISABLE_FACTORIES = "${KMOCK_PREFIX}disable_factories"
        const val FREEZE_OPTION = "${KMOCK_PREFIX}freeze"
        const val INTERFACES = "${KMOCK_PREFIX}allowInterfaces"
        const val ROOT_PACKAGE = "${KMOCK_PREFIX}rootPackage"
        const val DEPENDENCIES = "${KMOCK_PREFIX}dependencies_"
        const val ALIASES = "${KMOCK_PREFIX}alias_"
        const val USE_BUILD_IN = "${KMOCK_PREFIX}buildIn_"
        const val SPY_ON = "${KMOCK_PREFIX}spyOn_"
        const val SPIES_ONLY = "${KMOCK_PREFIX}spiesOnly"
        const val SPY_ALL = "${KMOCK_PREFIX}spyAll"
        const val TYPE_PREFIXES = "${KMOCK_PREFIX}namePrefix_"
        const val CUSTOM_METHOD_NAME = "${KMOCK_PREFIX}customMethodName_"
        const val CUSTOM_ANNOTATION = "${KMOCK_PREFIX}customAnnotation_"
        const val ALTERNATIVE_PROXY_ACCESS = "${KMOCK_PREFIX}alternativeProxyAccess"
        const val FINE_GRAINED_PROXY_NAMES = "${KMOCK_PREFIX}enableFineGrainedProxyNames"
        const val PREVENT_ALIAS_RESOLVING = "${KMOCK_PREFIX}preventAliasResolving_"
    }
}
