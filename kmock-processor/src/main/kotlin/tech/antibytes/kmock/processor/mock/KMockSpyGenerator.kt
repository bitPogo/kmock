/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.TypeName
import tech.antibytes.kmock.processor.ProcessorContract.MethodReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.SpyGenerator

internal object KMockSpyGenerator : SpyGenerator {
    private fun buildSpy(
        invocation: String,
    ): String = "useSpyIf(__spyOn) { $invocation }\n"

    override fun buildGetterSpy(
        propertyName: String
    ): String = buildSpy("__spyOn!!.$propertyName")

    override fun buildSetterSpy(
        propertyName: String
    ): String = buildSpy("__spyOn!!.$propertyName = value")

    private fun determineSpyInvocationArgument(
        methodInfo: MethodTypeInfo
    ): String {
        return if (methodInfo.isVarArg) {
            "*${methodInfo.argumentName}"
        } else {
            methodInfo.argumentName
        }
    }

    private fun resolveTypes(parameter: List<TypeName>): String {
        return if (parameter.isEmpty()) {
            ""
        } else {
            "<${parameter.joinToString(", ")}>"
        }
    }

    override fun buildMethodSpy(
        methodName: String,
        parameter: List<TypeName>,
        arguments: Array<MethodTypeInfo>,
        methodReturnType: MethodReturnTypeInfo,
    ): String {
        val invocationArguments = arguments.joinToString(
            separator = ", ",
            transform = ::determineSpyInvocationArgument
        )
        val typesParameter = resolveTypes(parameter)

        return buildSpy("__spyOn!!.$methodName$typesParameter($invocationArguments)")
    }

    override fun buildEqualsSpy(mockName: String): String {
        return """
            |useSpyOnEqualsIf(
            |   spyTarget = __spyOn,
            |   other = other,
            |   spyOn = { super.equals(other) },
            |   mockKlass = $mockName::class
            |)
            |
        """.trimMargin()
    }
}
