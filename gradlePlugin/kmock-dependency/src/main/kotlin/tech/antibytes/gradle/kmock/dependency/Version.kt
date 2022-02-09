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
         * [AnitBytes GradlePlugins](https://github.com/bitPogo/gradle-plugins)
         */
        const val antibytes = "99eff34"

        /**
         * [Spotless](https://plugins.gradle.org/plugin/com.diffplug.gradle.spotless)
         */
        const val spotless = "6.2.0"

        /**
         * [KSP](https://github.com/google/ksp)
         */
        const val ksp = "1.6.10-1.0.2"
    }

    val antibytes = Antibytes

    object Antibytes {
        val test = "3469f60-add-ios-SNAPSHOT"
    }
}
