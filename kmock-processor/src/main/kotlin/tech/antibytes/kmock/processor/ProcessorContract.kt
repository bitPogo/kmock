/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.proxy.AsyncFunProxy
import tech.antibytes.kmock.proxy.PropertyProxy
import tech.antibytes.kmock.proxy.SyncFunProxy

internal interface ProcessorContract {
    data class Relaxer(
        val packageName: String,
        val functionName: String
    )

    data class Options(
        val isKmp: Boolean,
        val rootPackage: String
    )

    interface Aggregator {
        fun extractInterfaces(annotated: Sequence<KSAnnotated>): Aggregated
        fun extractRelaxer(annotated: Sequence<KSAnnotated>): Relaxer?
    }

    data class InterfaceSource(val marker: String, val interfaze: KSClassDeclaration)

    data class Aggregated(
        val illFormed: List<KSAnnotated>,
        val extractedInterfaces: List<InterfaceSource>,
        val dependencies: List<KSFile>
    )

    interface SourceFilter {
        fun filter(
            sources: List<InterfaceSource>,
            filteredBy: List<InterfaceSource>
        ): List<InterfaceSource>

        fun filterSharedSources(
            sources: List<InterfaceSource>
        ): List<InterfaceSource>
    }

    data class GenericDeclaration(
        val types: List<TypeName>,
        val recursive: Boolean,
        val nullable: Boolean
    )

    interface GenericResolver {
        fun extractGenerics(
            template: KSDeclaration,
            resolver: TypeParameterResolver
        ): Map<String, List<KSTypeReference>>?

        fun mapDeclaredGenerics(
            generics: Map<String, List<KSTypeReference>>,
            typeResolver: TypeParameterResolver
        ): List<TypeVariableName>

        fun mapProxyGenerics(
            generics: Map<String, List<KSTypeReference>>,
            typeResolver: TypeParameterResolver
        ): Map<String, GenericDeclaration>
    }

    interface RelaxerGenerator {
        fun buildRelaxers(relaxer: Relaxer?, useUnitFunRelaxer: Boolean): String
    }

    interface PropertyGenerator {
        fun buildPropertyBundle(
            qualifier: String,
            ksProperty: KSPropertyDeclaration,
            typeResolver: TypeParameterResolver,
            existingProxies: List<String> = mutableListOf(),
            relaxer: Relaxer?
        ): Pair<PropertySpec, PropertySpec>
    }

    interface FunctionGenerator {
        fun buildFunctionBundle(
            qualifier: String,
            ksFunction: KSFunctionDeclaration,
            typeResolver: TypeParameterResolver,
            existingProxies: List<String>,
            relaxer: Relaxer?
        ): Pair<PropertySpec, FunSpec>
    }

    interface MockGenerator {
        fun writePlatformMocks(
            interfaces: List<InterfaceSource>,
            dependencies: List<KSFile>,
            relaxer: Relaxer?
        )

        fun writeSharedMocks(
            interfaces: List<InterfaceSource>,
            dependencies: List<KSFile>,
            relaxer: Relaxer?
        )

        fun writeCommonMocks(
            interfaces: List<InterfaceSource>,
            dependencies: List<KSFile>,
            relaxer: Relaxer?
        )
    }

    interface MockFactoryGenerator {
        fun writeFactories(
            options: Options,
            interfaces: List<InterfaceSource>,
            dependencies: List<KSFile>,
            relaxer: Relaxer?,
        )
    }

    enum class Target(val value: String) {
        COMMON("COMMON SOURCE"),
        PLATFORM("")
    }

    companion object {
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
            SyncFunProxy::class.java.packageName,
            "relaxVoidFunction"
        )

        val SYNC_FUN_NAME = ClassName(
            SyncFunProxy::class.java.packageName,
            SyncFunProxy::class.java.simpleName
        )
        val ASYNC_FUN_NAME = ClassName(
            AsyncFunProxy::class.java.packageName,
            AsyncFunProxy::class.java.simpleName
        )
        val PROP_NAME = ClassName(
            PropertyProxy::class.java.packageName,
            PropertyProxy::class.java.simpleName
        )
    }
}
