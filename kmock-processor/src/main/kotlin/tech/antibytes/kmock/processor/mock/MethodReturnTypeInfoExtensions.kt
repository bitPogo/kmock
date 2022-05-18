/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.TypeName
import tech.antibytes.kmock.processor.ProcessorContract.MethodReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

internal fun MethodReturnTypeInfo.resolveClassScope(): List<TypeName>? {
    return this.classScope?.get(this.actualTypeName.toString().trimEnd('?'))
}

private fun MethodReturnTypeInfo.needsCastOnReturn(): Boolean = this.typeName != this.actualTypeName

private fun MethodReturnTypeInfo.needsCastForRelaxer(relaxer: Relaxer?): Boolean {
    return relaxer != null &&
        ((this.generic != null && this.generic.types.isNotEmpty()) || this.resolveClassScope() != null)
}

internal fun MethodReturnTypeInfo.needsCastForReceiverProperty(): Boolean {
    return (this.generic != null && (this.generic.types.size > 1 || this.generic.castReturnType))
}

internal fun MethodReturnTypeInfo.needsCastAnnotation(
    relaxer: Relaxer?,
): Boolean = this.needsCastOnReturn() || this.needsCastForRelaxer(relaxer)

private fun MethodReturnTypeInfo.resolveCast(condition: Boolean): String {
    return if (condition) {
        " as ${this.typeName}"
    } else {
        ""
    }
}

internal fun MethodReturnTypeInfo.resolveCastOnReturn(): String = resolveCast(this.needsCastOnReturn())

internal fun MethodReturnTypeInfo.resolveCastForRelaxer(
    relaxer: Relaxer?
): String = resolveCast(this.needsCastForRelaxer(relaxer))

internal fun MethodReturnTypeInfo.resolveCastForReceiverProperty(): String = resolveCast(needsCastForReceiverProperty())

internal fun MethodReturnTypeInfo.hasGenerics(): Boolean = this.generic != null || this.classScope != null
