/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.dependency.Dependency
import tech.antibytes.gradle.kmock.config.publishing.KMockProcessorConfiguration
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tech.antibytes.gradle.kmock.dependency.Dependency as LocalDependency

plugins {
    kotlin("jvm")
}

group = KMockProcessorConfiguration(project).group

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
}

dependencies {
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
    testImplementation(Dependency.multiplatform.stately.collections)
    testImplementation(platform(Dependency.jvm.test.junit))
    testImplementation(Dependency.jvm.test.kotlin)
    testImplementation(Dependency.jvm.test.jupiter)
    testImplementation(Dependency.jvm.test.mockk.unit)
    testImplementation(LocalDependency.compilerTest.core)
    testImplementation(LocalDependency.compilerTest.ksp)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.test {
    useJUnitPlatform()
}

configure<JavaPluginExtension> {
    withJavadocJar()
    withSourcesJar()
}
