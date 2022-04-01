/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

internal class KMockPropertyGenerator(
    private val relaxerGenerator: ProcessorContract.RelaxerGenerator
) : ProcessorContract.PropertyGenerator {
    private val proxy = PropertyProxy::class.asClassName()
    private val template = "ProxyFactory.createPropertyProxy(%S, collector = verifier, freeze = freeze) %L"

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

    private fun addSpies(
        relaxerDefinitions: StringBuilder,
        propertyName: String,
        isMutable: Boolean,
    ) {
        relaxerDefinitions.append("useSpyOnGetIf(__spyOn) { __spyOn!!.$propertyName }\n")

        if (isMutable) {
            relaxerDefinitions.append("useSpyOnSetIf(__spyOn) { value -> __spyOn!!.$propertyName = value }\n")
        }
    }

    private fun addRelaxation(
        propertyName: String,
        enableSpy: Boolean,
        isMutable: Boolean,
        relaxer: Relaxer?
    ): String {
        val relaxationDefinitions = StringBuilder(3)

        relaxationDefinitions.append("{\n")
        if (enableSpy) {
            addSpies(relaxationDefinitions, propertyName, isMutable)
        }

        relaxerGenerator.addRelaxer(relaxationDefinitions, relaxer)
        relaxationDefinitions.append("}")

        return if (relaxationDefinitions.length == 3) {
            ""
        } else {
            relaxationDefinitions.toString()
        }
    }

    private fun buildPropertyProxy(
        proxyId: String,
        propertyName: String,
        enableSpy: Boolean,
        isMutable: Boolean,
        relaxer: Relaxer?
    ): CodeBlock {
        val initializer = CodeBlock.builder()
        initializer.add(
            template,
            proxyId,
            addRelaxation(
                propertyName = propertyName,
                enableSpy = enableSpy,
                isMutable = isMutable,
                relaxer = relaxer
            )
        )

        return initializer.build()
    }

    private fun determinePropertyInitializer(
        propertyProxy: PropertySpec.Builder,
        qualifier: String,
        propertyName: String,
        isMutable: Boolean,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): PropertySpec.Builder {
        val proxyId = "$qualifier#_$propertyName"

        return propertyProxy.initializer(
            buildPropertyProxy(
                proxyId = proxyId,
                propertyName = propertyName,
                enableSpy = enableSpy,
                isMutable = isMutable,
                relaxer = relaxer
            )
        )
    }

    private fun buildPropertyProxy(
        qualifier: String,
        propertyName: String,
        propertyType: TypeName,
        isMutable: Boolean,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): PropertySpec {
        val property = PropertySpec.builder(
            "_$propertyName",
            proxy.parameterizedBy(propertyType),
        )

        return determinePropertyInitializer(
            propertyProxy = property,
            qualifier = qualifier,
            propertyName = propertyName,
            isMutable = isMutable,
            enableSpy = enableSpy,
            relaxer = relaxer
        ).build()
    }

    override fun buildPropertyBundle(
        qualifier: String,
        ksProperty: KSPropertyDeclaration,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): Pair<PropertySpec, PropertySpec> {
        val propertyName = ksProperty.simpleName.asString()
        val propertyType = ksProperty.type.toTypeName(typeResolver)
        val isMutable = ksProperty.isMutable

        return Pair(
            buildPropertyProxy(
                qualifier = qualifier,
                propertyName = propertyName,
                propertyType = propertyType,
                isMutable = isMutable,
                enableSpy = enableSpy,
                relaxer = relaxer,
            ),
            buildProperty(propertyName, propertyType, isMutable),
        )
    }
}
