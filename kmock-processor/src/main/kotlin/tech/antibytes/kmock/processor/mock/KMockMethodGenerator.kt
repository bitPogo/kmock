/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNCHECKED
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MemberArgumentTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MemberReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MethodGeneratorHelper
import tech.antibytes.kmock.processor.ProcessorContract.NonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.kotlinpoet.toProxyPairTypeName
import tech.antibytes.kmock.processor.kotlinpoet.toTypeParameterResolver

internal class KMockMethodGenerator(
    private val utils: MethodGeneratorHelper,
    private val nameSelector: ProxyNameSelector,
    private val nonIntrusiveInvocationGenerator: NonIntrusiveInvocationGenerator,
    private val genericResolver: GenericResolver,
) : MethodGenerator {
    private fun buildMethodBody(
        method: FunSpec.Builder,
        proxyInfo: ProxyInfo,
        enableSpy: Boolean,
        arguments: Array<MemberArgumentTypeInfo>,
        parameter: List<TypeName>,
        returnType: MemberReturnTypeInfo,
        relaxer: Relaxer?,
    ) {
        if (returnType.needsCastAnnotation(relaxer = relaxer)) {
            method.addAnnotation(UNCHECKED)
        }

        val cast = returnType.resolveCastOnReturn()

        val invocation = arguments.joinToString(", ") { argument -> argument.argumentName }
        val nonIntrusiveInvocation = nonIntrusiveInvocationGenerator.buildMethodNonIntrusiveInvocation(
            enableSpy = enableSpy,
            methodName = proxyInfo.templateName,
            typeParameter = parameter,
            arguments = arguments,
            methodReturnType = returnType,
            relaxer = relaxer,
        )

        method.addCode(
            "return %L.invoke(%L)%L%L",
            proxyInfo.proxyName,
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
        arguments: Array<MemberArgumentTypeInfo>,
        parameter: List<TypeName>,
        returnType: MemberReturnTypeInfo,
        typeResolver: TypeParameterResolver,
        relaxer: Relaxer?,
    ): FunSpec {
        val method = FunSpec
            .builder(proxyInfo.templateName)
            .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
            .addArguments(arguments)
            .returns(returnType.methodTypeName)

        if (isSuspending) {
            method.addModifiers(KModifier.SUSPEND)
        }

        if (generics != null) {
            method.typeVariables.addAll(
                this.genericResolver.mapDeclaredGenerics(generics, typeResolver),
            )
        }

        buildMethodBody(
            method = method,
            proxyInfo = proxyInfo,
            enableSpy = enableSpy,
            arguments = arguments,
            parameter = parameter,
            returnType = returnType,
            relaxer = relaxer,
        )

        return method.build()
    }

    override fun buildMethodBundle(
        qualifier: String,
        classScopeGenerics: Map<String, List<TypeName>>?,
        ksFunction: KSFunctionDeclaration,
        classWideResolver: TypeParameterResolver,
        enableSpy: Boolean,
        relaxer: Relaxer?,
    ): Triple<PropertySpec, FunSpec, LambdaTypeName> {
        val methodName = ksFunction.simpleName.asString()

        val typeParameterResolver = ksFunction.typeParameters.toTypeParameterResolver(classWideResolver)
        val generics = genericResolver.extractGenerics(ksFunction)
        val proxyGenerics = utils.resolveProxyGenerics(
            classScope = classScopeGenerics,
            generics = generics,
            methodWideResolver = typeParameterResolver,
        )
        val arguments = utils.determineArguments(
            arguments = ksFunction.parameters,
            generics = proxyGenerics,
            methodWideResolver = typeParameterResolver,
        )
        val parameter = utils.resolveTypeParameter(
            parameter = ksFunction.typeParameters,
            methodWideResolver = typeParameterResolver,
        )

        val proxyInfo = nameSelector.selectMethodName(
            qualifier = qualifier,
            methodName = methodName,
            arguments = arguments,
            generics = proxyGenerics ?: emptyMap(),
        )

        val (methodReturnType, proxyReturnType) = ksFunction.returnType!!.toProxyPairTypeName(
            generics = proxyGenerics ?: emptyMap(),
            typeParameterResolver = typeParameterResolver,
        )
        val isSuspending = ksFunction.modifiers.contains(Modifier.SUSPEND)

        val proxySignature = utils.buildProxy(
            proxyInfo = proxyInfo,
            arguments = arguments,
            classScopeGenerics = classScopeGenerics,
            generics = proxyGenerics,
            suspending = isSuspending,
            methodReturnType = methodReturnType,
            proxyReturnType = proxyReturnType,
            methodWideResolver = typeParameterResolver,
        )

        val method = buildMethod(
            proxyInfo = proxyInfo,
            generics = generics,
            isSuspending = isSuspending,
            enableSpy = enableSpy,
            parameter = parameter,
            arguments = arguments,
            returnType = proxySignature.returnType,
            typeResolver = typeParameterResolver,
            relaxer = relaxer,
        )

        return Triple(proxySignature.proxy, method, proxySignature.sideEffect)
    }
}
