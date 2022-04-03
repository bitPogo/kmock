/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.collections.IsoMutableList
import co.touchlab.stately.collections.sharedMutableListOf
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import tech.antibytes.kmock.KMockContract

internal class SideEffectChain<ReturnValue, SideEffect : Function<ReturnValue>>(
    onAdd: Function0<Unit>,
) : KMockContract.SideEffectChain<ReturnValue, SideEffect> {
    private val onAdd: AtomicRef<Function0<Unit>> = atomic(onAdd)

    private val sideEffects: IsoMutableList<SideEffect> = sharedMutableListOf()

    private fun _next(sideEffects: MutableList<SideEffect>): SideEffect {
        return when (sideEffects.size) {
            0 -> throw IllegalStateException("No SideEffect was stored.")
            1 -> sideEffects.first()
            else -> sideEffects.removeFirst()
        }
    }

    override fun next(): SideEffect = _next(sideEffects)

    override fun add(sideEffect: SideEffect): SideEffectChain<ReturnValue, SideEffect> {
        onAdd.value.invoke()
        sideEffects.add(sideEffect)

        return this
    }

    override fun addAll(sideEffect: Iterable<SideEffect>): SideEffectChain<ReturnValue, SideEffect> {
        onAdd.value.invoke()
        sideEffects.addAll(sideEffect)

        return this
    }

    override fun clear() {
        sideEffects.clear()
    }
}
