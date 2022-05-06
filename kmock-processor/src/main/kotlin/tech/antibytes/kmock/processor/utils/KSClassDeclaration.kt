/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal fun KSClassDeclaration.deriveSimpleName(packageName: String): String {
    val qualifiedName = ensureNotNullClassName(this.qualifiedName?.asString())

    return if (!qualifiedName.startsWith(packageName) || qualifiedName == packageName) {
        throw IllegalStateException("Malformed class name!")
    } else {
        qualifiedName.substringAfter("$packageName.")
    }
}
