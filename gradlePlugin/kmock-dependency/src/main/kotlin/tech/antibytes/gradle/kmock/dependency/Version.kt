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
        const val antibytes = "80c1464"

        /**
         * [Spotless](https://plugins.gradle.org/plugin/com.diffplug.gradle.spotless)
         */
        const val spotless = "6.2.0"
    }

    val antibytes = Antibytes

    object Antibytes {
        val test = "047a358-add-ios-SNAPSHOT"
    }

    val google = Google

    object Google {
        /**
         * [KSP](https://github.com/google/ksp)
         */
        const val ksp = "1.6.10-1.0.2"
    }

    val square = Square

    object Square {
        /**
         * [Kotlin Poet](https://square.github.io/kotlinpoet/)
         */
        val kotlinPoet = "1.10.2"
    }
}
