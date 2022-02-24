/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.FunMockery
import tech.antibytes.kmock.KMockContract.VerificationHandle

class VerificationChainBuilder : KMockContract.VerificationHandleContainer {
    private val handles = mutableListOf<VerificationHandle>()

    override fun add(handle: VerificationHandle) {
        handles.add(handle)
    }

    override fun toList(): List<VerificationHandle> = handles.toList()
}

fun VerificationChainBuilder.hadBeenCalledWith(mockery: FunMockery<*, *>, vararg arguments: Any?) {
    this.add(mockery.hadBeenCalledWith(*arguments))
}

fun VerificationChainBuilder.hadBeenStrictlyCalledWith(mockery: FunMockery<*, *>, vararg arguments: Any?) {
    this.add(mockery.hadBeenStrictlyCalledWith(*arguments))
}

fun VerificationChainBuilder.hadBeenCalledWithout(mockery: FunMockery<*, *>, vararg arguments: Any?) {
    this.add(mockery.hadBeenCalledWithout(*arguments))
}

fun VerificationChainBuilder.wasGotten(mockery: KMockContract.PropertyMockery<*>) {
    this.add(mockery.wasGotten())
}

fun VerificationChainBuilder.wasSet(mockery: KMockContract.PropertyMockery<*>) {
    this.add(mockery.wasSet())
}

fun VerificationChainBuilder.wasSetTo(mockery: KMockContract.PropertyMockery<*>, value: Any?) {
    this.add(mockery.wasSetTo(value))
}
