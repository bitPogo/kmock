/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import tech.antibytes.kmock.processor.ProcessorContract.MethodReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.RelaxerGenerator

internal class KMockRelaxerGenerator : RelaxerGenerator {
    private fun addRelaxer(
        relaxer: Relaxer?
    ): String {
        return if (relaxer != null) {
            "useRelaxerIf(relaxed) { proxyId -> ${relaxer.functionName}(proxyId) }\n"
        } else {
            ""
        }
    }

    private fun addFunRelaxer(
        methodReturnType: MethodReturnTypeInfo,
        relaxer: Relaxer?
    ): String {
        return if (methodReturnType.typeName.toString() == "kotlin.Unit") {
            "useUnitFunRelaxerIf(relaxUnitFun || relaxed)\n"
        } else {
            addRelaxer(relaxer)
        }
    }

    override fun buildPropertyRelaxation(
        relaxer: Relaxer?
    ): String = addRelaxer(relaxer)

    override fun buildMethodRelaxation(
        relaxer: Relaxer?,
        methodReturnType: MethodReturnTypeInfo,
    ): String = addFunRelaxer(
        methodReturnType = methodReturnType,
        relaxer = relaxer
    )

    private fun buildRelaxationInvocation(
        methodName: String,
        argumentName: String,
    ): String = "{ super.$methodName($argumentName) }"

    override fun buildBuildInRelaxation(
        methodName: String,
        argument: MethodTypeInfo?,
    ): String {
        val argumentName = argument?.argumentName ?: ""
        val relaxerBody = buildRelaxationInvocation(
            methodName = methodName,
            argumentName = argumentName
        )

        return "useRelaxerIf(true) $relaxerBody\n"
    }
}
