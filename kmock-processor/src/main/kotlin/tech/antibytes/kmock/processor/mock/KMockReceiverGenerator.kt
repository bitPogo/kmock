/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MethodReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.ReceiverGenerator
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.MethodeGeneratorHelper

internal class KMockReceiverGenerator(
    private val utils: MethodeGeneratorHelper,
    private val nameSelector: ProxyNameSelector,
    private val genericResolver: GenericResolver,
) : ReceiverGenerator {
    private fun KSPropertyDeclaration.determineReceiver(
        receiverTypeResolver: TypeParameterResolver,
        typeResolver: TypeParameterResolver,
    ): MethodTypeInfo {
        val receiver = this.resolveReceiver(
            typeResolver = typeResolver,
            receiverTypeResolver = receiverTypeResolver,
        )

        return MethodTypeInfo(
            argumentName = "receiver",
            typeName = receiver,
            isVarArg = false,
        )
    }

    private fun buildPropertyReceiver(
        getterProxy: ProxyInfo,
        setterProxy: ProxyInfo?,
        receiver: TypeName,
        returnType: MethodReturnTypeInfo,
        typeParameter: List<TypeVariableName>,
    ): PropertySpec {
        val property = PropertySpec.builder(
            getterProxy.templateName,
            returnType.typeName,
            KModifier.OVERRIDE
        ).receiver(receiver)

        if (typeParameter.isNotEmpty()) {
            property.addTypeVariables(typeParameter)
        }

        if (returnType.needsCastForReceiverProperty()) {
            property.addAnnotation(unchecked)
        }

        val cast = returnType.resolveCastForReceiverProperty()

        property.getter(
            FunSpec.getterBuilder()
                .addCode("return ${getterProxy.proxyName}.invoke(this@${getterProxy.templateName})$cast")
                .build()
        )

        if (setterProxy != null) {
            property.mutable(true)
            property.setter(
                FunSpec.setterBuilder()
                    .addParameter("value", returnType.typeName)
                    .addStatement("${setterProxy.proxyName}.invoke(this@${setterProxy.templateName}, value)")
                    .build()
            )
        }

        return property.build()
    }

    private fun resolveSetterProxy(
        qualifier: String,
        propertyType: KSType,
        propertyName: String,
        isMutable: Boolean,
        receiverInfo: MethodTypeInfo,
        generics: Map<String, List<KSTypeReference>>?,
        classScopeGenerics: Map<String, List<TypeName>>?,
        receiverTypeResolver: TypeParameterResolver,
    ): Pair<ProxyInfo?, PropertySpec?> {
        return if (isMutable) {
            val setterProxyInfo = nameSelector.selectReceiverSetterName(
                qualifier = qualifier,
                propertyName = propertyName,
                receiver = receiverInfo,
                generics = generics ?: emptyMap(),
                typeResolver = receiverTypeResolver,
            )

            val (setterProxy, _) = utils.buildProxy(
                proxyInfo = setterProxyInfo,
                arguments = arrayOf(receiverInfo),
                suspending = false,
                classScopeGenerics = classScopeGenerics,
                generics = generics ?: emptyMap(),
                returnType = propertyType,
                typeResolver = receiverTypeResolver,
            )

            return Pair(setterProxyInfo, setterProxy)
        } else {
            emptySetter
        }
    }

    override fun buildPropertyBundle(
        qualifier: String,
        classScopeGenerics: Map<String, List<TypeName>>?,
        ksProperty: KSPropertyDeclaration,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): Triple<PropertySpec, PropertySpec?, PropertySpec> {
        val propertyName = ksProperty.simpleName.asString()
        val receiverTypeResolver = ksProperty.toReceiverTypeParameterResolver(typeResolver)
        val propertyType = ksProperty.type.resolve()
        val receiverInfo = ksProperty.determineReceiver(
            receiverTypeResolver = receiverTypeResolver,
            typeResolver = typeResolver,
        )
        val generics = genericResolver.extractGenerics(ksProperty, receiverTypeResolver) ?: emptyMap()
        val isMutable = ksProperty.isMutable

        val getterProxyInfo = nameSelector.selectReceiverGetterName(
            qualifier = qualifier,
            propertyName = propertyName,
            receiver = receiverInfo,
            generics = generics,
            typeResolver = receiverTypeResolver,
        )

        val (getter, getterReturnType) = utils.buildProxy(
            proxyInfo = getterProxyInfo,
            arguments = arrayOf(receiverInfo),
            suspending = false,
            classScopeGenerics = classScopeGenerics,
            generics = generics,
            returnType = propertyType,
            typeResolver = receiverTypeResolver,
        )

        val (setterProxyInfo, setter) = resolveSetterProxy(
            qualifier = qualifier,
            propertyType = propertyType,
            propertyName = propertyName,
            isMutable = isMutable,
            receiverInfo = receiverInfo,
            generics = generics,
            classScopeGenerics = classScopeGenerics,
            receiverTypeResolver = receiverTypeResolver,
        )

        val property = buildPropertyReceiver(
            getterProxy = getterProxyInfo,
            setterProxy = setterProxyInfo,
            receiver = receiverInfo.typeName,
            returnType = getterReturnType,
            typeParameter = genericResolver.mapDeclaredGenerics(generics, receiverTypeResolver)
        )

        return Triple(getter, setter, property,)
    }

    private companion object {
        private val unchecked = AnnotationSpec.builder(Suppress::class).addMember("%S", "UNCHECKED_CAST").build()
        private val emptySetter = Pair(null, null)
    }
}
