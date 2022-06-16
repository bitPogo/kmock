/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.kmock.dependency.Dependency
import tech.antibytes.gradle.kmock.dependency.addCustomRepositories
import tech.antibytes.gradle.kmock.dependency.ensureKotlinVersion

plugins {
    `kotlin-dsl`

    id("tech.antibytes.gradle.kmock.dependency")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    addCustomRepositories()
}
ensureKotlinVersion("1.5.31")

dependencies {
    implementation(Dependency.gradle.dependency)
    implementation(Dependency.gradle.publishing)
    implementation(Dependency.gradle.versioning)
    implementation(Dependency.gradle.coverage)
    implementation(Dependency.gradle.spotless)
    implementation(Dependency.gradle.projectConfig)
    implementation(Dependency.gradle.runtimeConfig)
    implementation(Dependency.gradle.ksp)
}
