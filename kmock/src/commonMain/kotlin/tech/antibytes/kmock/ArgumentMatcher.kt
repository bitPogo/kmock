/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.kmock.KMockContract.GetOrSet

internal fun Array<out Any?>?.hadBeenCalledWith(vararg values: Any?): Boolean {
    return when {
        this == null -> values.isEmpty()
        values.isEmpty() -> true
        else -> values.all { value -> this.contains(value) }
    }
}

internal fun Array<out Any?>?.hadBeenStrictlyCalledWith(vararg values: Any?): Boolean {
    return this?.contentDeepEquals(values) ?: values.isEmpty()
}

internal fun Array<out Any?>?.hadBeenCalledWithout(vararg values: Any?): Boolean {
    return if (this == null) {
        values.isNotEmpty()
    } else {
        values.none { value -> this.contains(value) }
    }
}

internal fun GetOrSet.wasGotten(): Boolean = this is GetOrSet.Get

internal fun GetOrSet.wasSet(): Boolean = this is GetOrSet.Set

internal fun GetOrSet.wasSetTo(value: Any?): Boolean {
    return when (this) {
        !is GetOrSet.Set -> false
        else -> this.value == value
    }
}
