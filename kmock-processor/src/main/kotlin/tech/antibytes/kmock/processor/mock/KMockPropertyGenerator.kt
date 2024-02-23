/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
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
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.CREATE_PROPERTY_PROXY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FREEZE_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.PROXY_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.VALUE
import tech.antibytes.kmock.processor.ProcessorContract.MemberReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.NonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.ProcessorContract.PropertyGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.kotlinpoet.toTypeName

internal class KMockPropertyGenerator(
    private val nameSelector: ProxyNameSelector,
    private val nonIntrusiveInvocationGenerator: NonIntrusiveInvocationGenerator,
) : PropertyGenerator {
    private fun FunSpec.Builder.addGetterInvocation(
        propertyName: String,
        returnType: MemberReturnTypeInfo,
        enableSpy: Boolean,
        relaxer: Relaxer?,
    ): FunSpec.Builder {
        val nonIntrusive = nonIntrusiveInvocationGenerator.buildGetterNonIntrusiveInvocation(
            propertyType = returnType,
            enableSpy = enableSpy,
            propertyName = propertyName.drop(1),
            relaxer = relaxer,
        )

        val statement = if (nonIntrusive.isNotEmpty()) {
            "return $propertyName.executeOnGet$nonIntrusive"
        } else {
            "return $propertyName.executeOnGet()"
        }

        return this.addStatement(statement)
    }

    private fun buildGetter(
        propertyName: String,
        returnType: MemberReturnTypeInfo,
        enableSpy: Boolean,
        relaxer: Relaxer?,
    ): FunSpec {
        return FunSpec
            .getterBuilder()
            .addGetterInvocation(
                propertyName = propertyName,
                returnType = returnType,
                enableSpy = enableSpy,
                relaxer = relaxer,
            )
            .build()
    }

    private fun FunSpec.Builder.addSetterInvocation(
        propertyName: String,
        enableSpy: Boolean,
    ): FunSpec.Builder {
        val nonIntrusive = nonIntrusiveInvocationGenerator.buildSetterNonIntrusiveInvocation(
            enableSpy = enableSpy,
            propertyName = propertyName.drop(1),
        )

        val statement = if (nonIntrusive.isNotEmpty()) {
            "return $propertyName.executeOnSet($VALUE)$nonIntrusive"
        } else {
            "return $propertyName.executeOnSet($VALUE)"
        }

        return this.addStatement(statement)
    }

    private fun buildSetter(
        propertyName: String,
        propertyType: TypeName,
        enableSpy: Boolean,
    ): FunSpec {
        return FunSpec
            .setterBuilder()
            .addParameter(VALUE, propertyType)
            .addSetterInvocation(propertyName, enableSpy)
            .build()
    }

    private fun buildPropertyBody(
        property: PropertySpec.Builder,
        proxyInfo: ProxyInfo,
        propertyType: TypeName,
        returnType: MemberReturnTypeInfo,
        isMutable: Boolean,
        enableSpy: Boolean,
        relaxer: Relaxer?,
    ) {
        if (isMutable) {
            property.mutable(true)
            property.setter(
                buildSetter(
                    propertyName = proxyInfo.proxyName,
                    propertyType = propertyType,
                    enableSpy = enableSpy,
                ),
            )
        }

        property
            .addModifiers(KModifier.PUBLIC)
            .getter(
                buildGetter(
                    propertyName = proxyInfo.proxyName,
                    returnType = returnType,
                    enableSpy = enableSpy,
                    relaxer = relaxer,
                ),
            )
    }

    private fun buildProperty(
        proxyInfo: ProxyInfo,
        propertyType: TypeName,
        returnType: MemberReturnTypeInfo,
        isMutable: Boolean,
        enableSpy: Boolean,
        relaxer: Relaxer?,
    ): PropertySpec {
        val property = PropertySpec.builder(
            proxyInfo.templateName,
            propertyType,
            KModifier.OVERRIDE,
            KModifier.PUBLIC,
        )

        buildPropertyBody(
            property = property,
            proxyInfo = proxyInfo,
            propertyType = propertyType,
            returnType = returnType,
            isMutable = isMutable,
            enableSpy = enableSpy,
            relaxer = relaxer,
        )

        return property.build()
    }

    private fun buildPropertyProxy(
        proxyInfo: ProxyInfo,
    ): CodeBlock {
        val initializer = CodeBlock.builder()
        initializer.add(
            template,
            proxyInfo.proxyId,
        )

        return initializer.build()
    }

    private fun determinePropertyInitializer(
        propertyProxy: PropertySpec.Builder,
        proxyInfo: ProxyInfo,
    ): PropertySpec.Builder {
        return propertyProxy.initializer(
            buildPropertyProxy(
                proxyInfo = proxyInfo,
            ),
        )
    }

    private fun buildPropertyProxy(
        proxyInfo: ProxyInfo,
        propertyType: TypeName,
    ): PropertySpec {
        val property = PropertySpec.builder(
            proxyInfo.proxyName,
            proxy.parameterizedBy(propertyType),
        )

        return determinePropertyInitializer(
            propertyProxy = property,
            proxyInfo = proxyInfo,
        ).build()
    }

    override fun buildPropertyBundle(
        qualifier: String,
        classScopeGenerics: Map<String, List<TypeName>>?,
        ksProperty: KSPropertyDeclaration,
        classWideResolver: TypeParameterResolver,
        enableSpy: Boolean,
        relaxer: Relaxer?,
    ): Pair<PropertySpec, PropertySpec> {
        val propertyName = ksProperty.simpleName.asString()
        val propertyType = ksProperty.type.toTypeName(classWideResolver)
        val isMutable = ksProperty.isMutable
        val proxyInfo = nameSelector.selectPropertyName(
            qualifier = qualifier,
            propertyName = propertyName,
        )
        val returnType = MemberReturnTypeInfo(
            methodTypeName = propertyType,
            proxyTypeName = propertyType,
            generic = null,
            classScope = classScopeGenerics,
        )

        return Pair(
            buildPropertyProxy(
                proxyInfo = proxyInfo,
                propertyType = propertyType,
            ),
            buildProperty(
                proxyInfo = proxyInfo,
                propertyType = propertyType,
                returnType = returnType,
                isMutable = isMutable,
                enableSpy = enableSpy,
                relaxer = relaxer,
            ),
        )
    }

    private companion object {
        private val proxy = PropertyProxy::class.asClassName()
        private val template = "${PROXY_FACTORY.simpleName}.$CREATE_PROPERTY_PROXY(%S, $COLLECTOR_ARGUMENT = $COLLECTOR_ARGUMENT, $FREEZE_ARGUMENT = $FREEZE_ARGUMENT)"
    }
}
