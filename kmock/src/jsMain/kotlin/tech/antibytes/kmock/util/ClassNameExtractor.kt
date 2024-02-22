/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ktlint:standard:filename")

package tech.antibytes.kmock.util

import kotlin.reflect.KClass

internal actual fun extractKClassName(clazz: KClass<*>): String? = clazz.toString()
