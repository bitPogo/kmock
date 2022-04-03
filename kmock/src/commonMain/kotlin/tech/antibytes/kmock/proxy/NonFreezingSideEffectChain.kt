/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract

internal class NonFreezingSideEffectChain<ReturnValue, SideEffect : Function<ReturnValue>>(
    private val onAdd: Function0<Unit>,
) : KMockContract.SideEffectChain<ReturnValue, SideEffect> {
    private val sideEffects: MutableList<SideEffect> = mutableListOf()

    private fun _next(sideEffects: MutableList<SideEffect>): SideEffect {
        return when (sideEffects.size) {
            0 -> throw IllegalStateException("No SideEffect was stored.")
            1 -> sideEffects.first()
            else -> sideEffects.removeFirst()
        }
    }

    override fun next(): SideEffect = _next(sideEffects)

    override fun add(sideEffect: SideEffect): NonFreezingSideEffectChain<ReturnValue, SideEffect> {
        onAdd.invoke()
        sideEffects.add(sideEffect)

        return this
    }

    override fun addAll(sideEffect: Iterable<SideEffect>): NonFreezingSideEffectChain<ReturnValue, SideEffect> {
        onAdd.invoke()
        sideEffects.addAll(sideEffect)

        return this
    }

    override fun clear() {
        sideEffects.clear()
    }
}
