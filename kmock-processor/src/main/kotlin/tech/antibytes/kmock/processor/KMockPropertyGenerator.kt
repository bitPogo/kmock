/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName
import tech.antibytes.kmock.KMockContract

internal class KMockPropertyGenerator(
    private val relaxerGenerator: ProcessorContract.RelaxerGenerator
) : ProcessorContract.PropertyGenerator {
    private fun buildGetter(propertyName: String): FunSpec {
        return FunSpec
            .getterBuilder()
            .addStatement("return _$propertyName.onGet()")
            .build()
    }

    private fun buildSetter(
        propertyName: String,
        propertyType: TypeName
    ): FunSpec {
        return FunSpec
            .setterBuilder()
            .addParameter("value", propertyType)
            .addStatement("return _$propertyName.onSet(value)")
            .build()
    }

    private fun buildProperty(
        propertyName: String,
        propertyType: TypeName,
        isMutable: Boolean
    ): PropertySpec {
        val property = PropertySpec.builder(
            propertyName,
            propertyType,
            KModifier.OVERRIDE
        )

        if (isMutable) {
            property.mutable(true)
            property.setter(buildSetter(propertyName, propertyType))
        }

        return property
            .getter(buildGetter(propertyName))
            .build()
    }

    private fun determinePropertyInitializer(
        propertyProxy: PropertySpec.Builder,
        qualifier: String,
        propertyName: String,
        isMutable: Boolean,
        relaxer: ProcessorContract.Relaxer?
    ): PropertySpec.Builder {
        val name = "$qualifier#_$propertyName"

        return if (!isMutable) {
            propertyProxy.initializer(
                """
                    |if (spyOn == null) {
                    |   PropertyProxy(%S, spyOnGet = %L, collector = verifier, freeze = freeze, %L)
                    |} else {
                    |   PropertyProxy(%S, spyOnGet = %L, collector = verifier, freeze = freeze, %L)
                    |}
                |
                """.trimMargin(),
                name,
                "null",
                relaxerGenerator.buildRelaxers(relaxer, false),
                name,
                "{ spyOn.$propertyName }",
                relaxerGenerator.buildRelaxers(relaxer, false)
            )
        } else {
            propertyProxy.initializer(
                """
                |if (spyOn == null) {
                |   PropertyProxy(%S, spyOnGet = %L, spyOnSet = %L, collector = verifier, freeze = freeze, %L)
                |} else {
                |   PropertyProxy(%S, spyOnGet = %L, spyOnSet = %L, collector = verifier, freeze = freeze, %L)
                |}
                |
                """.trimMargin(),
                name,
                "null",
                "null",
                relaxerGenerator.buildRelaxers(relaxer, false),
                name,
                "{ spyOn.$propertyName }",
                "{ spyOn.$propertyName = it; Unit }",
                relaxerGenerator.buildRelaxers(relaxer, false)
            )
        }
    }

    private fun buildPropertyProxy(
        qualifier: String,
        propertyName: String,
        propertyType: TypeName,
        isMutable: Boolean,
        relaxer: ProcessorContract.Relaxer?
    ): PropertySpec {
        val property = PropertySpec.builder(
            "_$propertyName",
            KMockContract.PropertyProxy::class
                .asClassName()
                .parameterizedBy(propertyType),
        )

        return determinePropertyInitializer(
            property,
            qualifier,
            propertyName,
            isMutable,
            relaxer
        ).build()
    }

    override fun buildPropertyBundle(
        qualifier: String,
        ksProperty: KSPropertyDeclaration,
        typeResolver: TypeParameterResolver,
        relaxer: ProcessorContract.Relaxer?
    ): Pair<PropertySpec, PropertySpec> {
        val propertyName = ksProperty.simpleName.asString()
        val propertyType = ksProperty.type.toTypeName(typeResolver)
        val isMutable = ksProperty.isMutable

        return Pair(
            buildPropertyProxy(qualifier, propertyName, propertyType, isMutable, relaxer),
            buildProperty(propertyName, propertyType, isMutable),
        )
    }
}
