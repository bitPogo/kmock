/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.TypeName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_CONTEXT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_PROPERTY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNIT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.VALUE
import tech.antibytes.kmock.processor.ProcessorContract.MemberArgumentTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MemberReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.SpyGenerator

internal object KMockSpyGenerator : SpyGenerator {
    private fun buildSpy(invocation: String): String = "useSpyIf($SPY_PROPERTY) { $invocation }\n"

    override fun buildGetterSpy(
        propertyName: String,
    ): String = buildSpy("$SPY_PROPERTY!!.$propertyName")

    override fun buildSetterSpy(
        propertyName: String,
    ): String = buildSpy("$SPY_PROPERTY!!.$propertyName = $VALUE")

    override fun buildReceiverGetterSpy(propertyName: String, propertyType: MemberReturnTypeInfo): String {
        val invocation = """
            |   $SPY_CONTEXT {
            |       this@$propertyName.$propertyName
            |   } as ${propertyType.methodTypeName}
        """.trimMargin()
        return buildSpy(invocation)
    }

    override fun buildReceiverSetterSpy(propertyName: String): String {
        val invocation = """
            |   $SPY_CONTEXT {
            |       this@$propertyName.$propertyName = $VALUE
            |       ${UNIT.simpleName}
            |   }
        """.trimMargin()
        return buildSpy(invocation)
    }

    private fun determineSpyInvocationArgument(
        methodInfo: MemberArgumentTypeInfo,
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
        arguments: Array<MemberArgumentTypeInfo>,
        methodReturnType: MemberReturnTypeInfo,
    ): String {
        val invocationArguments = arguments.joinToString(
            separator = ", ",
            transform = ::determineSpyInvocationArgument,
        )
        val typesParameter = resolveTypes(parameter)

        return buildSpy("$SPY_PROPERTY!!.$methodName$typesParameter($invocationArguments)")
    }

    override fun buildReceiverMethodSpy(
        methodName: String,
        parameter: List<TypeName>,
        arguments: Array<MemberArgumentTypeInfo>,
        methodReturnType: MemberReturnTypeInfo,
    ): String {
        val invocationArguments = arguments.joinToString(
            separator = ", ",
            transform = ::determineSpyInvocationArgument,
        )
        val typesParameter = resolveTypes(parameter)
        val invocation = """
            |   $SPY_CONTEXT {
            |       this@$methodName.$methodName$typesParameter($invocationArguments)
            |   } as ${methodReturnType.methodTypeName}
        """.trimMargin()

        return buildSpy(invocation)
    }

    override fun buildEqualsSpy(mockName: String): String {
        return """
            |useSpyOnEqualsIf(
            |   spyTarget = $SPY_PROPERTY,
            |   other = other,
            |   spyOn = { super.equals(other) },
            |   mockKlass = $mockName::class
            |)
            |
        """.trimMargin()
    }
}
