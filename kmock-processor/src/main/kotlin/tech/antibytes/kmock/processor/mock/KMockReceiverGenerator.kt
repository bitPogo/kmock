/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNCHECKED
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MethodReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodeGeneratorHelper
import tech.antibytes.kmock.processor.ProcessorContract.NonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.ReceiverGenerator
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.utils.resolveReceiver
import tech.antibytes.kmock.processor.utils.toReceiverTypeParameterResolver

internal class KMockReceiverGenerator(
    private val utils: MethodeGeneratorHelper,
    private val nameSelector: ProxyNameSelector,
    private val nonIntrusiveInvocationGenerator: NonIntrusiveInvocationGenerator,
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

    private fun KSFunctionDeclaration.determineReceiver(
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
        receiver: MethodTypeInfo,
        returnType: MethodReturnTypeInfo,
        typeParameter: List<TypeVariableName>,
        relaxer: Relaxer?,
    ): PropertySpec {
        val property = PropertySpec.builder(
            getterProxy.templateName,
            returnType.typeName,
            KModifier.OVERRIDE
        ).receiver(receiver.typeName)

        if (typeParameter.isNotEmpty()) {
            property.addTypeVariables(typeParameter)
        }

        if (returnType.needsCastForReceiverProperty()) {
            property.addAnnotation(UNCHECKED)
        }

        val cast = returnType.resolveCastForReceiverProperty()

        val nonIntrusiveInvocation = nonIntrusiveInvocationGenerator.buildMethodNonIntrusiveInvocation(
            enableSpy = false,
            methodName = getterProxy.templateName,
            parameter = typeParameter,
            arguments = arrayOf(receiver),
            methodReturnType = returnType,
            relaxer = relaxer
        )

        property.getter(
            FunSpec.getterBuilder()
                .addCode(
                    "return %L.invoke(this@%L)%L%L",
                    getterProxy.proxyName,
                    getterProxy.templateName,
                    nonIntrusiveInvocation,
                    cast,
                )
                .build()
        )

        if (setterProxy != null) {
            property.mutable(true)
            property.setter(
                FunSpec.setterBuilder()
                    .addParameter("value", returnType.typeName)
                    .addStatement(
                        "%L.invoke(this@%L, value)",
                        setterProxy.proxyName,
                        setterProxy.templateName,
                    )
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
            receiver = receiverInfo,
            returnType = getterReturnType,
            typeParameter = genericResolver.mapDeclaredGenerics(generics, receiverTypeResolver),
            relaxer = relaxer
        )

        return Triple(getter, setter, property,)
    }

    private fun buildMethodBody(
        method: FunSpec.Builder,
        proxyInfo: ProxyInfo,
        enableSpy: Boolean,
        typeParameter: List<TypeName>,
        arguments: Array<MethodTypeInfo>,
        returnType: MethodReturnTypeInfo,
        relaxer: Relaxer?
    ) {
        if (returnType.needsCastAnnotation(relaxer = relaxer)) {
            method.addAnnotation(UNCHECKED)
        }

        val cast = returnType.resolveCastOnReturn()

        val invocation = arguments.joinToString(", ") { argument -> argument.argumentName }
        val nonIntrusiveInvocation = nonIntrusiveInvocationGenerator.buildMethodNonIntrusiveInvocation(
            enableSpy = false,
            methodName = proxyInfo.templateName,
            parameter = typeParameter,
            arguments = arguments,
            methodReturnType = returnType,
            relaxer = relaxer
        )

        method.addCode(
            "return %L.invoke(this@%L,%L)%L%L",
            proxyInfo.proxyName,
            proxyInfo.templateName,
            invocation,
            nonIntrusiveInvocation,
            cast,
        )
    }

    private fun buildMethod(
        proxyInfo: ProxyInfo,
        generics: Map<String, List<KSTypeReference>>?,
        isSuspending: Boolean,
        enableSpy: Boolean,
        receiverInfo: MethodTypeInfo,
        arguments: Array<MethodTypeInfo>,
        typeParameter: List<TypeName>,
        returnType: MethodReturnTypeInfo,
        typeResolver: TypeParameterResolver,
        relaxer: Relaxer?
    ): FunSpec {
        val method = FunSpec
            .builder(proxyInfo.templateName)
            .addModifiers(KModifier.OVERRIDE)
            .addArguments(arguments)
            .returns(returnType.typeName)
            .receiver(receiverInfo.typeName)

        if (generics != null) {
            method.typeVariables.addAll(
                genericResolver.mapDeclaredGenerics(generics, typeResolver)
            )
        }

        if (isSuspending) {
            method.addModifiers(KModifier.SUSPEND)
        }

        buildMethodBody(
            method = method,
            proxyInfo = proxyInfo,
            enableSpy = enableSpy,
            typeParameter = typeParameter,
            arguments = arguments,
            returnType = returnType,
            relaxer = relaxer
        )

        return method.build()
    }

    override fun buildMethodBundle(
        qualifier: String,
        classScopeGenerics: Map<String, List<TypeName>>?,
        ksFunction: KSFunctionDeclaration,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        inherited: Boolean,
        relaxer: Relaxer?
    ): Pair<PropertySpec, FunSpec> {
        val methodName = ksFunction.simpleName.asString()
        val receiverTypeResolver = ksFunction.toReceiverTypeParameterResolver(typeResolver)
        val receiverInfo = ksFunction.determineReceiver(
            receiverTypeResolver = receiverTypeResolver,
            typeResolver = typeResolver,
        )
        val generics = genericResolver.extractGenerics(ksFunction, receiverTypeResolver)
        val arguments = utils.determineArguments(
            inherited = inherited,
            arguments = ksFunction.parameters,
            typeParameterResolver = receiverTypeResolver
        )
        val argumentsWithReceiver = arguments.toMutableList().also { it.add(0, receiverInfo) }.toTypedArray()
        val parameter = utils.resolveTypeParameter(
            parameter = ksFunction.typeParameters,
            typeParameterResolver = receiverTypeResolver,
        )

        val proxyInfo = nameSelector.selectReceiverMethodName(
            qualifier = qualifier,
            methodName = methodName,
            arguments = argumentsWithReceiver,
            generics = generics ?: emptyMap(),
            typeResolver = receiverTypeResolver,
        )
        val returnType = ksFunction.returnType!!.resolve()
        val isSuspending = ksFunction.modifiers.contains(Modifier.SUSPEND)

        val proxySignature = utils.buildProxy(
            proxyInfo = proxyInfo,
            arguments = argumentsWithReceiver,
            classScopeGenerics = classScopeGenerics,
            generics = generics,
            suspending = isSuspending,
            returnType = returnType,
            typeResolver = receiverTypeResolver,
        )

        val method = buildMethod(
            proxyInfo = proxyInfo,
            receiverInfo = receiverInfo,
            generics = generics,
            isSuspending = isSuspending,
            enableSpy = enableSpy,
            typeParameter = parameter,
            arguments = arguments,
            returnType = proxySignature.returnType,
            typeResolver = receiverTypeResolver,
            relaxer = relaxer
        )

        return Pair(proxySignature.proxy, method)
    }

    private companion object {
        private val emptySetter = Pair(null, null)
    }
}
