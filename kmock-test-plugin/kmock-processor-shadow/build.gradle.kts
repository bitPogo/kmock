/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.kmock.config.publishing.KMockProcessorConfiguration
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(antibytesCatalog.plugins.kotlin.jvm.get().pluginId)

    alias(antibytesCatalog.plugins.gradle.antibytes.javaConfiguration)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
}

dependencies {
    implementation(antibytesCatalog.gradle.ksp.runtime)
    implementation(antibytesCatalog.jvm.square.kotlinPoet.core) {
        exclude(module = "kotlin-reflect")
    }
    implementation(antibytesCatalog.jvm.square.kotlinPoet.ksp) {
        exclude(module = "kotlin-reflect")
    }
    implementation(projects.kmockShadow)
}
