/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Relaxer
import kotlin.reflect.KClass

internal class NonIntrusiveFunConfigurator<ReturnValue, SideEffect : Function<ReturnValue>> :
    KMockContract.NonIntrusiveFunConfigurator<ReturnValue, SideEffect>,
    KMockContract.NonIntrusiveFunTarget<ReturnValue, SideEffect> {

    private var relaxer: Relaxer<ReturnValue>? = null
    private var spyOn: SideEffect? = null

    @Suppress("UNCHECKED_CAST")
    override fun useUnitFunRelaxerIf(condition: Boolean) {
        relaxer = condition.guardRelaxer(::kmockUnitFunRelaxer as Function1<String, ReturnValue>)
    }

    override fun useRelaxerIf(
        condition: Boolean,
        relaxer: Function1<String, ReturnValue>
    ) {
        this.relaxer = condition.guardRelaxer(relaxer)
    }

    override fun isRelaxable(): Boolean = relaxer != null

    override fun unwrapRelaxer(): Relaxer<ReturnValue>? = relaxer

    override fun useSpyIf(spyTarget: Any?, spyOn: SideEffect) {
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
        } as SideEffect?
    }

    override fun isSpyable(): Boolean = spyOn != null

    override fun unwrapSpy(): SideEffect? = spyOn
}
