/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("UnusedReceiverParameter")

package tech.antibytes.gradle.kmock.source

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project
import tech.antibytes.gradle.kmock.config.MainConfig

internal fun DependencyHandler.determineProcessor(): Any = MainConfig.processor
