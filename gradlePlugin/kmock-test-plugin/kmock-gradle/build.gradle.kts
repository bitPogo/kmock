/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.dependency.Dependency
import tech.antibytes.gradle.kmock.config.publishing.KMockConfiguration
import tech.antibytes.gradle.kmock.dependency.Dependency as LocalDependency
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile as KotlinTaskCompile
import tech.antibytes.gradle.configuration.runtime.AntiBytesMainConfigurationTask
import tech.antibytes.gradle.versioning.Versioning
import tech.antibytes.gradle.kmock.config.publishing.KMockGradleConfiguration

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.palantir.git-version")
}

dependencies {
    implementation(LocalDependency.kotlin.gradle)
    implementation(LocalDependency.gradle.ksp)
    implementation(Dependency.android.androidGradlePlugin)

    testImplementation(LocalDependency.antibytes.test.core)
    testImplementation(LocalDependency.antibytes.test.fixture)
    testImplementation(platform(Dependency.jvm.test.junit))
    testImplementation(Dependency.jvm.test.jupiter)
    testImplementation(Dependency.jvm.test.mockk.unit)
    testImplementation(Dependency.jvm.test.kotlin)
    testImplementation(Dependency.multiplatform.stately.isolate)

    testImplementation(LocalDependency.antibytes.test.gradle)
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("${buildDir.absolutePath.trimEnd('/')}/generated/antibytes/main/kotlin")
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

val publishingConfiguration = KMockGradleConfiguration(project)
val generateConfig by tasks.creating(AntiBytesMainConfigurationTask::class.java) {
    packageName.set("tech.antibytes.gradle.kmock.config")
    stringFields.set(
        mapOf(
            "version" to Versioning.getInstance(
                project = project,
                configuration = publishingConfiguration.publishing.versioning
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
    plugins.register(publishingConfiguration.pluginId) {
        id = publishingConfiguration.pluginId
        group = publishingConfiguration.group
        displayName = publishingConfiguration.publishing.description
        description = publishingConfiguration.longDescription
        implementationClass = "tech.antibytes.gradle.kmock.KMockPlugin"
        version = publishingConfiguration.version
    }
}
configure<JavaPluginExtension> {
    withJavadocJar()
    withSourcesJar()
}
