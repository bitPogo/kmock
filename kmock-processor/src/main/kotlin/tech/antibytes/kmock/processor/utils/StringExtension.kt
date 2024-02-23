/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import java.util.Locale

internal fun String.titleCase(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(Locale.getDefault())
        } else {
            it.toString()
        }
    }
}

internal fun ensureNotNullClassName(name: String?): String {
    return name ?: throw IllegalStateException("Expected non null class name!")
}
