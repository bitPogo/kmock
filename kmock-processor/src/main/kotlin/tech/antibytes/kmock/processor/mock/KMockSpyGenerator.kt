/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.SpyGenerator

internal object KMockSpyGenerator : SpyGenerator {
    private fun buildSpy(
        invocation: String,
    ): String {
        return """ {
            |   useSpyIf(__spyOn) { $invocation }
            |}
        """.trimMargin()
    }

    override fun buildGetterSpy(propertyName: String): String {
        return buildSpy("__spyOn!!.$propertyName")
    }

    override fun buildSetterSpy(propertyName: String): String {
        return buildSpy("__spyOn!!.$propertyName = value")
    }

    private fun determineSpyInvocationArgument(
        methodInfo: MethodTypeInfo
    ): String {
        return if (methodInfo.isVarArg) {
            "*${methodInfo.argumentName}"
        } else {
            methodInfo.argumentName
        }
    }

    override fun buildMethodSpy(
        methodName: String,
        arguments: Array<MethodTypeInfo>
    ): String {
        val invocationArguments = arguments.joinToString(
            separator = ", ",
            transform = ::determineSpyInvocationArgument
        )

        return buildSpy("__spyOn!!.$methodName($invocationArguments)")
    }

    override fun buildEqualsSpy(mockName: String): String {
        return """ {
            |   useSpyOnEqualsIf(
            |       spyTarget = __spyOn,
            |       other = other,
            |       spyOn = { super.equals(other) },
            |       mockKlass = $mockName::class
            |   )
            |}
        """.trimMargin()
    }
}
