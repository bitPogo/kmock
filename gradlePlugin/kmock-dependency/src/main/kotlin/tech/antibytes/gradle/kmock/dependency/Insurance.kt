/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache License, Version 2.0
 */

package tech.antibytes.gradle.kmock.dependency

import org.gradle.api.Project

private val modules = listOf(
    "kotlin-stdlib-jdk7",
    "kotlin-stdlib-jdk8",
    "kotlin-stdlib",
    "kotlin-stdlib-common",
    "kotlin-reflect"
)

fun Project.ensureKotlinVersion(version: String? = null) {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name in modules) {
                useVersion(version ?: Version.kotlin)
                because("Avoid resolution conflicts")
            }
        }
    }
}
