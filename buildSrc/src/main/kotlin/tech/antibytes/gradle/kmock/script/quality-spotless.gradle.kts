/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.script

import tech.antibytes.gradle.dependency.Version

/**
 * Quality check to keep the code spotless using [Spotless](https://github.com/diffplug/spotless)
 *
 * It uses Ktlint to format and validate Kotlin code style.
 *
 * Install:
 *
 * You need to add following dependencies to the buildSrc/build.gradle.kts
 *
 * dependencies {
 *     implementation("com.diffplug.spotless:spotless-plugin-gradle:5.14.3"")
 *     implementation("com.pinterest:ktlint:0.42.3") or higher
 * }
 *
 * and ensure that the gradlePluginPortal is available
 *
 * repositories {
 *     gradlePluginPortal()
 * }
 *
 * Now just add id("tech.antibytes.gradle.test.script.quality-spotless") to your rootProject build.gradle.kts plugins
 *
 * plugins {
 *     id("tech.antibytes.gradle.test.script.quality-spotless")
 * }
 */
plugins {
    id("com.diffplug.spotless")
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude(
            "buildSrc/build/",
            "**/buildSrc/build/",
            "**/kmock-processor/src/test/resources/",
            "**/kmock-gradle/src/test/resources/"
        )
        ktlint(Version.gradle.ktLint).editorConfigOverride( // see: https://github.com/diffplug/spotless/issues/142
            mapOf(
                "disabled_rules" to "no-wildcard-imports,filename",
                "ij_kotlin_imports_layout" to "*",
                "ij_kotlin_allow_trailing_comma" to "true",
                "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
            )
        )
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(Version.gradle.ktLint)
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
    format("misc") {
        target("**/*.adoc", "**/*.md", "**/.gitignore", ".java-version")

        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}
