/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.AnnotationSpec
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
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNCHECKED
import tech.antibytes.kmock.processor.utils.titleCase
import kotlin.reflect.KProperty

internal class KMockProxyAccessMethodGenerator private constructor(
    private val enabled: Boolean
) : ProxyAccessMethodGenerator {
    private sealed class Member {
        data class Property(
            val propertyName: String,
            val propertyType: TypeName,
            val proxyName: String,
            val proxySignature: TypeName
        ) : Member()
    }

    private val properties: MutableMap<String, MutableList<Member.Property>> = mutableMapOf()

    private fun guardedCollect(action: () -> Unit) {
        if (enabled) {
            action()
        }
    }

    override fun collectProperty(
        propertyName: String,
        propertyType: TypeName,
        proxyName: String,
        proxySignature: TypeName
    ) = guardedCollect {
        val key = propertyType.toString()
        val registry = properties.getOrElse(key) { mutableListOf() }
        registry.add(
            Member.Property(
                propertyName = propertyName,
                propertyType = propertyType,
                proxyName = proxyName,
                proxySignature = proxySignature
            )
        )

        properties[key] = registry
    }

    override fun collectMethod(methodName: String, proxyName: String, isSuspending: Boolean, proxySignature: String) {
        TODO("Not yet implemented")
    }

    private fun determineEntry(member: Member): String {
        return when (member) {
            is Member.Property -> {
                "\n\"${member.propertyName}|property\" to ${member.proxyName},"
            }
        }
    }

    private fun extractReferenceStoreEntries(): String {
        val entries = StringBuilder()

        properties.forEach { (_, propertyGroup) ->
            propertyGroup.forEach { member ->
                entries.append(determineEntry(member))
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
                "return $REFERENCE_STORE[\"\${reference.name}|property\"] as %L",
                propertyProxy,
            )
            .addAnnotation(UNCHECKED)
            .build()
    }

    override fun createAccessMethods(): List<FunSpec> {
        val accessMethods: MutableList<FunSpec> = mutableListOf()

        if (properties.isNotEmpty()) {
            accessMethods.add(createPropertyAccessMethod())
        }

        return accessMethods
    }

    companion object : ProxyAccessMethodGeneratorFactory {
        private const val REFERENCE_STORE = "referenceStore"
        private val referenceStoreType = Map::class.asClassName().parameterizedBy(
            String::class.asTypeName(),
            Proxy::class.asTypeName().parameterizedBy(
                TypeVariableName("*"),
                TypeVariableName("*")
            ),
        )
        private val propertyType = TypeVariableName("Property")
        private val kProperty = KProperty::class.asClassName().parameterizedBy(propertyType)
        private val propertyProxy = PropertyProxy::class.asClassName().parameterizedBy(propertyType)

        override fun getInstance(enableGenerator: Boolean): ProxyAccessMethodGenerator {
            return KMockProxyAccessMethodGenerator(enableGenerator)
        }
    }
}
