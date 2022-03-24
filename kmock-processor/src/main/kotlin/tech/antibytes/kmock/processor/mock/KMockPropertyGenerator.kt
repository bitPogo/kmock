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
    private val immutableTemplate = "ProxyFactory.createPropertyProxy(%S, spyOnGet = %L, collector = verifier, freeze = freeze, %L)"
    private val mutableTemplate = "ProxyFactory.createPropertyProxy(%S, spyOnGet = %L, spyOnSet = %L, collector = verifier, freeze = freeze, %L)"

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

    private fun buildImmutablePropertyProxy(
        proxyId: String,
        propertyName: String,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): CodeBlock {
        val initializer = CodeBlock.builder()
        if (enableSpy) {
            initializer.beginControlFlow("if (spyOn == null)")
            initializer.add(
                immutableTemplate,
                proxyId,
                "null",
                relaxerGenerator.buildRelaxers(relaxer, false)
            )
            initializer.nextControlFlow("else")
            initializer.add(
                immutableTemplate,
                proxyId,
                "{ __spyOn!!.$propertyName }",
                relaxerGenerator.buildRelaxers(relaxer, false)
            )
            initializer.endControlFlow()
        } else {
            initializer.add(
                immutableTemplate,
                proxyId,
                "null",
                relaxerGenerator.buildRelaxers(relaxer, false)
            )
        }

        return initializer.build()
    }

    private fun buildMutablePropertyProxy(
        proxyId: String,
        propertyName: String,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): CodeBlock {
        val initializer = CodeBlock.builder()

        if (enableSpy) {
            initializer.beginControlFlow("if (spyOn == null)")
            initializer.add(
                mutableTemplate,
                proxyId,
                "null",
                "null",
                relaxerGenerator.buildRelaxers(relaxer, false)
            )
            initializer.nextControlFlow("else")
            initializer.add(
                mutableTemplate,
                proxyId,
                "{ __spyOn!!.$propertyName }",
                "{ __spyOn!!.$propertyName = it; Unit }",
                relaxerGenerator.buildRelaxers(relaxer, false)
            )
            initializer.endControlFlow()
        } else {
            initializer.add(
                mutableTemplate,
                proxyId,
                "null",
                "null",
                relaxerGenerator.buildRelaxers(relaxer, false)
            )
        }

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

        return if (!isMutable) {
            propertyProxy.initializer(
                buildImmutablePropertyProxy(
                    proxyId = proxyId,
                    propertyName = propertyName,
                    enableSpy = enableSpy,
                    relaxer = relaxer
                )
            )
        } else {
            propertyProxy.initializer(
                buildMutablePropertyProxy(
                    proxyId = proxyId,
                    propertyName = propertyName,
                    enableSpy = enableSpy,
                    relaxer = relaxer
                )
            )
        }
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
