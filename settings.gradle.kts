/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */
import tech.antibytes.gradle.dependency.settings.fullCache

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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

dependencyResolutionManagement {
    versionCatalogs {
        getByName("antibytesCatalog") {
            version("minSdk", "21")
            version("kfixture", "0.4.0-SNAPSHOT")
            version("testUtils", "b6c6e6c")
            version("kotlinx-coroutines-core", "1.7.1")
            version("kotlinx-coroutines-test", "1.7.1")

            library("kfixture", "tech.antibytes.kfixture", "core").versionRef("kfixture")
            library("testUtils-core", "tech.antibytes.test-utils-kmp", "test-utils").versionRef("testUtils")
            library("testUtils-annotations", "tech.antibytes.test-utils-kmp", "test-utils-annotations-junit4").versionRef("testUtils")
            library("testUtils-coroutine", "tech.antibytes.test-utils-kmp", "test-utils-coroutine").versionRef("testUtils")
        }
    }
}

plugins {
    id("tech.antibytes.gradle.dependency.settings") version "288f8da"
}

includeBuild("setup")
includeBuild("kmock-test-plugin")

include(
    ":kmock",
    ":kmock-processor",
    ":kmock-gradle",
    ":docs",
    ":playground",
    ":integration-kmp",
    ":integration-android-application",
)

buildCache {
    fullCache(rootDir)
}

// see: https://github.com/gradle/gradle/issues/16608
rootProject.name = "kmock-lib"
