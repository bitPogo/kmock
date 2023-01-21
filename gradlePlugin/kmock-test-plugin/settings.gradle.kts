/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */
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
    id("tech.antibytes.gradle.dependency.settings") version "022f831"
}

includeBuild("../../setup")

include(
    ":kmock",
    ":kmock-processor",
    ":kmock-gradle",
)

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
        getByName("antibytesCatalog") {
            version("gradle-ksp-plugin-dependency", "1.7.20-1.0.8")
            version("gradle-ksp-runtime", "1.7.20-1.0.8")
            version("gradle-ksp-plugin-plugin", "1.7.20-1.0.8")
        }
    }
}

rootProject.name = "kmock"
