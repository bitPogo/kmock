/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */
import tech.antibytes.gradle.dependency.settings.localGithub

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
            version("gradle-ksp-plugin-dependency", "1.8.0-1.0.8")
            version("gradle-ksp-runtime", "1.8.0-1.0.8")
            version("gradle-ksp-plugin-plugin", "1.8.0-1.0.8")

            version("kotlin-android", "1.8.0")
            version("kotlin-jvm", "1.8.0")
            version("kotlin-kotlin-dependency", "1.8.0")
            version("kotlin-kotlin-plugin", "1.8.0")
            version("kotlin-language", "1.8.0")
            version("kotlin-multiplatform", "1.8.0")
            version("kotlin-reflect", "1.8.0")
            version("kotlin-stdlib-common", "1.8.0")
            version("kotlin-stdlib-jdk", "1.8.0")
            version("kotlin-stdlib-jdk8", "1.8.0")
            version("kotlin-stdlib-js", "1.8.0")
            version("kotlin-test-annotations", "1.8.0")
            version("kotlin-test-core-common", "1.8.0")
            version("kotlin-test-core-js", "1.8.0")
            version("kotlin-test-core-jvm", "1.8.0")
            version("kotlin-test-core-wasm", "1.8.0")
            version("kotlin-test-junit4", "1.8.0")
            version("kotlin-test-junit5", "1.8.0")
        }
    }
}

plugins {
    id("tech.antibytes.gradle.dependency.settings") version "022f831"
}

includeBuild("setup")
includeBuild("gradlePlugin/kmock-test-plugin")

include(
    ":kmock",
    ":kmock-processor",
    ":kmock-gradle",
    ":docs",
    ":playground"
    // ":integration-kmp", ignored until 1.8 full integration
    // ":integration-android-application" ignored until 1.8 full integration
)

buildCache {
    localGithub()
}

// see: https://github.com/gradle/gradle/issues/16608
rootProject.name = "kmock-lib"
