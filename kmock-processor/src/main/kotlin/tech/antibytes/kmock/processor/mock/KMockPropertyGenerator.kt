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
import com.squareup.kotlinpoet.TypeVariableName
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
    private val scopedBody = "throw IllegalStateException(\"This action is not callable.\")"
    private val scopedGetter = FunSpec.getterBuilder().addStatement(scopedBody)

    private fun FunSpec.Builder.addGetterInvocation(
        propertyName: String,
        enableSpy: Boolean
    ): FunSpec.Builder {
        val statement = if (enableSpy) {
            "return $propertyName.onGet ${spyGenerator.buildGetterSpy(propertyName.drop(1))}"
        } else {
            "return $propertyName.onGet()"
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
            "return $propertyName.onSet(value)${spyGenerator.buildSetterSpy(propertyName.drop(1))}"
        } else {
            "return $propertyName.onSet(value)"
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

    private fun buildPropertyBody(
        property: PropertySpec.Builder,
        proxyInfo: ProxyInfo,
        propertyType: TypeName,
        isMutable: Boolean,
        enableSpy: Boolean
    ) {
        if (isMutable) {
            property.mutable(true)
            property.setter(
                buildSetter(
                    propertyName = proxyInfo.proxyName,
                    propertyType = propertyType,
                    enableSpy = enableSpy,
                )
            )
        }

        property
            .getter(
                buildGetter(
                    propertyName = proxyInfo.proxyName,
                    enableSpy = enableSpy,
                )
            )
    }

    private fun buildShallowPropertyBody(
        property: PropertySpec.Builder,
        propertyType: TypeName,
        isMutable: Boolean,
    ) {
        if (isMutable) {
            property.mutable(true)
            property.setter(
                FunSpec.setterBuilder()
                    .addStatement(scopedBody)
                    .addParameter("value", propertyType)
                    .build()
            )
        }

        property.getter(scopedGetter.build())
    }

    private fun buildProperty(
        propertyScope: TypeName?,
        proxyInfo: ProxyInfo,
        propertyType: TypeName,
        isMutable: Boolean,
        enableSpy: Boolean
    ): PropertySpec {
        val property = PropertySpec.builder(
            proxyInfo.templateName,
            propertyType,
            KModifier.OVERRIDE
        ).receiver(propertyScope)

        if (propertyScope is TypeVariableName) {
            property.addTypeVariable(propertyScope)
        }

        if (propertyScope != null) {
            buildShallowPropertyBody(
                property = property,
                propertyType = propertyType,
                isMutable = isMutable
            )
        } else {
            buildPropertyBody(
                property = property,
                proxyInfo = proxyInfo,
                propertyType = propertyType,
                isMutable = isMutable,
                enableSpy = enableSpy,
            )
        }

        return property.build()
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
        propertyScope: TypeName?,
        proxyInfo: ProxyInfo,
        propertyType: TypeName,
        relaxer: Relaxer?
    ): PropertySpec? {
        return if (propertyScope == null) {
            val property = PropertySpec.builder(
                proxyInfo.proxyName,
                proxy.parameterizedBy(propertyType),
            )

            return determinePropertyInitializer(
                propertyProxy = property,
                proxyInfo = proxyInfo,
                relaxer = relaxer
            ).build()
        } else {
            null
        }
    }

    override fun buildPropertyBundle(
        qualifier: String,
        ksProperty: KSPropertyDeclaration,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): Pair<PropertySpec?, PropertySpec> {
        val propertyScope = ksProperty.determineScope()
        val propertyName = ksProperty.simpleName.asString()
        val propertyType = ksProperty.type.toTypeName(typeResolver)
        val isMutable = ksProperty.isMutable
        val proxyInfo = nameSelector.selectPropertyName(
            qualifier = qualifier,
            propertyName = propertyName
        )

        return Pair(
            buildPropertyProxy(
                propertyScope = propertyScope,
                proxyInfo = proxyInfo,
                propertyType = propertyType,
                relaxer = relaxer,
            ),
            buildProperty(
                propertyScope = propertyScope,
                proxyInfo = proxyInfo,
                propertyType = propertyType,
                isMutable = isMutable,
                enableSpy = enableSpy,
            ),
        )
    }
}
