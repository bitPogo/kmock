/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.mock

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.FunProxy
import tech.antibytes.kmock.KMockContract.PropertyProxy

internal class AssertionsStub(
    private val hasBeenCalledAtIndex: ((FunProxy<*, *>, Int) -> Unit)? = null,
    private val hasBeenCalledWithVoidAtIndex: ((FunProxy<*, *>, Int) -> Unit)? = null,
    private val hasBeenCalledWithAtIndex: ((FunProxy<*, *>, Int, Array<out Any?>) -> Unit)? = null,
    private val hasBeenStrictlyCalledWithAtIndex: ((FunProxy<*, *>, Int, Array<out Any?>) -> Unit)? = null,
    private val hasBeenCalledWithoutAtIndex: ((FunProxy<*, *>, Int, Array<out Any?>) -> Unit)? = null,
    private val wasGottenAtIndex: ((PropertyProxy<*>, Int) -> Unit)? = null,
    private val wasSetAtIndex: ((PropertyProxy<*>, Int) -> Unit)? = null,
    private val wasSetToAtIndex: ((PropertyProxy<*>, Int, Any?) -> Unit)? = null,
) : KMockContract.Assertions {
    override fun hasBeenCalledAtIndex(proxy: FunProxy<*, *>, callIndex: Int) {
        return hasBeenCalledAtIndex?.invoke(proxy, callIndex)
            ?: throw IllegalStateException("Missing SideEffect hasBeenCalledAtIndex.")
    }

    override fun hasBeenCalledWithVoidAtIndex(proxy: FunProxy<*, *>, callIndex: Int) {
        return hasBeenCalledWithVoidAtIndex?.invoke(proxy, callIndex)
            ?: throw IllegalStateException("Missing SideEffect hasBeenCalledWithVoidAtIndex.")
    }

    override fun hasBeenCalledWithAtIndex(proxy: FunProxy<*, *>, callIndex: Int, vararg arguments: Any?) {
        return hasBeenCalledWithAtIndex?.invoke(proxy, callIndex, arguments)
            ?: throw IllegalStateException("Missing SideEffect hasBeenCalledWithAtIndex.")
    }

    override fun hasBeenStrictlyCalledWithAtIndex(
        proxy: FunProxy<*, *>,
        callIndex: Int,
        vararg arguments: Any?,
    ) {
        return hasBeenStrictlyCalledWithAtIndex?.invoke(proxy, callIndex, arguments)
            ?: throw IllegalStateException("Missing SideEffect hasBeenStrictlyCalledWithAtIndex.")
    }

    override fun hasBeenCalledWithoutAtIndex(
        proxy: FunProxy<*, *>,
        callIndex: Int,
        vararg illegal: Any?,
    ) {
        return hasBeenCalledWithoutAtIndex?.invoke(proxy, callIndex, illegal)
            ?: throw IllegalStateException("Missing SideEffect hasBeenCalledWithoutAtIndex.")
    }

    override fun wasGottenAtIndex(proxy: PropertyProxy<*>, callIndex: Int) {
        return wasGottenAtIndex?.invoke(proxy, callIndex)
            ?: throw IllegalStateException("Missing SideEffect wasGottenAtIndex.")
    }

    override fun wasSetAtIndex(proxy: PropertyProxy<*>, callIndex: Int) {
        return wasSetAtIndex?.invoke(proxy, callIndex)
            ?: throw IllegalStateException("Missing SideEffect wasSetAtIndex.")
    }

    override fun wasSetToAtIndex(proxy: PropertyProxy<*>, callIndex: Int, value: Any?) {
        return wasSetToAtIndex?.invoke(proxy, callIndex, value)
            ?: throw IllegalStateException("Missing SideEffect wasSetToAtIndex.")
    }
}
