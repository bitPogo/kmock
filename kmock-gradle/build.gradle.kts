/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.dependency.Dependency
import tech.antibytes.gradle.kmock.config.KMockGradleConfiguration
import tech.antibytes.gradle.kmock.dependency.Dependency as LocalDependency
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import tech.antibytes.gradle.configuration.runtime.AntiBytesMainConfigurationTask

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

afterEvaluate {
    val generateConfig by tasks.creating(AntiBytesMainConfigurationTask::class.java) {
        packageName.set("tech.antibytes.gradle.kmock.config")
        stringFields.set(
            mapOf(
                "version" to project.version as String
            )
        )
    }

    tasks.withType(KotlinCompile::class.java) {
        this.dependsOn(generateConfig)
    }
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("${buildDir.absolutePath.trimEnd('/')}/generated/antibytes/main")
    }
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
