/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import tech.antibytes.kmock.processor.ProcessorContract.MethodTypeInfo

private val varargs = arrayOf(KModifier.VARARG)
private val noVarargs = arrayOf<KModifier>()

internal fun FunSpec.Builder.addArguments(
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
            argument.methodTypeName,
            *vararged
        )
    }

    return this
}
