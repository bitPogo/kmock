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
import tech.antibytes.kmock.processor.titleCase

internal class KMockRelaxerGenerator : RelaxerGenerator {
    private fun addRelaxer(
        relaxationDefinitions: StringBuilder,
        relaxer: Relaxer?
    ) {
        if (relaxer != null) {
            relaxationDefinitions.append("useRelaxerIf(relaxed) { proxyId -> ${relaxer.functionName}(proxyId) }\n")
        }
    }

    private fun addFunRelaxer(
        relaxationDefinitions: StringBuilder,
        methodReturnType: MethodReturnTypeInfo,
        relaxer: Relaxer?
    ) {
        if (methodReturnType.typeName.toString() == "kotlin.Unit") {
            relaxationDefinitions.append("useUnitFunRelaxerIf(relaxUnitFun || relaxed)\n")
        } else {
            addRelaxer(relaxationDefinitions, relaxer)
        }
    }

    private fun addRelaxation(
        addRelaxer: Function1<StringBuilder, Unit>,
    ): String {
        val relaxationDefinitions = StringBuilder(3)

        relaxationDefinitions.append("{\n")

        addRelaxer(relaxationDefinitions)

        relaxationDefinitions.append("}")

        return if (relaxationDefinitions.length == 3) {
            ""
        } else {
            relaxationDefinitions.toString()
        }
    }

    override fun addPropertyRelaxation(
        relaxer: Relaxer?
    ): String = addRelaxation { relaxationDefinitions ->
        addRelaxer(
            relaxationDefinitions = relaxationDefinitions,
            relaxer = relaxer
        )
    }

    override fun addMethodRelaxation(
        relaxer: Relaxer?,
        methodReturnType: MethodReturnTypeInfo,
    ): String = addRelaxation { relaxationDefinitions ->
        addFunRelaxer(
            relaxationDefinitions = relaxationDefinitions,
            methodReturnType = methodReturnType,
            relaxer = relaxer
        )
    }

    private fun buildRelaxationInvocation(
        methodName: String,
        argumentName: String,
    ): String {
        val body = "super.$methodName($argumentName)"

        return if (argumentName.isEmpty()) {
            "{ $body }"
        } else {
            "{ $argumentName ->\n$body\n}"
        }
    }

    override fun addBuildInRelaxation(
        methodName: String,
        argument: MethodTypeInfo?,
    ): String = addRelaxation { relaxationDefinitions ->
        val argumentName = argument?.argumentName ?: ""
        val relaxerBody = buildRelaxationInvocation(
            methodName = methodName,
            argumentName = argumentName
        )

        relaxationDefinitions.append("use${methodName.titleCase()}Relaxer $relaxerBody\n")
    }
}
