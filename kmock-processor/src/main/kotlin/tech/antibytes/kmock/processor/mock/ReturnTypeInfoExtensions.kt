/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.TypeName
import tech.antibytes.kmock.processor.ProcessorContract.ReturnTypeInfo
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer

internal fun ReturnTypeInfo.resolveClassScope(): List<TypeName>? {
    return this.classScope?.get(this.proxyTypeName.toString().trimEnd('?'))
}

private fun ReturnTypeInfo.needsCastOnReturn(): Boolean = this.methodTypeName != this.proxyTypeName

private fun ReturnTypeInfo.needsCastForRelaxer(relaxer: Relaxer?): Boolean {
    return relaxer != null &&
        ((this.generic != null && this.generic.types.isNotEmpty()) || this.resolveClassScope() != null)
}

internal fun ReturnTypeInfo.needsCastForReceiverProperty(): Boolean {
    return (this.generic != null && (this.generic.types.size > 1 || this.generic.castReturnType))
}

internal fun ReturnTypeInfo.needsCastAnnotation(
    relaxer: Relaxer?,
): Boolean = this.needsCastOnReturn() || this.needsCastForRelaxer(relaxer)

private fun ReturnTypeInfo.resolveCast(condition: Boolean): String {
    return if (condition) {
        " as ${this.methodTypeName}"
    } else {
        ""
    }
}

internal fun ReturnTypeInfo.resolveCastOnReturn(): String = resolveCast(this.needsCastOnReturn())

internal fun ReturnTypeInfo.resolveCastForRelaxer(
    relaxer: Relaxer?
): String = resolveCast(this.needsCastForRelaxer(relaxer))

internal fun ReturnTypeInfo.resolveCastForReceiverProperty(): String = resolveCast(needsCastForReceiverProperty())

internal fun ReturnTypeInfo.hasGenerics(): Boolean = this.generic != null || this.classScope != null
