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

    alias(antibytesCatalog.plugins.gradle.antibytes.javaConfiguration)
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
    // signing.set(publishingConfiguration.publishing.signing)
}

configure<SourceSetContainer> {
    main {
        java.srcDirs(
            "src/main/kotlin",
            "src-processor/main/kotlin",
        )
    }
}

dependencies {
    implementation(antibytesCatalog.gradle.kotlin.kotlin)
    implementation(antibytesCatalog.gradle.ksp.plugin)
    implementation(antibytesCatalog.gradle.agp)

    testImplementation(antibytesCatalog.testUtils.core)
    testImplementation(antibytesCatalog.kfixture)
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
        kotlin.srcDir("${layout.buildDirectory.get().asFile.absolutePath.trimEnd('/')}/generated/antibytes/main/kotlin")
    }
}

tasks.withType<KotlinTaskCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
}

val generateConfig by tasks.creating(AntiBytesMainConfigurationTask::class.java) {
    val version = Versioning.getInstance(
        project = project,
        configuration = publishingConfiguration.publishing.versioning
    ).versionName()
    packageName.set("tech.antibytes.gradle.kmock.config")
    stringFields.set(
        mapOf(
            "processor" to "tech.antibytes.kmock:kmock-processor:${version}"
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
