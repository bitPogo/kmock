/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.kmock.config.publishing.KMockGradleConfiguration
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile as KotlinTaskCompile
import tech.antibytes.gradle.configuration.runtime.AntiBytesMainConfigurationTask
import tech.antibytes.gradle.versioning.Versioning

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`

    alias(antibytesCatalog.plugins.gradle.antibytes.publishing)
    alias(antibytesCatalog.plugins.gradle.antibytes.coverage)
    alias(antibytesCatalog.plugins.gradle.antibytes.dokkaConfiguration)
}

val publishingConfiguration = KMockGradleConfiguration(project)
group = publishingConfiguration.group

antibytesPublishing {
    packaging.set(publishingConfiguration.publishing.packageConfiguration)
    repositories.set(publishingConfiguration.publishing.repositories)
    versioning.set(publishingConfiguration.publishing.versioning)
    signing.set(publishingConfiguration.publishing.signing)
}

dependencies {
    implementation(antibytesCatalog.gradle.kotlin.kotlin)
    implementation(antibytesCatalog.gradle.ksp.plugin)//.dependency)
    implementation(antibytesCatalog.gradle.agp)

    testImplementation(libs.testUtils.core)
    testImplementation(libs.kfixture)
    testImplementation(antibytesCatalog.jvm.test.junit.runtime)
    testImplementation(platform(antibytesCatalog.jvm.test.junit.bom))
    testImplementation(antibytesCatalog.jvm.test.mockk)
    testImplementation(antibytesCatalog.jvm.test.kotlin.core)
    testImplementation(antibytesCatalog.jvm.test.kotlin.junit5)
    testImplementation(antibytesCatalog.jvm.stately.isolate)

    testImplementation(antibytesCatalog.gradle.test.antibytes.testUtils)
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
    dependsOn(generateConfig)
    mustRunAfter(generateConfig)
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

tasks.test {
    useJUnitPlatform()
}
