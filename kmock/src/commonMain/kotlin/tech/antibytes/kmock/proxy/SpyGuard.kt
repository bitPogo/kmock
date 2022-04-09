/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

internal fun <ReturnValue, SideEffect : Function<ReturnValue>> Any?.guardSpy(spyOn: SideEffect): SideEffect? {
    return if (this != null) {
        spyOn
    } else {
        null
    }
}
