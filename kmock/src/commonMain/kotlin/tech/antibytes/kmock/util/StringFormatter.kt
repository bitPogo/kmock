/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.util

private fun formatArg(arg: Any?): String {
    return if (arg is Array<*>) {
        "[${arg.joinToString(", ")}]"
    } else {
        arg.toString()
    }
}

private fun formatPart(
    builder: StringBuilder,
    part: String,
    args: Array<out Any?>,
) {
    val end = part.indexOfFirst { char -> !char.isDigit() }

    val idx = if (end != -1) {
        part.substring(0, end).toInt()
    } else {
        part.toInt()
    }

    builder.append(formatArg(args[idx]))

    if (end != -1) {
        builder.append(part.substring(startIndex = end))
    }
}

private fun formatParts(
    parts: List<String>,
    args: Array<out Any?>,
): String {
    val builder = StringBuilder(parts[0])

    for (idx in 1 until parts.size) {
        formatPart(builder, parts[idx], args)
    }

    return builder.toString()
}

internal fun String.format(vararg args: Any?): String {
    val parts = this.split("%")

    return if (parts.size == 1) {
        parts[0]
    } else {
        formatParts(parts, args)
    }
}
