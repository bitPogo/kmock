/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.kmock.config.publishing.KMockProcessorConfiguration
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(antibytesCatalog.plugins.kotlin.jvm.get().pluginId)

    alias(antibytesCatalog.plugins.gradle.antibytes.publishing)
    alias(antibytesCatalog.plugins.gradle.antibytes.coverage)
}

val publishingConfiguration = KMockProcessorConfiguration(project)
group = publishingConfiguration.group

antibytesPublishing {
    packaging.set(publishingConfiguration.publishing.packageConfiguration)
    repositories.set(publishingConfiguration.publishing.repositories)
    versioning.set(publishingConfiguration.publishing.versioning)
    signing.set(publishingConfiguration.publishing.signing)
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
    implementation(projects.kmock)

    testImplementation(antibytesCatalog.testUtils.core)
    testImplementation(antibytesCatalog.kfixture)
    testImplementation(antibytesCatalog.jvm.test.junit.runtime)
    testImplementation(platform(antibytesCatalog.jvm.test.junit.bom))
    testImplementation(antibytesCatalog.jvm.test.mockk)
    testImplementation(antibytesCatalog.jvm.test.kotlin.core)
    testImplementation(antibytesCatalog.jvm.test.kotlin.junit5)
    testImplementation(antibytesCatalog.jvm.stately.collections)
    testImplementation(antibytesCatalog.jvm.test.compiler.core)
    testImplementation(antibytesCatalog.jvm.test.compiler.ksp)
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
