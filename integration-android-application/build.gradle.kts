/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.dependency.Dependency
import tech.antibytes.gradle.kmock.dependency.Dependency as LocalDependency
import tech.antibytes.gradle.dependency.Version
import tech.antibytes.gradle.coverage.api.AndroidJacocoConfiguration

plugins {
    id("org.jetbrains.kotlin.android")

    alias(antibytesCatalog.plugins.gradle.antibytes.androidApplicationConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.coverage)

    // Processor
    alias(antibytesCatalog.plugins.gradle.ksp)

    id(antibytesCatalog.plugins.kotlinx.atomicfu.get().pluginId)

    id("tech.antibytes.kmock.kmock-gradle")
}

kmock {
    rootPackage = "tech.antibytes.kmock.integration"
}

antibytesCoverage {
    val excludes = setOf("**/*")
    val androidCoverage = AndroidJacocoConfiguration.createAndroidLibraryKmpConfiguration(
        project,
        classFilter = excludes,
    )
    configurations.put("android", androidCoverage)
}

android {
    namespace = "tech.antibytes.kmock.integration"

    defaultConfig {
        applicationId = "tech.antibytes.kmock.integration.app"
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Version.android.compose.compiler
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        debug {
            isMinifyEnabled = false
            matchingFallbacks.add("release")
        }
    }
}

dependencies {
    implementation(Dependency.multiplatform.kotlin.jdk8)

    implementation(Dependency.android.ktx.core)
    implementation(Dependency.android.ktx.viewmodel)
    implementation(Dependency.android.ktx.viewmodelCoroutine)
    implementation(Dependency.android.appCompact.core)

    implementation(Dependency.android.compose.ui)
    implementation(Dependency.android.compose.material)
    implementation(Dependency.android.compose.viewmodel)
    implementation(Dependency.android.compose.foundation)

    testImplementation(LocalDependency.antibytes.test.android.core)
    testImplementation(LocalDependency.antibytes.test.android.coroutine)
    testImplementation(LocalDependency.antibytes.test.android.fixture)
    testImplementation(project(":kmock"))
    testImplementation(Dependency.android.test.junit)
    testImplementation(Dependency.android.test.junit4)

    // Debug
    debugImplementation(Dependency.android.compose.uiTooling)
    debugImplementation(Dependency.android.compose.uiManifest)

    androidTestImplementation(Dependency.android.test.junit)
    androidTestImplementation(Dependency.android.test.junit4)
    androidTestImplementation(Dependency.android.test.composeJunit4)
    androidTestImplementation(Dependency.android.test.espressoCore)
    androidTestImplementation(Dependency.android.test.uiAutomator)

    androidTestImplementation(LocalDependency.antibytes.test.android.core)
    androidTestImplementation(LocalDependency.antibytes.test.android.fixture)
    androidTestImplementation(project(":kmock"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
