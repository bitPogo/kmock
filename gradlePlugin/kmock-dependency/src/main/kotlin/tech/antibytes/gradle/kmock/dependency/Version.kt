/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.dependency

object Version {

    val gradle = Gradle
    const val kotlin = "1.6.10"

    object Gradle {
        /**
         * [Kotlin](https://github.com/JetBrains/kotlin)
         */
        const val kotlin = Version.kotlin

        /**
         * [AnitBytes GradlePlugins](https://github.com/bitPogo/gradle-plugins)
         */
        const val antibytes = "4137039"

        /**
         * [Spotless](https://plugins.gradle.org/plugin/com.diffplug.gradle.spotless)
         */
        const val spotless = "6.3.0"

        /**
         * [Gradle Plugin Publishing](https://plugins.gradle.org/plugin/com.gradle.plugin-publish)
         */
        const val pluginPublishing = "0.20.0"
    }

    val antibytes = Antibytes

    object Antibytes {
        const val test = "7281fe2"
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
        const val kotlinPoet = "1.11.0"
    }

    /**
     * [Compiler Test](https://github.com/tschuchortdev/kotlin-compile-testing)
     */
    const val compilerTest = "1.4.7"
}
