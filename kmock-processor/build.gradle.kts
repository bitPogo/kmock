/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.dependency.Dependency
import tech.antibytes.gradle.kmock.config.KMockProcessorConfiguration
import tech.antibytes.gradle.kmock.dependency.Dependency as LocalDependency

plugins {
    kotlin("jvm")

    id("tech.antibytes.gradle.publishing")
    id("tech.antibytes.gradle.coverage")
}

group = KMockProcessorConfiguration.group

antiBytesPublishing {
    packageConfiguration = KMockProcessorConfiguration.publishing.packageConfiguration
    repositoryConfiguration = KMockProcessorConfiguration.publishing.repositories
    versioning = KMockProcessorConfiguration.publishing.versioning
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview"
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

dependencies {
    implementation(LocalDependency.kotlin.gradle)
    implementation(Dependency.multiplatform.stately.isolate)
    implementation(Dependency.multiplatform.stately.concurrency)
    implementation(LocalDependency.google.ksp)
    implementation(LocalDependency.square.kotlinPoet.core) {
        exclude(module = "kotlin-reflect")
    }
    implementation(LocalDependency.square.kotlinPoet.ksp) {
        exclude(module = "kotlin-reflect")
    }
    implementation(project(":kmock"))

    testImplementation(LocalDependency.antibytes.test.core)
    testImplementation(LocalDependency.antibytes.test.fixture)
    testImplementation(platform(Dependency.jvm.test.junit))
    testImplementation(Dependency.jvm.test.kotlin)
    testImplementation(Dependency.jvm.test.jupiter)
    testImplementation(Dependency.jvm.test.mockk)
    testImplementation(LocalDependency.compilerTest.core)
    testImplementation(LocalDependency.compilerTest.ksp)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
}
