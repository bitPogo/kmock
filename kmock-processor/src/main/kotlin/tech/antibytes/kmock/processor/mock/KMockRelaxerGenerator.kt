/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.TypeName
import tech.antibytes.kmock.processor.ProcessorContract.MethodReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.RelaxerGenerator

internal class KMockRelaxerGenerator : RelaxerGenerator {
    private fun TypeName.toParameterlessString(): String {
        return this.toString().trimEnd('?').substringBefore('<')
    }

    private fun MethodReturnTypeInfo.toParameterlessString(): String {
        val generics = StringBuilder()
        val classScope = this.resolveClassScope()
        var idx = 0

        if (classScope != null) {
            classScope.forEach { type ->
                generics.append("\ntype$idx = ${type.toParameterlessString()}::class,")
                idx += 1
            }
        } else {
            this.generic?.types?.forEach { type ->
                generics.append("\ntype$idx = ${type.toParameterlessString()}::class,")
                idx += 1
            }
        }

        return generics.toString()
    }

    private fun resolveTypeParameter(
        methodReturnType: MethodReturnTypeInfo,
    ): String {
        return if (!methodReturnType.hasGenerics()) {
            ""
        } else {
            methodReturnType.toParameterlessString()
        }
    }

    private fun addRelaxer(
        methodReturnType: MethodReturnTypeInfo,
        relaxer: Relaxer?
    ): String {
        val types = resolveTypeParameter(methodReturnType)
        val cast = methodReturnType.resolveCastForRelaxer(relaxer)

        return if (relaxer != null) {
            "useRelaxerIf(relaxed) { proxyId -> ${relaxer.functionName}(proxyId,$types)$cast }\n"
        } else {
            ""
        }
    }

    private fun addFunRelaxer(
        methodReturnType: MethodReturnTypeInfo,
        relaxer: Relaxer?
    ): String {
        return if (methodReturnType.actualTypeName.toString() == "kotlin.Unit") {
            "useUnitFunRelaxerIf(relaxUnitFun || relaxed)\n"
        } else {
            addRelaxer(methodReturnType, relaxer)
        }
    }

    override fun buildPropertyRelaxation(
        propertyType: MethodReturnTypeInfo,
        relaxer: Relaxer?,
    ): String = addRelaxer(propertyType, relaxer)

    override fun buildMethodRelaxation(
        methodReturnType: MethodReturnTypeInfo,
        relaxer: Relaxer?,
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
