/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

/**
 * Relaxer for functions which return Unit
 * @param T actual type of the return value.
 */
inline fun <reified T> relaxVoidFunction(): T? {
    return if (T::class == Unit::class) {
        Unit as T
    } else {
        null
    }
}
