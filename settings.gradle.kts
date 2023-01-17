/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */
import tech.antibytes.gradle.dependency.settings.localGithub

pluginManagement {
    repositories {
        val antibytesPlugins = "^tech\\.antibytes\\.[\\.a-z\\-]+"
        gradlePluginPortal()
        google()
        maven {
            setUrl("https://raw.github.com/bitPogo/maven-snapshots/main/snapshots")
            content {
                includeGroupByRegex(antibytesPlugins)
            }
        }
        maven {
            setUrl("https://raw.github.com/bitPogo/maven-rolling-releases/main/rolling")
            content {
                includeGroupByRegex(antibytesPlugins)
            }
        }
        mavenCentral()
    }
}

plugins {
    id("tech.antibytes.gradle.dependency.settings") version "15fbbaa"
}

includeBuild("setup")

includeBuild("gradlePlugin/kmock-dependency")
includeBuild("gradlePlugin/kmock-test-plugin")

include(
    ":kmock",
    ":kmock-processor",
    ":kmock-gradle",
    ":docs",
)
include(
    ":playground",
    ":integration-kmp",
    ":integration-android-application",
)

dependencyResolutionManagement {
    versionCatalogs {
        /*
        getByName("antibytesCatalog") {
            version("kotlin-kotlin-plugin", "1.7.20")
            version("kotlin-bom", "1.7.20")
            version("kotlin-kotlin-dependency", "1.7.20")
            version("kotlin-language", "1.7.20")
            version("kotlin-multiplatform", "1.7.20")
            version("kotlin-stdlib-common", "1.7.20")
            version("kotlin-stdlib-jdk7", "1.7.20")
            version("kotlin-stdlib-jdk8", "1.7.20")
            version("kotlin-stdlib-js", "1.7.20")
            version("kotlin-stdlib-jvm", "1.7.20")
            version("kotlin-stdlib-wasm", "1.7.20")
            version("kotlin-test-annotations", "1.7.20")
            version("kotlin-test-core-common", "1.7.20")
            version("kotlin-test-core-js", "1.7.20")
            version("kotlin-test-core-jvm", "1.7.20")
            version("kotlin-test-core-wasm", "1.7.20")
            version("kotlin-test-junit4", "1.7.20")
        }*/
    }
}

buildCache {
    localGithub()
}

rootProject.name = "kmock"
