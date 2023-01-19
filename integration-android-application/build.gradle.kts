/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.coverage.api.AndroidJacocoConfiguration

plugins {
    id(antibytesCatalog.plugins.kotlin.android.get().pluginId)

    alias(antibytesCatalog.plugins.gradle.antibytes.androidApplicationConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.coverage)

    // Processor
    alias(antibytesCatalog.plugins.gradle.ksp.plugin)

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
        kotlinCompilerExtensionVersion = antibytesCatalog.versions.android.compose.compiler.get()
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
    implementation(antibytesCatalog.jvm.kotlin.stdlib.jdk8)

    implementation(antibytesCatalog.android.ktx.core)
    implementation(antibytesCatalog.android.ktx.viewmodel.core)
    implementation(antibytesCatalog.android.ktx.viewmodel.compose)

    implementation(antibytesCatalog.android.appCompact.core)

    implementation(antibytesCatalog.android.compose.ui.core)
    implementation(antibytesCatalog.android.material.compose.core)
    implementation(antibytesCatalog.android.compose.foundation.core)

    testImplementation(libs.testUtils.core)
    testImplementation(libs.testUtils.coroutine)
    testImplementation(libs.kfixture)
    testImplementation(project(":kmock"))
    testImplementation(antibytesCatalog.android.test.junit.core)
    testImplementation(antibytesCatalog.jvm.test.junit.junit4)

    // Debug
    debugImplementation(antibytesCatalog.android.compose.ui.tooling.core)
    debugImplementation(antibytesCatalog.android.test.compose.manifest)

    androidTestImplementation(antibytesCatalog.android.test.junit.core)
    androidTestImplementation(antibytesCatalog.jvm.test.junit.junit4)
    androidTestImplementation(antibytesCatalog.android.test.compose.junit4)
    androidTestImplementation(antibytesCatalog.android.test.espresso.core)
    androidTestImplementation(antibytesCatalog.android.test.uiAutomator)

    androidTestImplementation(libs.testUtils.core)
    androidTestImplementation(libs.kfixture)
    androidTestImplementation(project(":kmock"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
