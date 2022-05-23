/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

/**
 * Ensures usage of JvmName in Common.
 */
expect annotation class SafeJvmName(val name: String)
