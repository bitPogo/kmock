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
import tech.antibytes.kmock.processor.ProcessorContract.PropertyGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.RelaxerGenerator
import tech.antibytes.kmock.processor.ProcessorContract.SpyGenerator

internal class KMockPropertyGenerator(
    private val spyGenerator: SpyGenerator,
    private val nameSelector: ProxyNameSelector,
    private val relaxerGenerator: RelaxerGenerator
) : PropertyGenerator {
    private val proxy = PropertyProxy::class.asClassName()
    private val template = "ProxyFactory.createPropertyProxy(%S, collector = verifier, freeze = freeze) %L"

    private fun FunSpec.Builder.addGetterInvocation(
        propertyName: String,
        enableSpy: Boolean
    ): FunSpec.Builder {
        val statement = if (enableSpy) {
            "return _$propertyName.onGet ${spyGenerator.buildGetterSpy(propertyName)}"
        } else {
            "return _$propertyName.onGet()"
        }

        return this.addStatement(statement)
    }

    private fun buildGetter(
        propertyName: String,
        enableSpy: Boolean,
    ): FunSpec {
        return FunSpec
            .getterBuilder()
            .addGetterInvocation(propertyName, enableSpy)
            .build()
    }

    private fun FunSpec.Builder.addSetterInvocation(
        propertyName: String,
        enableSpy: Boolean
    ): FunSpec.Builder {
        val statement = if (enableSpy) {
            "return _$propertyName.onSet(value)${spyGenerator.buildSetterSpy(propertyName)}"
        } else {
            "return _$propertyName.onSet(value)"
        }

        return this.addStatement(statement)
    }

    private fun buildSetter(
        propertyName: String,
        propertyType: TypeName,
        enableSpy: Boolean
    ): FunSpec {
        return FunSpec
            .setterBuilder()
            .addParameter("value", propertyType)
            .addSetterInvocation(propertyName, enableSpy)
            .build()
    }

    private fun buildProperty(
        propertyName: String,
        propertyType: TypeName,
        isMutable: Boolean,
        enableSpy: Boolean
    ): PropertySpec {
        val property = PropertySpec.builder(
            propertyName,
            propertyType,
            KModifier.OVERRIDE
        )

        if (isMutable) {
            property.mutable(true)
            property.setter(
                buildSetter(
                    propertyName = propertyName,
                    propertyType = propertyType,
                    enableSpy = enableSpy,
                )
            )
        }

        return property
            .getter(
                buildGetter(
                    propertyName = propertyName,
                    enableSpy = enableSpy,
                )
            )
            .build()
    }

    private fun buildPropertyProxy(
        proxyInfo: ProxyInfo,
        relaxer: Relaxer?
    ): CodeBlock {
        val initializer = CodeBlock.builder()
        initializer.add(
            template,
            proxyInfo.proxyId,
            relaxerGenerator.addPropertyRelaxation(relaxer)
        )

        return initializer.build()
    }

    private fun determinePropertyInitializer(
        propertyProxy: PropertySpec.Builder,
        proxyInfo: ProxyInfo,
        relaxer: Relaxer?
    ): PropertySpec.Builder {
        return propertyProxy.initializer(
            buildPropertyProxy(
                proxyInfo = proxyInfo,
                relaxer = relaxer
            )
        )
    }

    private fun buildPropertyProxy(
        proxyInfo: ProxyInfo,
        propertyType: TypeName,
        relaxer: Relaxer?
    ): PropertySpec {
        val property = PropertySpec.builder(
            proxyInfo.proxyName,
            proxy.parameterizedBy(propertyType),
        )

        return determinePropertyInitializer(
            propertyProxy = property,
            proxyInfo = proxyInfo,
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
        val proxyInfo = nameSelector.selectPropertyName(
            qualifier = qualifier,
            propertyName = propertyName
        )

        return Pair(
            buildPropertyProxy(
                proxyInfo = proxyInfo,
                propertyType = propertyType,
                relaxer = relaxer,
            ),
            buildProperty(
                propertyName = propertyName,
                propertyType = propertyType,
                isMutable = isMutable,
                enableSpy = enableSpy,
            ),
        )
    }
}
