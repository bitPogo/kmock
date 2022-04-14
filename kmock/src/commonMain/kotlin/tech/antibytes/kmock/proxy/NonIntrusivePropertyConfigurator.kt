/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.Relaxer

internal class NonIntrusivePropertyConfigurator<Value> :
    KMockContract.NonIntrusivePropertyConfigurator<Value>,
    KMockContract.NonIntrusivePropertyTarget<Value> {
    private var relaxer: Relaxer<Value>? = null
    private var spyOn: Function0<Value>? = null

    override fun useRelaxerIf(condition: Boolean, relaxer: Function1<String, Value>) {
        this.relaxer = condition.guardRelaxer(relaxer)
    }

    override fun isRelaxable(): Boolean = relaxer != null

    override fun unwrapRelaxer(): Relaxer<Value>? = relaxer

    override fun useSpyIf(spyTarget: Any?, spyOn: Function0<Value>) {
        this.spyOn = spyTarget.guardSpy(spyOn)
    }

    override fun isSpyable(): Boolean = spyOn != null

    override fun unwrapSpy(): Function0<Value>? = spyOn
}
