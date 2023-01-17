/*
 * Copyright (c) 2023 Matthias Geisler (bitPogo) / All rights reserved.
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

dependencies {
    implementation(Dependency.gradle.dependency) {
        exclude(
            group = "org.jetbrains.dokka",
            module = "dokka-gradle-plugin"
        )
    }
}
