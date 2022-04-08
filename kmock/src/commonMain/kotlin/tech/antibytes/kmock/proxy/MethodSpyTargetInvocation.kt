/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import kotlin.reflect.KClass

internal class MethodSpyTargetInvocation<Value, SpyTarget : Function<Value>> : KMockContract.MethodSpyTargetInvocation<Value, SpyTarget> {
    private var spyOn: SpyTarget? = null

    override fun useSpyIf(spyTarget: Any?, spyOn: SpyTarget) {
        this.spyOn = spyTarget.guardSpy(spyOn)
    }

    override fun useSpyOnEqualsIf(
        spyTarget: Any?,
        other: Any?,
        spyOn: Function1<Any?, Boolean>,
        mockKlass: KClass<out Any>
    ) {
        @Suppress("UNCHECKED_CAST")
        this.spyOn = spyTarget.guardSpy {
            if (other != null && other::class == mockKlass) {
                spyOn(other)
            } else {
                spyTarget == other
            }
        } as SpyTarget
    }

    override fun unwrap(): SpyTarget? = spyOn
}
