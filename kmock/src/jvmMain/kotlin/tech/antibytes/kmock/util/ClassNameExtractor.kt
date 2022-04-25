/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.util

import kotlin.reflect.KClass

actual fun extractKClassName(clazz: KClass<*>): String? = clazz.qualifiedName
