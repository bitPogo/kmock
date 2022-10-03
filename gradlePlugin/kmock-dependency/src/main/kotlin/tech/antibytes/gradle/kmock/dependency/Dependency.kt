/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.dependency

object Dependency {
    val gradle = GradlePlugin
    val antibytes = AntiBytes

    val kotlin = Kotlin

    object Kotlin {
        val gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.gradle.kotlin}"
    }

    object AntiBytes {

        val test = Test

        object Test {
            const val annotations = "tech.antibytes.test-utils-kmp:test-utils-annotations:${Version.antibytes.test}"
            const val core = "tech.antibytes.test-utils-kmp:test-utils:${Version.antibytes.test}"
            const val fixture = "tech.antibytes.kfixture:core:${Version.antibytes.kfixture}"
            const val coroutine = "tech.antibytes.test-utils-kmp:test-utils-coroutine:${Version.antibytes.test}"
            const val gradle = "tech.antibytes.gradle-plugins:antibytes-gradle-test-utils:${Version.gradle.antibytes}"

            val android = AndroidTest

            object AndroidTest {
                const val annotations = "tech.antibytes.test-utils-kmp:test-utils-annotations-android:${Version.antibytes.test}"
                const val core = "tech.antibytes.test-utils-kmp:test-utils-android:${Version.antibytes.test}"
                const val fixture = "tech.antibytes.kfixture:core-android:${Version.antibytes.kfixture}"
                const val coroutine = "tech.antibytes.test-utils-kmp:test-utils-coroutine-android:${Version.antibytes.test}"
                const val ktor = "tech.antibytes.test-utils-kmp:test-utils-ktor-android:${Version.antibytes.test}"
            }
        }
    }

    val google = Google

    object Google {
        const val ksp = "com.google.devtools.ksp:symbol-processing-api:${Version.google.ksp}"
    }

    val square = Square

    object Square {
        val kotlinPoet = KotlinPoet

        object KotlinPoet {
            const val core = "com.squareup:kotlinpoet:${Version.square.kotlinPoet}"
            const val ksp = "com.squareup:kotlinpoet-ksp:${Version.square.kotlinPoet}"
        }
    }

    val compilerTest = CompilerTest

    object CompilerTest {
        const val core = "com.github.tschuchortdev:kotlin-compile-testing:${Version.compilerTest}"
        const val ksp = "com.github.tschuchortdev:kotlin-compile-testing-ksp:${Version.compilerTest}"
    }
}
