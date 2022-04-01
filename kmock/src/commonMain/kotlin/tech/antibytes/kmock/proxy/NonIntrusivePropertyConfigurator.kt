/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.NonIntrusivePropertyConfiguration
import tech.antibytes.kmock.KMockContract.Relaxer

internal class NonIntrusivePropertyConfigurator<Value> :
    KMockContract.NonIntrusivePropertyConfigurator<Value>,
    KMockContract.NonIntrusiveConfigurationReceiver<NonIntrusivePropertyConfiguration<Value>> {
    private var relaxer: Relaxer<Value>? = null
    private var spyOnGet: Function0<Value>? = null
    private var spyOnSet: Function1<Value, Unit>? = null

    private fun finalizeRelaxers() {
        if (spyOnGet != null || spyOnSet != null) {
            relaxer = null
        }
    }

    override fun useRelaxerIf(condition: Boolean, relaxer: Function1<String, Value>) {
        this.relaxer = condition.guardRelaxer(relaxer)
    }

    override fun useSpyOnGetIf(spy: Any?, spyOn: Function0<Value>) {
        this.spyOnGet = spy.guardSpy(spyOn)
    }

    override fun useSpyOnSetIf(spy: Any?, spyOn: Function1<Value, Unit>) {
        this.spyOnSet = spy.guardSpy(spyOn)
    }

    override fun getConfiguration(): NonIntrusivePropertyConfiguration<Value> {
        finalizeRelaxers()

        return NonIntrusivePropertyConfiguration(
            relaxer = relaxer,
            spyOnGet = spyOnGet,
            spyOnSet = spyOnSet,
        )
    }
}
