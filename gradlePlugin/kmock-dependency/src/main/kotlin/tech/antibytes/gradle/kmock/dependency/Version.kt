/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.dependency

object Version {

    val gradle = Gradle

    object Gradle {
        /**
         * [Kotlin](https://github.com/JetBrains/kotlin)
         */
        val kotlin = "1.6.10"

        /**
         * [AnitBytes GradlePlugins](https://github.com/bitPogo/gradle-plugins)
         */
        const val antibytes = "ed855ae"

        /**
         * [Spotless](https://plugins.gradle.org/plugin/com.diffplug.gradle.spotless)
         */
        const val spotless = "6.3.0"
    }

    val antibytes = Antibytes

    object Antibytes {
        const val test = "c3e5acf"
    }

    val google = Google

    object Google {
        /**
         * [KSP](https://github.com/google/ksp)
         */
        /**
         * [KSP DevTools on MavenCentral](https://mvnrepository.com/artifact/com.google.devtools.ksp/com.google.devtools.ksp.gradle.plugin)
         */
        const val ksp = "1.6.10-1.0.4"
    }

    val square = Square

    object Square {
        /**
         * [Kotlin Poet](https://square.github.io/kotlinpoet/)
         */
        const val kotlinPoet = "1.10.2"
    }

    /**
     * [Compiler Test](https://github.com/tschuchortdev/kotlin-compile-testing)
     */
    const val compilerTest = "1.4.7"
}
