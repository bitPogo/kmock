/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ktlint:standard:filename")

package tech.antibytes.kmock.proxy

internal fun <ReturnValue, SideEffect : Function<ReturnValue>> Any?.guardSpy(spyOn: SideEffect): SideEffect? {
    return if (this != null) {
        spyOn
    } else {
        null
    }
}
