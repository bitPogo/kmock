/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.kmock.config.publishing.KMockConfiguration
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import tech.antibytes.gradle.coverage.api.JvmJacocoConfiguration
import tech.antibytes.gradle.coverage.api.AndroidJacocoConfiguration
import tech.antibytes.gradle.coverage.api.JacocoVerificationRule
import tech.antibytes.gradle.coverage.CoverageApiContract.JacocoCounter
import tech.antibytes.gradle.coverage.CoverageApiContract.JacocoMeasurement
import tech.antibytes.gradle.configuration.apple.ensureAppleDeviceCompatibility
import tech.antibytes.gradle.configuration.sourcesets.nativeCoroutine

plugins {
    alias(antibytesCatalog.plugins.gradle.antibytes.kmpConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.androidLibraryConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.publishing)
    alias(antibytesCatalog.plugins.gradle.antibytes.coverage)
    alias(antibytesCatalog.plugins.gradle.antibytes.dokkaConfiguration)
    id(antibytesCatalog.plugins.kotlinx.atomicfu.get().pluginId)
}

val publishingConfiguration = KMockConfiguration(project)
group = publishingConfiguration.group

antibytesPublishing {
    packaging.set(publishingConfiguration.publishing.packageConfiguration)
    repositories.set(publishingConfiguration.publishing.repositories)
    versioning.set(publishingConfiguration.publishing.versioning)
    documentation.set(publishingConfiguration.publishing.documentation)
    signing.set(publishingConfiguration.publishing.signing)
}

antibytesCoverage {
    val branchCoverage = JacocoVerificationRule(
        counter = JacocoCounter.BRANCH,
        measurement = JacocoMeasurement.COVERED_RATIO,
        minimum = BigDecimal(0.90)
    )

    val instructionCoverage = JacocoVerificationRule(
        counter = JacocoCounter.INSTRUCTION,
        measurement = JacocoMeasurement.COVERED_RATIO,
        minimum = BigDecimal(0.95)
    )

    val jvmCoverage = JvmJacocoConfiguration.createJvmKmpConfiguration(
        project,
        classFilter = setOf("**/Hint*"),
        verificationRules = setOf(
            branchCoverage,
            instructionCoverage
        )
    )

    val androidCoverage = AndroidJacocoConfiguration.createAndroidLibraryKmpConfiguration(
        project,
        classFilter = setOf("**/Hint*"),
        verificationRules = setOf(
            branchCoverage,
            instructionCoverage
        )
    )

    configurations.put("jvm", jvmCoverage)
    configurations.put("android", androidCoverage)
}

android {
    namespace = "tech.antibytes.kmock"

    defaultConfig {
        minSdk = antibytesCatalog.versions.minSdk.get().toInt()
    }
}

atomicfu {
    dependenciesVersion = antibytesCatalog.versions.kotlinx.atomicfu.core.get()
    transformJvm = false
    transformJs = false
}

kotlin {
    androidTarget()

    js(IR) {
        nodejs()
        browser()
    }

    jvm()

    nativeCoroutine()
    ensureAppleDeviceCompatibility()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(antibytesCatalog.common.kotlin.stdlib)
                implementation(antibytesCatalog.common.kotlinx.atomicfu.core)
                implementation(antibytesCatalog.common.stately.collections)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(antibytesCatalog.common.test.kotlin.core)
                implementation(antibytesCatalog.testUtils.core)
                implementation(antibytesCatalog.testUtils.annotations)
                implementation(antibytesCatalog.testUtils.coroutine)
                implementation(antibytesCatalog.kfixture)
                implementation(antibytesCatalog.common.test.kotlinx.coroutines)
                implementation(antibytesCatalog.common.stately.collections)
                implementation(antibytesCatalog.common.stately.concurrency)
                implementation(antibytesCatalog.common.stately.freeze)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(antibytesCatalog.jvm.kotlin.stdlib.jdk8)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(antibytesCatalog.android.test.junit.core)
                implementation(antibytesCatalog.jvm.test.kotlin.junit4)
                implementation(antibytesCatalog.android.test.robolectric)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(antibytesCatalog.js.kotlin.stdlib)
                implementation(antibytesCatalog.js.kotlinx.nodeJs)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(antibytesCatalog.js.test.kotlin.core)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(antibytesCatalog.jvm.kotlin.stdlib.jdk)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(antibytesCatalog.jvm.test.kotlin.core)
                implementation(antibytesCatalog.jvm.test.junit.junit4)
            }
        }

        val nativeMain by getting

        val nativeTest by getting
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
}
