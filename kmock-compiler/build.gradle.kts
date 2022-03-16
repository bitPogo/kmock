/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.dependency.Dependency
import tech.antibytes.gradle.kmock.config.KMockCompilerPluginConfiguration
import tech.antibytes.gradle.kmock.dependency.Dependency as LocalDependency
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import tech.antibytes.gradle.configuration.runtime.AntiBytesMainConfigurationTask

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`

    id("tech.antibytes.gradle.publishing")
    id("tech.antibytes.gradle.coverage")
}

group = KMockCompilerPluginConfiguration.group

antiBytesPublishing {
    packageConfiguration = KMockCompilerPluginConfiguration.publishing.packageConfiguration
    repositoryConfiguration = KMockCompilerPluginConfiguration.publishing.repositories
    versioning = KMockCompilerPluginConfiguration.publishing.versioning
}

dependencies {
    implementation(LocalDependency.kotlin.gradle)
    implementation(LocalDependency.kotlin.compiler)

    testImplementation(LocalDependency.antibytes.test.core)
    testImplementation(LocalDependency.antibytes.test.fixture)
    testImplementation(platform(Dependency.jvm.test.junit))
    testImplementation(Dependency.jvm.test.jupiter)
    testImplementation(Dependency.jvm.test.mockk)
    testImplementation(Dependency.jvm.test.kotlin)
    testImplementation(LocalDependency.compilerTest.core)
    testImplementation(project(":kmock"))

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
        packageName.set("tech.antibytes.gradle.kmock.compiler.config")
        stringFields.set(
            mapOf(
                "version" to project.version as String,
                "group" to KMockCompilerPluginConfiguration.group,
                "artifactId" to KMockCompilerPluginConfiguration.id,
                "pluginId" to KMockCompilerPluginConfiguration.pluginId,
            )
        )
    }

    tasks.withType(KotlinCompile::class.java) {
        this.dependsOn(generateConfig)
        this.mustRunAfter(generateConfig)
    }
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("${buildDir.absolutePath.trimEnd('/')}/generated/antibytes/main")
    }
}

gradlePlugin {
    plugins.register(KMockCompilerPluginConfiguration.pluginId) {
        group = KMockCompilerPluginConfiguration.group
        id = KMockCompilerPluginConfiguration.pluginId
        implementationClass = "tech.antibytes.gradle.kmock.compiler.KMockCompilerPlugin"
        displayName = "${id}.gradle.plugin"
        description = "KMock Compiler Plugin"
        version = "0.1.0"
    }
}
