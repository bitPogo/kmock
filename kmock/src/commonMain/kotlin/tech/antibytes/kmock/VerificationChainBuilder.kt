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

fun VerificationChainBuilder.withArguments(mockery: FunMockery<*, *>, vararg arguments: Any?) {
    this.add(mockery.withArguments(*arguments))
}

fun VerificationChainBuilder.withSameArguments(mockery: FunMockery<*, *>, vararg arguments: Any?) {
    this.add(mockery.withSameArguments(*arguments))
}

fun VerificationChainBuilder.withoutArguments(mockery: FunMockery<*, *>, vararg arguments: Any?) {
    this.add(mockery.withoutArguments(*arguments))
}

fun VerificationChainBuilder.wasGotten(mockery: KMockContract.PropMockery<*>) {
    this.add(mockery.wasGotten())
}

fun VerificationChainBuilder.wasSet(mockery: KMockContract.PropMockery<*>) {
    this.add(mockery.wasSet())
}

fun VerificationChainBuilder.wasSetTo(mockery: KMockContract.PropMockery<*>, value: Any?) {
    this.add(mockery.wasSetTo(value))
}
