/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.dependency.Dependency
import tech.antibytes.gradle.kmock.config.KMockGradleConfiguration
import tech.antibytes.gradle.kmock.dependency.Dependency as LocalDependency

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`

    id("tech.antibytes.gradle.publishing")
    id("tech.antibytes.gradle.coverage")
}

group = KMockGradleConfiguration.group

antiBytesPublishing {
    packageConfiguration = KMockGradleConfiguration.publishing.packageConfiguration
    repositoryConfiguration = KMockGradleConfiguration.publishing.repositories
    versioning = KMockGradleConfiguration.publishing.versioning
}

dependencies {
    implementation(LocalDependency.kotlin.gradle)
    implementation(Dependency.multiplatform.stately.isolate)
    implementation(Dependency.multiplatform.stately.concurrency)
    implementation(LocalDependency.google.ksp)
    implementation(LocalDependency.square.kotlinPoet.core)
    implementation(LocalDependency.square.kotlinPoet.ksp)
    implementation(LocalDependency.antibytes.gradle.util)
    implementation(project(":kmock"))

    testImplementation(LocalDependency.antibytes.test.core)
    testImplementation(LocalDependency.antibytes.test.fixture)
    testImplementation(platform(Dependency.jvm.test.junit))
    testImplementation(Dependency.jvm.test.jupiter)
    testImplementation(Dependency.jvm.test.mockk)

    testImplementation(LocalDependency.antibytes.test.gradle)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
}

val templatesPath = "${projectDir}/src/main/resources/template"
val configPath = "${projectDir}/src-gen/main/kotlin/tech/antibytes/gradle/kmock/config"

val provideConfig: Task by tasks.creating {
    doFirst {
        val templates = File(templatesPath)
        val configs = File(configPath)

        val config = File(templates, "Config.tmpl")
            .readText()
            .replace("VERSION", project.version as String)

        if (!configs.exists()) {
            if(!configs.mkdir()) {
                System.err.println("Creation of the configuration directory failed!")
            }
        }
        File(configPath, "Config.kt").writeText(config)
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinCompile::class.java) {
    this.dependsOn(provideConfig)
}

sourceSets.getByName("main") {
    java.srcDir("src-gen/main/kotlin")
}

gradlePlugin {
    plugins.register(KMockGradleConfiguration.pluginId) {
        group = KMockGradleConfiguration.group
        id = KMockGradleConfiguration.pluginId
        implementationClass = "tech.antibytes.gradle.kmock.KMock"
        displayName = "${id}.gradle.plugin"
        description = "KMock Stub Generator"
        version = "0.1.0"
    }
}
