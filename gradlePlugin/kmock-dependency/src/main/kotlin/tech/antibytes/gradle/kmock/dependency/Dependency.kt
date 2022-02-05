/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.dependency

object Dependency {
    val gradle = GradlePlugin
    val antibytes = AntiBytes

    object AntiBytes {
        val test = Test

        object Test {
            val annotations = "tech.antibytes.test-utils-kmp:test-utils-annotations:${Version.antibytes.test}"
            val core = "tech.antibytes.test-utils-kmp:test-utils:${Version.antibytes.test}"
            val fixture = "tech.antibytes.test-utils-kmp:test-utils-fixture:${Version.antibytes.test}"
            val coroutine = "tech.antibytes.test-utils-kmp:test-utils-coroutine:${Version.antibytes.test}"
            val ktor = "tech.antibytes.test-utils-kmp:test-utils-ktor:${Version.antibytes.test}"
        }
    }
}
