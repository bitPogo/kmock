/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.dependency.Dependency
import tech.antibytes.gradle.kmock.config.KMockGradleConfiguration
import tech.antibytes.gradle.kmock.dependency.Dependency as LocalDependency
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile as KotlinTaskCompile
import tech.antibytes.gradle.configuration.runtime.AntiBytesMainConfigurationTask
import tech.antibytes.gradle.kmock.config.KMockPublishingConfiguration
import tech.antibytes.gradle.versioning.Versioning

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`

    id("tech.antibytes.gradle.publishing")

    id("tech.antibytes.gradle.coverage")

    // Pin API
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.11.0"
}

group = KMockGradleConfiguration.group

antiBytesPublishing {
    packageConfiguration = KMockGradleConfiguration.publishing.packageConfiguration
    repositoryConfiguration = KMockGradleConfiguration.publishing.repositories
    versioning = KMockGradleConfiguration.publishing.versioning
    signingConfiguration = KMockGradleConfiguration.publishing.signing
}

dependencies {
    implementation(LocalDependency.kotlin.gradle)
    implementation(LocalDependency.gradle.ksp)
    implementation(Dependency.android.androidGradlePlugin)

    testImplementation(LocalDependency.antibytes.test.core)
    testImplementation(LocalDependency.antibytes.test.fixture)
    testImplementation(platform(Dependency.jvm.test.junit))
    testImplementation(Dependency.jvm.test.jupiter)
    testImplementation(Dependency.jvm.test.mockk)
    testImplementation(Dependency.jvm.test.kotlin)
    testImplementation(Dependency.multiplatform.stately.isolate)

    testImplementation(LocalDependency.antibytes.test.gradle)
}

kotlin {
    explicitApi()

    sourceSets.main {
        kotlin.srcDir("${buildDir.absolutePath.trimEnd('/')}/generated/antibytes/main")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinTaskCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
}

tasks.test {
    useJUnitPlatform()
}

val generateConfig by tasks.creating(AntiBytesMainConfigurationTask::class.java) {
    packageName.set("tech.antibytes.gradle.kmock.config")
    stringFields.set(
        mapOf(
            "version" to Versioning.getInstance(
                project = project,
                configuration = KMockPublishingConfiguration().versioning
            ).versionName()
        )
    )

    mustRunAfter("clean")
}

tasks.withType(KotlinCompile::class.java) {
    this.dependsOn(generateConfig)
    this.mustRunAfter(generateConfig)
}

gradlePlugin {
    plugins.register(KMockGradleConfiguration.pluginId) {
        id = KMockGradleConfiguration.pluginId
        group = KMockGradleConfiguration.group
        displayName = KMockGradleConfiguration.publishing.description
        description = KMockGradleConfiguration.longDescription
        implementationClass = "tech.antibytes.gradle.kmock.KMockPlugin"
        version = KMockGradleConfiguration.version
    }
}
configure<JavaPluginExtension> {
    withJavadocJar()
    withSourcesJar()
}
