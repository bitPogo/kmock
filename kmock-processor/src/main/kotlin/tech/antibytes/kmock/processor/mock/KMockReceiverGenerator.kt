/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_CONTEXT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_PROPERTY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNCHECKED
import tech.antibytes.kmock.processor.ProcessorContract.Companion.VALUE
import tech.antibytes.kmock.processor.ProcessorContract.Companion.nullableAny
import tech.antibytes.kmock.processor.ProcessorContract.Companion.unit
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MemberArgumentTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MemberReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodGeneratorHelper
import tech.antibytes.kmock.processor.ProcessorContract.NonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.ReceiverGenerator
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.utils.resolveReceiver
import tech.antibytes.kmock.processor.utils.toReceiverTypeParameterResolver
import tech.antibytes.kmock.processor.utils.toSecuredTypeName

internal class KMockReceiverGenerator(
    private val utils: MethodGeneratorHelper,
    private val nameSelector: ProxyNameSelector,
    private val nonIntrusiveInvocationGenerator: NonIntrusiveInvocationGenerator,
    private val genericResolver: GenericResolver,
) : ReceiverGenerator {
    private fun KSPropertyDeclaration.determineReceiver(
        receiverTypeResolver: TypeParameterResolver,
        proxyGenericTypes: Map<String, GenericDeclaration>?,
        typeResolver: TypeParameterResolver,
    ): MemberArgumentTypeInfo {
        val receiver = this.resolveReceiver(
            typeResolver = typeResolver,
            receiverTypeResolver = receiverTypeResolver,
        )
        val proxyReceiver = proxyGenericTypes?.get(receiver.toString().trimEnd('?'))?.resolveGeneric()
            ?: receiver

        return MemberArgumentTypeInfo(
            argumentName = "receiver",
            methodTypeName = receiver,
            proxyTypeName = proxyReceiver,
            isVarArg = false,
        )
    }

    private fun KSFunctionDeclaration.determineReceiver(
        receiverTypeResolver: TypeParameterResolver,
        proxyGenericTypes: Map<String, GenericDeclaration>?,
        typeResolver: TypeParameterResolver,
    ): MemberArgumentTypeInfo {
        val receiver = this.resolveReceiver(
            typeResolver = typeResolver,
            receiverTypeResolver = receiverTypeResolver,
        )

        val proxyReceiver = proxyGenericTypes?.get(receiver.toString().trimEnd('?'))?.resolveGeneric()
            ?: receiver

        return MemberArgumentTypeInfo(
            argumentName = "receiver",
            methodTypeName = receiver,
            proxyTypeName = proxyReceiver,
            isVarArg = false,
        )
    }

    private fun PropertySpec.Builder.addSetter(
        enableSpy: Boolean,
        setterProxy: ProxyInfo?,
        propertyType: MemberReturnTypeInfo,
    ) {
        if (setterProxy != null) {
            val nonIntrusiveInvocation = nonIntrusiveInvocationGenerator.buildReceiverSetterNonIntrusiveInvocation(
                enableSpy = enableSpy,
                propertyName = setterProxy.templateName,
            )

            this.mutable(true)
            this.setter(
                FunSpec.setterBuilder()
                    .addParameter(VALUE, propertyType.methodTypeName)
                    .addStatement(
                        "%L.invoke(this@%L, $VALUE)%L",
                        setterProxy.proxyName,
                        setterProxy.templateName,
                        nonIntrusiveInvocation,
                    )
                    .build()
            )
        }
    }

    private fun buildPropertyReceiver(
        getterProxy: ProxyInfo,
        setterProxy: ProxyInfo?,
        enableSpy: Boolean,
        receiver: MemberArgumentTypeInfo,
        propertyType: MemberReturnTypeInfo,
        typeParameter: List<TypeVariableName>,
        relaxer: Relaxer?,
    ): PropertySpec {
        val property = PropertySpec.builder(
            getterProxy.templateName,
            propertyType.methodTypeName,
            KModifier.OVERRIDE
        ).receiver(receiver.methodTypeName)

        if (typeParameter.isNotEmpty()) {
            property.addTypeVariables(typeParameter)
        }

        if (propertyType.needsCastForReceiverProperty() || enableSpy) {
            property.addAnnotation(UNCHECKED)
        }

        val cast = propertyType.resolveCastForReceiverProperty()

        val nonIntrusiveInvocation = nonIntrusiveInvocationGenerator.buildReceiverGetterNonIntrusiveInvocation(
            enableSpy = enableSpy,
            propertyName = getterProxy.templateName,
            propertyType = propertyType,
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

        property.addSetter(
            enableSpy = enableSpy,
            setterProxy = setterProxy,
            propertyType = propertyType,
        )

        return property.build()
    }

    private fun resolveSetterProxy(
        qualifier: String,
        propertyName: String,
        isMutable: Boolean,
        receiverInfo: MemberArgumentTypeInfo,
        generics: Map<String, List<KSTypeReference>>,
        proxyGenerics: Map<String, GenericDeclaration>?,
        classScopeGenerics: Map<String, List<TypeName>>?,
        receiverTypeResolver: TypeParameterResolver,
    ): Pair<ProxyInfo?, PropertySpec?> {
        return if (isMutable) {
            val setterProxyInfo = nameSelector.selectReceiverSetterName(
                qualifier = qualifier,
                propertyName = propertyName,
                receiver = receiverInfo,
                generics = generics,
                typeResolver = receiverTypeResolver,
            )

            val (setterProxy, _) = utils.buildProxy(
                proxyInfo = setterProxyInfo,
                arguments = arrayOf(receiverInfo),
                suspending = false,
                classScopeGenerics = classScopeGenerics,
                generics = proxyGenerics,
                methodReturnType = unit,
                proxyReturnType = unit,
                typeResolver = receiverTypeResolver,
            )

            return Pair(setterProxyInfo, setterProxy)
        } else {
            emptySetter
        }
    }

    private fun MemberArgumentTypeInfo.resolveProxyType(propertyType: TypeName): TypeName {
        return if (propertyType == methodTypeName) {
            proxyTypeName
        } else {
            propertyType
        }
    }

    override fun buildPropertyBundle(
        spyType: TypeName,
        qualifier: String,
        classScopeGenerics: Map<String, List<TypeName>>?,
        ksProperty: KSPropertyDeclaration,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        relaxer: Relaxer?
    ): Triple<PropertySpec, PropertySpec?, PropertySpec> {
        val propertyName = ksProperty.simpleName.asString()
        val receiverTypeResolver = ksProperty.toReceiverTypeParameterResolver(typeResolver)
        val generics = genericResolver.extractGenerics(ksProperty, receiverTypeResolver) ?: emptyMap()
        val proxyGenerics = utils.resolveProxyGenerics(
            generics = generics,
            typeResolver = receiverTypeResolver,
        )

        val receiverInfo = ksProperty.determineReceiver(
            receiverTypeResolver = receiverTypeResolver,
            proxyGenericTypes = proxyGenerics,
            typeResolver = typeResolver,
        )

        val propertyType = ksProperty.type.resolve().toTypeName(receiverTypeResolver)

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
            generics = proxyGenerics,
            methodReturnType = propertyType,
            proxyReturnType = receiverInfo.resolveProxyType(propertyType),
            typeResolver = receiverTypeResolver,
        )

        val (setterProxyInfo, setter) = resolveSetterProxy(
            qualifier = qualifier,
            propertyName = propertyName,
            isMutable = isMutable,
            receiverInfo = receiverInfo,
            generics = generics,
            proxyGenerics = proxyGenerics,
            classScopeGenerics = classScopeGenerics,
            receiverTypeResolver = receiverTypeResolver,
        )

        val property = buildPropertyReceiver(
            getterProxy = getterProxyInfo,
            setterProxy = setterProxyInfo,
            enableSpy = enableSpy,
            receiver = receiverInfo,
            propertyType = getterReturnType,
            typeParameter = genericResolver.mapDeclaredGenerics(generics, receiverTypeResolver),
            relaxer = relaxer
        )

        return Triple(getter, setter, property)
    }

    private fun buildMethodBody(
        method: FunSpec.Builder,
        proxyInfo: ProxyInfo,
        enableSpy: Boolean,
        typeParameter: List<TypeName>,
        arguments: Array<MemberArgumentTypeInfo>,
        returnType: MemberReturnTypeInfo,
        relaxer: Relaxer?
    ) {
        if (returnType.needsCastAnnotation(relaxer = relaxer) || enableSpy) {
            method.addAnnotation(UNCHECKED)
        }

        val cast = returnType.resolveCastOnReturn()

        val invocation = arguments.joinToString(", ") { argument -> argument.argumentName }
        val nonIntrusiveInvocation = nonIntrusiveInvocationGenerator.buildReceiverMethodNonIntrusiveInvocation(
            enableSpy = enableSpy,
            methodName = proxyInfo.templateName,
            typeParameter = typeParameter,
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
        receiverInfo: MemberArgumentTypeInfo,
        arguments: Array<MemberArgumentTypeInfo>,
        typeParameter: List<TypeName>,
        returnType: MemberReturnTypeInfo,
        typeResolver: TypeParameterResolver,
        relaxer: Relaxer?
    ): FunSpec {
        val method = FunSpec
            .builder(proxyInfo.templateName)
            .addModifiers(KModifier.OVERRIDE)
            .addArguments(arguments)
            .returns(returnType.methodTypeName)
            .receiver(receiverInfo.methodTypeName)

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
        spyType: TypeName,
        qualifier: String,
        classScopeGenerics: Map<String, List<TypeName>>?,
        ksFunction: KSFunctionDeclaration,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        inherited: Boolean,
        relaxer: Relaxer?
    ): Triple<PropertySpec, FunSpec, TypeVariableName> {
        val methodName = ksFunction.simpleName.asString()
        val receiverTypeResolver = ksFunction.toReceiverTypeParameterResolver(typeResolver)
        val generics = genericResolver.extractGenerics(ksFunction, receiverTypeResolver) ?: emptyMap()
        val proxyGenerics = utils.resolveProxyGenerics(
            generics = generics,
            typeResolver = receiverTypeResolver,
        )

        val receiverInfo = ksFunction.determineReceiver(
            receiverTypeResolver = receiverTypeResolver,
            proxyGenericTypes = proxyGenerics,
            typeResolver = typeResolver,
        )
        val arguments = utils.determineArguments(
            inherited = inherited,
            arguments = ksFunction.parameters,
            generics = proxyGenerics,
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
            generics = generics,
            typeResolver = receiverTypeResolver,
        )
        val (methodReturnType, proxyReturnType) = ksFunction.returnType!!.toSecuredTypeName(
            inheritedVarargArg = false,
            generics = proxyGenerics ?: emptyMap(),
            typeParameterResolver = receiverTypeResolver
        )
        val isSuspending = ksFunction.modifiers.contains(Modifier.SUSPEND)

        val proxySignature = utils.buildProxy(
            proxyInfo = proxyInfo,
            arguments = argumentsWithReceiver,
            classScopeGenerics = classScopeGenerics,
            generics = proxyGenerics,
            suspending = isSuspending,
            methodReturnType = methodReturnType,
            proxyReturnType = proxyReturnType,
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

        return Triple(proxySignature.proxy, method, proxySignature.sideEffect)
    }

    override fun buildReceiverSpyContext(spyType: TypeName, typeResolver: TypeParameterResolver): FunSpec {
        return FunSpec.builder(SPY_CONTEXT)
            .addParameter(
                "action",
                TypeVariableName("$spyType.() -> $nullableAny").copy(nullable = false),
            )
            .addCode("return action($SPY_PROPERTY!!)")
            .build()
    }

    private companion object {
        private val emptySetter = Pair(null, null)
    }
}
