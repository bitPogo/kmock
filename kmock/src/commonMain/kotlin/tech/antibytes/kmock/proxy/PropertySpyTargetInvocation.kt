/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract

internal class PropertySpyTargetInvocation<Value> : KMockContract.PropertySpyTargetInvocation<Value> {
    private var spyOn: Function0<Value>? = null

    override fun useSpyIf(spyTarget: Any?, spyOn: Function0<Value>) {
        this.spyOn = spyTarget.guardSpy(spyOn)
    }

    override fun unwrap(): Function0<Value>? = spyOn
}
