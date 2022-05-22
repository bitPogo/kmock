/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyAccessMethodGeneratorFactory
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNCHECKED
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

internal class KMockProxyAccessMethodGenerator private constructor(
    private val enabled: Boolean
) : ProxyAccessMethodGenerator {
    private sealed class Member {
        data class Property(
            val propertyName: String,
            val propertyType: TypeName,
            val proxyName: String,
        ) : Member()

        data class SyncFunProxy(
            val methodName: String,
            val proxyName: String,
            val proxySignature: TypeName,
            val sideEffect: String,
        ) : Member()
    }

    private val properties: MutableList<Member.Property> = mutableListOf()
    private val syncFun: MutableMap<String, MutableList<Member.SyncFunProxy>> = mutableMapOf()

    private fun guardedCollect(action: () -> Unit) {
        if (enabled) {
            action()
        }
    }

    override fun collectProperty(
        propertyName: String,
        propertyType: TypeName,
        proxyName: String,
    ) = guardedCollect {
        properties.add(
            Member.Property(
                propertyName = propertyName,
                propertyType = propertyType,
                proxyName = proxyName,
            )
        )
    }

    private fun String.trimToSideEffect(): String {
        return this.substringAfter(',').substringBeforeLast(">").trim()
    }

    private fun collectSyncMethod(
        methodName: String,
        proxyName: String,
        classScopeGenerics: Map<String, List<TypeName>>?,
        typeParameter: List<TypeVariableName>,
        arguments: List<TypeName>,
        proxySignature: TypeName
    ) {
        val sideEffect = proxySignature.toString().trimToSideEffect()
        val registry = syncFun.getOrElse(sideEffect) { mutableListOf() }
        registry.add(
            Member.SyncFunProxy(
                methodName = methodName,
                proxyName = proxyName,
                proxySignature = proxySignature,
                sideEffect = sideEffect,
            )
        )

        syncFun[sideEffect] = registry
    }

    override fun collectMethod(
        methodName: String,
        isSuspending: Boolean,
        classScopeGenerics: Map<String, List<TypeName>>?,
        typeParameter: List<TypeVariableName>,
        arguments: List<TypeName>,
        proxyName: String,
        proxySignature: TypeName,
    ) = guardedCollect {
        if (isSuspending) {

        } else {
            collectSyncMethod(
                methodName = methodName,
                proxyName = proxyName,
                classScopeGenerics = classScopeGenerics,
                typeParameter = typeParameter,
                arguments = arguments,
                proxySignature = proxySignature
            )
        }
    }

    private fun determineEntry(member: Member): String {
        return when (member) {
            is Member.Property -> {
                "\n\"${member.propertyName}|property\" to ${member.proxyName},"
            }
            is Member.SyncFunProxy -> {
                val sideEffect = member.sideEffect.replace(" ", "·")
                "\n\"${member.methodName}|$sideEffect\" to ${member.proxyName},"
            }
        }
    }

    private fun extractReferenceStoreEntries(): String {
        val entries = StringBuilder()

        properties.forEach { property ->
            entries.append(determineEntry(property))
        }

        syncFun.values.forEach { sycFuns ->
            sycFuns.forEach { sycFun ->
                entries.append(determineEntry(sycFun))
            }
        }

        return entries.toString()
    }

    override fun createReferenceStorage(): PropertySpec {
        val referenceStorage = PropertySpec.builder(
            REFERENCE_STORE,
            referenceStoreType,
            KModifier.PRIVATE
        )

        referenceStorage.initializer(
            "mapOf(%L\n)",
            extractReferenceStoreEntries()
        )

        return referenceStorage.build()
    }

    private fun createPropertyAccessMethod(): FunSpec {
        return FunSpec.builder("propertyProxyOf")
            .returns(propertyProxy)
            .addParameter("reference", kProperty)
            .addTypeVariable(propertyType)
            .addStatement(
                REFERENCE_STORE_ACCESS,
                "\${reference.name}|property",
                propertyProxy,
            )
            .addAnnotation(UNCHECKED)
            .build()
    }

    private fun createJvmName(accessType: String, id: Int): AnnotationSpec {
        return AnnotationSpec
            .builder(safeJvmName)
            .addMember("%S", "$accessType$id")
            .build()
    }

    private fun TypeName.syncFunProxyToFunProxy(): TypeName {
        val type = this.toString().replace("Sync", "")
        return TypeVariableName(
            name = type
        ).copy(nullable = false)
    }

    private fun createSyncAccessMethod(
        syncAccess: Member.SyncFunProxy,
        id: Int,
    ): FunSpec {
        val agnosticSignature = syncAccess.proxySignature.syncFunProxyToFunProxy()

        return FunSpec.builder("syncFunProxyOf")
            .returns(agnosticSignature)
            .addParameter("reference", TypeVariableName(syncAccess.sideEffect))
            .addStatement(
                REFERENCE_STORE_ACCESS,
                "\${(reference as $kFunction).name}|${syncAccess.sideEffect}",
                agnosticSignature
            )
            .addAnnotation(UNCHECKED)
            .addAnnotation(createJvmName("syncFunProxyOf", id))
            .build()
    }

    override fun createAccessMethods(): List<FunSpec> {
        val accessMethods: MutableList<FunSpec> = mutableListOf()

        if (properties.isNotEmpty()) {
            accessMethods.add(createPropertyAccessMethod())
        }

        syncFun.values.forEachIndexed { idx, proxyGroup ->
            accessMethods.add(
                createSyncAccessMethod(proxyGroup.first(), idx)
            )
        }

        return accessMethods
    }

    companion object : ProxyAccessMethodGeneratorFactory {
        private const val REFERENCE_STORE = "referenceStore"
        private const val REFERENCE_STORE_ACCESS = "return $REFERENCE_STORE[%P] as %L"
        private val safeJvmName = ClassName(
            Mock::class.java.packageName,
            "SafeJvmName"
        )
        private val starParameter = TypeVariableName("*")
        private val referenceStoreType = Map::class.asClassName().parameterizedBy(
            String::class.asTypeName(),
            Proxy::class.asTypeName().parameterizedBy(
                starParameter,
                starParameter
            ),
        )
        private val propertyType = TypeVariableName("Property")
        private val kProperty = KProperty::class.asClassName().parameterizedBy(propertyType)
        private val kFunction = KFunction::class.asClassName().parameterizedBy(starParameter)
        private val propertyProxy = PropertyProxy::class.asClassName().parameterizedBy(propertyType)

        override fun getInstance(enableGenerator: Boolean): ProxyAccessMethodGenerator {
            return KMockProxyAccessMethodGenerator(enableGenerator)
        }
    }
}
