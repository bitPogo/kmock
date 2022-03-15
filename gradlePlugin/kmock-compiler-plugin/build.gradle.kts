/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.configuration.runtime.AntiBytesMainConfigurationTask
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

// To make it available as direct dependency
group = "tech.antibytes.gradle.kmock"
version = "1.0.0-SNAPSHOT"

buildscript {
    val antibytes = "ed855ae"
    val runtimeConfig = "tech.antibytes.gradle-plugins:antibytes-runtime-configuration:$antibytes"

    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven {
            setUrl("https://raw.github.com/bitPogo/maven-dev/main/dev")
            content {
                includeGroup("tech.antibytes.gradle-plugins")
            }
        }
        maven {
            setUrl("https://raw.github.com/bitPogo/maven-snapshots/main/snapshots")
            content {
                includeGroup("tech.antibytes.gradle-plugins")
            }
        }
        maven {
            setUrl("https://raw.github.com/bitPogo/maven-snapshots/main/snapshots")
            content {
                includeGroup("tech.antibytes.gradle-plugins")
            }
        }
    }
    dependencies {
        classpath(runtimeConfig)
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}


afterEvaluate {
    val generateConfig by tasks.creating(AntiBytesMainConfigurationTask::class.java) {
        packageName.set("tech.antibytes.gradle.kmock.config")
        stringFields.set(
            mapOf(
                "version" to project.version.toString(),
                "group" to project.group.toString(),
                "id" to "${project.group}.compiler-plugin",
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
    plugins.register("tech.antibytes.gradle.kmock.compiler-plugin") {
        id = "tech.antibytes.gradle.kmock.compiler-plugin"
        implementationClass = "tech.antibytes.gradle.kmock.compiler.KMockCompilerPlugin"
    }
}

val kotlinVersion = "1.6.10"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
}
