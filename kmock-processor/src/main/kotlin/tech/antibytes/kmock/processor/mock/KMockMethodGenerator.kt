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
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNCHECKED
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MethodGeneratorHelper
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.NonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.ReturnTypeInfo
import tech.antibytes.kmock.processor.utils.toSecuredTypeName

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
        arguments: Array<MethodTypeInfo>,
        parameter: List<TypeName>,
        returnType: ReturnTypeInfo,
        relaxer: Relaxer?
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
            relaxer = relaxer
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
        arguments: Array<MethodTypeInfo>,
        parameter: List<TypeName>,
        returnType: ReturnTypeInfo,
        typeResolver: TypeParameterResolver,
        relaxer: Relaxer?
    ): FunSpec {
        val method = FunSpec
            .builder(proxyInfo.templateName)
            .addModifiers(KModifier.OVERRIDE)
            .addArguments(arguments)
            .returns(returnType.methodTypeName)

        if (isSuspending) {
            method.addModifiers(KModifier.SUSPEND)
        }

        if (generics != null) {
            method.typeVariables.addAll(
                this.genericResolver.mapDeclaredGenerics(generics, typeResolver)
            )
        }

        buildMethodBody(
            method = method,
            proxyInfo = proxyInfo,
            enableSpy = enableSpy,
            arguments = arguments,
            parameter = parameter,
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
    ): Triple<PropertySpec, FunSpec, TypeVariableName> {
        val methodName = ksFunction.simpleName.asString()

        val typeParameterResolver = ksFunction.typeParameters
            .toTypeParameterResolver(typeResolver)
        val generics = genericResolver.extractGenerics(ksFunction, typeParameterResolver)
        val proxyGenerics = utils.resolveProxyGenerics(
            generics = generics,
            typeResolver = typeParameterResolver,
        )
        val arguments = utils.determineArguments(
            inherited = inherited,
            arguments = ksFunction.parameters,
            generics = proxyGenerics,
            typeParameterResolver = typeParameterResolver
        )
        val parameter = utils.resolveTypeParameter(
            parameter = ksFunction.typeParameters,
            typeParameterResolver = typeParameterResolver,
        )

        val proxyInfo = nameSelector.selectMethodName(
            qualifier = qualifier,
            methodName = methodName,
            arguments = arguments,
            generics = generics ?: emptyMap(),
            typeResolver = typeParameterResolver,
        )

        val (methodReturnType, proxyReturnType) = ksFunction.returnType!!.toSecuredTypeName(
            inheritedVarargArg = false,
            generics = proxyGenerics ?: emptyMap(),
            typeParameterResolver = typeParameterResolver
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
            typeResolver = typeParameterResolver,
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
            relaxer = relaxer
        )

        return Triple(proxySignature.proxy, method, proxySignature.sideEffect)
    }
}
