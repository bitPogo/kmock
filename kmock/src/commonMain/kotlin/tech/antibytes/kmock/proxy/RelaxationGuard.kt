/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ktlint:standard:filename")

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract.Relaxer

internal fun <ReturnValue> Boolean.guardRelaxer(relaxer: Function1<String, ReturnValue>): Relaxer<ReturnValue>? {
    return if (this) {
        Relaxer { mockId -> relaxer.invoke(mockId) }
    } else {
        null
    }
}
