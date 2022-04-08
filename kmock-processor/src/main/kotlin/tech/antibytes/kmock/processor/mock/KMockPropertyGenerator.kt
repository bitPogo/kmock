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

internal class KMockPropertyGenerator(
    private val nameSelector: ProxyNameSelector,
    private val relaxerGenerator: RelaxerGenerator
) : PropertyGenerator {
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
        proxyInfo: ProxyInfo,
        enableSpy: Boolean,
        isMutable: Boolean,
        relaxer: Relaxer?
    ): String {
        return relaxerGenerator.addPropertyRelaxation(
            relaxer = relaxer
        ) { relaxationDefinitions ->
            if (enableSpy) {
                addSpies(
                    relaxerDefinitions = relaxationDefinitions,
                    propertyName = proxyInfo.templateName,
                    isMutable = isMutable
                )
            }
        }
    }

    private fun buildPropertyProxy(
        proxyInfo: ProxyInfo,
        enableSpy: Boolean,
        isMutable: Boolean,
        relaxer: Relaxer?
    ): CodeBlock {
        val initializer = CodeBlock.builder()
        initializer.add(
            template,
            proxyInfo.proxyId,
            addRelaxation(
                proxyInfo = proxyInfo,
                enableSpy = enableSpy,
                isMutable = isMutable,
                relaxer = relaxer
            )
        )

        return initializer.build()
    }

    private fun determinePropertyInitializer(
        propertyProxy: PropertySpec.Builder,
        proxyInfo: ProxyInfo,
        isMutable: Boolean,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): PropertySpec.Builder {
        return propertyProxy.initializer(
            buildPropertyProxy(
                proxyInfo = proxyInfo,
                enableSpy = enableSpy,
                isMutable = isMutable,
                relaxer = relaxer
            )
        )
    }

    private fun buildPropertyProxy(
        proxyInfo: ProxyInfo,
        propertyType: TypeName,
        isMutable: Boolean,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): PropertySpec {
        val property = PropertySpec.builder(
            proxyInfo.proxyName,
            proxy.parameterizedBy(propertyType),
        )

        return determinePropertyInitializer(
            propertyProxy = property,
            proxyInfo = proxyInfo,
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
        val proxyInfo = nameSelector.selectPropertyName(
            qualifier = qualifier,
            propertyName = propertyName
        )

        return Pair(
            buildPropertyProxy(
                proxyInfo = proxyInfo,
                propertyType = propertyType,
                isMutable = isMutable,
                enableSpy = enableSpy,
                relaxer = relaxer,
            ),
            buildProperty(propertyName, propertyType, isMutable),
        )
    }
}
