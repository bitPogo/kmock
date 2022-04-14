/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import tech.antibytes.kmock.processor.ProcessorContract.MethodReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.NonIntrusiveInvocationGenerator
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.RelaxerGenerator
import tech.antibytes.kmock.processor.ProcessorContract.SpyGenerator

internal class KMockNonIntrusiveInvocationGenerator(
    private val relaxerGenerator: RelaxerGenerator,
    private val spyGenerator: SpyGenerator
) : NonIntrusiveInvocationGenerator {
    private val noArguments: Array<MethodTypeInfo> = arrayOf()

    private fun buildInvocation(hook: (StringBuilder) -> Unit): String {
        val nonIntrusiveInvocation = StringBuilder(4)

        nonIntrusiveInvocation.append(" {\n")

        hook(nonIntrusiveInvocation)

        nonIntrusiveInvocation.append("}")

        return if (nonIntrusiveInvocation.length == 4) {
            ""
        } else {
            nonIntrusiveInvocation.toString()
        }
    }

    override fun buildGetterNonIntrusiveInvocation(
        enableSpy: Boolean,
        propertyName: String,
        relaxer: Relaxer?
    ): String = buildInvocation { nonIntrusiveInvocation ->
        nonIntrusiveInvocation.append(
            relaxerGenerator.buildPropertyRelaxation(relaxer)
        )
        if (enableSpy) {
            nonIntrusiveInvocation.append(
                spyGenerator.buildGetterSpy(propertyName)
            )
        }
    }

    override fun buildSetterNonIntrusiveInvocation(
        enableSpy: Boolean,
        propertyName: String
    ): String = buildInvocation { nonIntrusiveInvocation ->
        if (enableSpy) {
            nonIntrusiveInvocation.append(
                spyGenerator.buildSetterSpy(propertyName)
            )
        }
    }

    override fun buildMethodNonIntrusiveInvocation(
        enableSpy: Boolean,
        methodName: String,
        arguments: Array<MethodTypeInfo>,
        methodReturnType: MethodReturnTypeInfo,
        relaxer: Relaxer?
    ): String = buildInvocation { nonIntrusiveInvocation ->
        nonIntrusiveInvocation.append(
            relaxerGenerator.buildMethodRelaxation(
                methodReturnType = methodReturnType,
                relaxer = relaxer
            )
        )

        if (enableSpy) {
            nonIntrusiveInvocation.append(
                spyGenerator.buildMethodSpy(
                    methodName = methodName,
                    arguments = arguments
                )
            )
        }
    }

    private fun buildEqualsSpy(
        mockName: String
    ): String = spyGenerator.buildEqualsSpy(mockName)

    private fun buildBuildInSpy(
        methodName: String
    ): String = spyGenerator.buildMethodSpy(methodName, noArguments)

    private fun buildEqualsRelaxer(
        argument: MethodTypeInfo?
    ): String = relaxerGenerator.buildBuildInRelaxation("equals", argument)

    private fun buildBuildInRelaxer(
        methodName: String
    ): String = relaxerGenerator.buildBuildInRelaxation(methodName, null)

    private fun buildNonIntrusiveEquals(
        nonIntrusiveInvocation: StringBuilder,
        enableSpy: Boolean,
        mockName: String,
        argument: MethodTypeInfo?
    ) {
        nonIntrusiveInvocation.append(buildEqualsRelaxer(argument))

        if (enableSpy) {
            nonIntrusiveInvocation.append(
                buildEqualsSpy(mockName)
            )
        }
    }

    private fun buildNonIntrusiveBuildInMethod(
        nonIntrusiveInvocation: StringBuilder,
        enableSpy: Boolean,
        methodName: String,
    ) {
        nonIntrusiveInvocation.append(buildBuildInRelaxer(methodName))

        if (enableSpy) {
            nonIntrusiveInvocation.append(
                buildBuildInSpy(methodName)
            )
        }
    }

    override fun buildBuildInNonIntrusiveInvocation(
        enableSpy: Boolean,
        mockName: String,
        methodName: String,
        argument: MethodTypeInfo?
    ): String = buildInvocation { nonIntrusiveInvocation ->
        if (methodName == "equals") {
            buildNonIntrusiveEquals(
                nonIntrusiveInvocation = nonIntrusiveInvocation,
                enableSpy = enableSpy,
                mockName = mockName,
                argument = argument
            )
        } else {
            buildNonIntrusiveBuildInMethod(
                nonIntrusiveInvocation = nonIntrusiveInvocation,
                enableSpy = enableSpy,
                methodName = methodName
            )
        }
    }
}
