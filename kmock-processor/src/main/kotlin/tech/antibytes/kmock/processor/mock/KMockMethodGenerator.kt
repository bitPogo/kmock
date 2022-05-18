/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import tech.antibytes.kmock.processor.ProcessorContract.MethodeGeneratorHelper
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.MethodGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MethodReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.NonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.ProcessorContract.ProxyInfo
import tech.antibytes.kmock.processor.ProcessorContract.ProxyNameSelector
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

internal class KMockMethodGenerator(
    private val utils: MethodeGeneratorHelper,
    private val nameSelector: ProxyNameSelector,
    private val nonIntrusiveInvocationGenerator: NonIntrusiveInvocationGenerator,
    private val genericResolver: GenericResolver,
) : MethodGenerator {
    private fun FunSpec.Builder.addArguments(
        arguments: Array<MethodTypeInfo>
    ): FunSpec.Builder {
        arguments.forEach { argument ->
            val vararged = if (argument.isVarArg) {
                varargs
            } else {
                noVarargs
            }

            this.addParameter(
                argument.argumentName,
                argument.typeName,
                *vararged
            )
        }

        return this
    }

    private fun buildMethodBody(
        method: FunSpec.Builder,
        proxyInfo: ProxyInfo,
        enableSpy: Boolean,
        arguments: Array<MethodTypeInfo>,
        parameter: List<TypeName>,
        proxyReturnType: MethodReturnTypeInfo,
        relaxer: Relaxer?
    ) {
        if (proxyReturnType.needsCastAnnotation(relaxer = relaxer)) {
            method.addAnnotation(unchecked)
        }

        val cast = proxyReturnType.resolveCastOnReturn()

        val invocation = arguments.joinToString(", ") { argument -> argument.argumentName }
        val nonIntrusiveInvocation = nonIntrusiveInvocationGenerator.buildMethodNonIntrusiveInvocation(
            enableSpy = enableSpy,
            methodName = proxyInfo.templateName,
            parameter = parameter,
            arguments = arguments,
            methodReturnType = proxyReturnType,
            relaxer = relaxer
        )

        method.addCode(
            "return %L.invoke(%L)%L%L",
            proxyInfo.proxyName,
            invocation,
            nonIntrusiveInvocation,
            cast
        )
    }

    private fun buildShallowMethodBody(method: FunSpec.Builder) {
        method.addCode(scopedBody)
    }

    private fun buildMethod(
        methodScope: TypeName?,
        returnType: TypeName,
        proxyInfo: ProxyInfo,
        generics: Map<String, List<KSTypeReference>>?,
        isSuspending: Boolean,
        enableSpy: Boolean,
        arguments: Array<MethodTypeInfo>,
        parameter: List<TypeName>,
        proxyReturnType: MethodReturnTypeInfo?,
        typeResolver: TypeParameterResolver,
        relaxer: Relaxer?
    ): FunSpec {
        val method = FunSpec
            .builder(proxyInfo.templateName)
            .addModifiers(KModifier.OVERRIDE)
            .addArguments(arguments)
            .returns(returnType)

        if (generics != null) {
            method.typeVariables.addAll(
                this.genericResolver.mapDeclaredGenerics(generics, typeResolver)
            )
        }

        if (isSuspending) {
            method.addModifiers(KModifier.SUSPEND)
        }

        if (methodScope != null) {
            method.receiver(methodScope)
            buildShallowMethodBody(method)
        } else {
            buildMethodBody(
                method = method,
                proxyInfo = proxyInfo,
                enableSpy = enableSpy,
                arguments = arguments,
                parameter = parameter,
                proxyReturnType = proxyReturnType!!,
                relaxer = relaxer
            )
        }

        return method.build()
    }

    override fun buildMethodBundle(
        methodScope: TypeName?,
        qualifier: String,
        classScopeGenerics: Map<String, List<TypeName>>?,
        ksFunction: KSFunctionDeclaration,
        typeResolver: TypeParameterResolver,
        enableSpy: Boolean,
        inherited: Boolean,
        relaxer: Relaxer?
    ): Pair<PropertySpec?, FunSpec> {
        val methodName = ksFunction.simpleName.asString()
        val typeParameterResolver = ksFunction.typeParameters
            .toTypeParameterResolver(typeResolver)
        val generics = genericResolver.extractGenerics(ksFunction, typeParameterResolver)
        val arguments = utils.determineArguments(
            inherited = inherited,
            arguments = ksFunction.parameters,
            typeParameterResolver = typeParameterResolver
        )
        val parameter = utils.determineTypeParameter(
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
        val returnType = ksFunction.returnType!!.resolve()
        val isSuspending = ksFunction.modifiers.contains(Modifier.SUSPEND)

        val proxySignature = utils.buildProxy(
            proxyInfo = proxyInfo,
            arguments = arguments,
            classScopeGenerics = classScopeGenerics,
            generics = generics,
            suspending = isSuspending,
            returnType = returnType,
            typeResolver = typeParameterResolver,
        )

        val method = buildMethod(
            methodScope = methodScope,
            returnType = returnType.toTypeName(typeParameterResolver),
            proxyInfo = proxyInfo,
            generics = generics,
            isSuspending = isSuspending,
            enableSpy = enableSpy,
            parameter = parameter,
            arguments = arguments,
            proxyReturnType = proxySignature.returnType,
            typeResolver = typeParameterResolver,
            relaxer = relaxer
        )

        return Pair(proxySignature.proxy, method)
    }

    private companion object {
        private val unchecked = AnnotationSpec.builder(Suppress::class).addMember("%S", "UNCHECKED_CAST").build()
        private val varargs = arrayOf(KModifier.VARARG)
        private val noVarargs = arrayOf<KModifier>()
        private val scopedBody = "throw IllegalStateException(\n\"This action is not callable.\"\n)"
    }
}
