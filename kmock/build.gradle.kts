/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.dependency.Dependency
import tech.antibytes.gradle.kmock.config.KMockConfiguration
import tech.antibytes.gradle.kmock.dependency.Dependency as LocalDependency

plugins {
    id("org.jetbrains.kotlin.multiplatform")

    // Android
    id("com.android.library")

    id("tech.antibytes.gradle.configuration")
    id("tech.antibytes.gradle.publishing")
    id("tech.antibytes.gradle.coverage")
}

group = KMockConfiguration.group

antiBytesPublishing {
    packageConfiguration = KMockConfiguration.publishing.packageConfiguration
    repositoryConfiguration = KMockConfiguration.publishing.repositories
    versioning = KMockConfiguration.publishing.versioning
}

kotlin {
    android()

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependency.multiplatform.kotlin.common)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Dependency.multiplatform.test.common)
                implementation(Dependency.multiplatform.test.annotations)

                implementation(LocalDependency.antibytes.test.annotations)
                implementation(LocalDependency.antibytes.test.core)
                implementation(LocalDependency.antibytes.test.coroutine)
                implementation(LocalDependency.antibytes.test.fixture)
            }
        }

        val androidMain by getting {
            dependencies {
                dependsOn(commonMain)
                implementation(Dependency.multiplatform.kotlin.android)
            }
        }
        val androidTest by getting {
            dependencies {
                dependsOn(commonTest)

                implementation(Dependency.multiplatform.test.jvm)
                implementation(Dependency.multiplatform.test.junit)
                implementation(Dependency.android.test.robolectric)
            }
        }

        val jvmMain by getting {
            dependencies {
                dependsOn(commonMain)
                implementation(Dependency.multiplatform.kotlin.jdk8)
            }
        }
        val jvmTest by getting {
            dependencies {
                dependsOn(commonTest)

                implementation(Dependency.multiplatform.test.jvm)
                implementation(Dependency.multiplatform.test.junit)
            }
        }
    }
}
