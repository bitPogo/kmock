/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.collections.sharedMutableListOf
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.SideEffectChainState

internal class SideEffectChain<ReturnValue, SideEffect : Function<ReturnValue>>(
    freeze: Boolean,
    onAdd: Function0<Unit>,
) : KMockContract.SideEffectChain<ReturnValue, SideEffect> {
    private class FreezingSideEffectChainState<ReturnValue, SideEffect : Function<ReturnValue>>(
        onAdd: Function0<Unit>,
    ) : SideEffectChainState<ReturnValue, SideEffect> {
        private val _onAdd: AtomicRef<Function0<Unit>> = atomic(onAdd)

        override val onAdd: Function0<Unit> by _onAdd
        override val sideEffects: MutableList<SideEffect> = sharedMutableListOf()
    }

    private class NonFreezingSideEffectChainState<ReturnValue, SideEffect : Function<ReturnValue>>(
        override val onAdd: Function0<Unit>,
    ) : SideEffectChainState<ReturnValue, SideEffect> {
        override val sideEffects: MutableList<SideEffect> = mutableListOf()
    }

    private val state: SideEffectChainState<ReturnValue, SideEffect> = if (freeze) {
        FreezingSideEffectChainState(onAdd)
    } else {
        NonFreezingSideEffectChainState(onAdd)
    }

    override fun next(): SideEffect {
        return when (state.sideEffects.size) {
            0 -> throw IllegalStateException("No SideEffect was stored.")
            1 -> state.sideEffects.first()
            else -> state.sideEffects.removeFirst()
        }
    }

    override fun add(sideEffect: SideEffect): SideEffectChain<ReturnValue, SideEffect> {
        state.onAdd.invoke()
        state.sideEffects.add(sideEffect)

        return this
    }

    override fun addAll(sideEffect: Iterable<SideEffect>): SideEffectChain<ReturnValue, SideEffect> {
        state.onAdd.invoke()
        state.sideEffects.addAll(sideEffect)

        return this
    }

    override fun clear() {
        state.sideEffects.clear()
    }
}
