/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.kmock.config.publishing.KMockConfiguration
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tech.antibytes.gradle.configuration.apple.ensureAppleDeviceCompatibility
import tech.antibytes.gradle.configuration.isIdea

plugins {
    alias(antibytesCatalog.plugins.gradle.antibytes.kmpConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.androidLibraryConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.coverage)

    // Processor
    alias(antibytesCatalog.plugins.gradle.ksp.plugin)
    id(antibytesCatalog.plugins.kotlinx.atomicfu.get().pluginId)

    id("tech.antibytes.kmock.kmock-gradle")
}

group = KMockConfiguration(project).group

kotlin {
    android {
        publishAllLibraryVariants()
        publishLibraryVariantsGroupedByFlavor = true
    }

    js(IR) {
        nodejs()
        browser()
    }

    jvm()

    ios()
    iosSimulatorArm64()
    ensureAppleDeviceCompatibility()

    linuxX64()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlin.RequiresOptIn")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(antibytesCatalog.common.kotlin.stdlib)
                implementation(antibytesCatalog.common.kotlinx.coroutines.core)
                implementation(antibytesCatalog.common.stately.isolate)
                implementation(antibytesCatalog.common.stately.concurrency)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.testUtils.annotations)
                implementation(libs.testUtils.core)
                implementation(libs.testUtils.coroutine)
                implementation(libs.kfixture)
                implementation(antibytesCatalog.common.test.kotlin.core)

                implementation(antibytesCatalog.common.stately.freeze)

                implementation(antibytesCatalog.common.kotlinx.atomicfu.core)

                implementation(project(":kmock"))
            }
        }

        val concurrentMain by creating {
            dependsOn(commonMain)
        }

        val concurrentTest by creating {
            dependsOn(commonTest)
        }

        val androidMain by getting {
            dependsOn(concurrentMain)
            dependencies {
                implementation(antibytesCatalog.jvm.kotlin.stdlib.jdk8)
            }
        }
        if (!isIdea()) {
            val androidAndroidTestRelease by getting
            val androidAndroidTestDebug by getting

            val androidAndroidTest by getting {
                dependsOn(androidAndroidTestRelease)
                dependsOn(androidAndroidTestDebug)
            }
            val androidTestFixturesDebug by getting
            val androidTestFixturesRelease by getting
            val androidTestFixtures by getting {
                dependsOn(androidTestFixturesDebug)
                dependsOn(androidTestFixturesRelease)
            }

            val androidTest by getting {
                dependsOn(androidTestFixtures)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(antibytesCatalog.jvm.test.junit.junit4)
                implementation(antibytesCatalog.jvm.test.kotlin.junit4)
                implementation(antibytesCatalog.android.test.robolectric)
            }
        }

        val androidAndroidTest by getting {
            dependsOn(concurrentTest)

            dependencies {
                implementation(antibytesCatalog.jvm.test.junit.junit4)
                implementation(antibytesCatalog.jvm.test.kotlin.junit4)
                implementation(antibytesCatalog.android.test.junit.core)
                implementation(antibytesCatalog.android.test.junit.ktx)
                implementation(antibytesCatalog.android.test.compose.junit4)
                implementation(antibytesCatalog.android.test.espresso.core)
                implementation(antibytesCatalog.android.test.uiAutomator)
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
            dependsOn(concurrentMain)
            dependencies {
                implementation(antibytesCatalog.jvm.kotlin.stdlib.jdk)
            }
        }
        val jvmTest by getting {
            dependsOn(concurrentTest)

            dependencies {
                implementation(antibytesCatalog.jvm.test.kotlin.core)
                implementation(antibytesCatalog.jvm.test.junit.junit4)
            }
        }

        val nativeMain by creating {
            dependsOn(concurrentMain)
        }
        val nativeTest by creating {
            dependsOn(concurrentTest)
        }

        val darwinMain by creating {
            dependsOn(nativeMain)
        }
        val darwinTest by creating {
            dependsOn(nativeTest)
        }

        val otherMain by creating {
            dependsOn(nativeMain)
        }
        val otherTest by creating {
            dependsOn(nativeTest)
        }

        val linuxX64Main by getting {
            dependsOn(otherMain)
        }
        val linuxX64Test by getting {
            dependsOn(otherTest)
        }

        val iosMain by getting {
            dependsOn(darwinMain)
        }
        val iosTest by getting {
            dependsOn(darwinTest)
        }

        val iosX64Test by getting {
            dependsOn(iosTest)
        }

        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }
}

kmock {
    rootPackage = "tech.antibytes.kmock.integration"
    spyOn = setOf(
        "tech.antibytes.kmock.integration.contract.ExampleContract.SampleDomainObject"
    )
    allowInterfaces = true
    preventResolvingOfAliases = setOf(
        "tech.antibytes.kmock.integration.contract.PlatformDecoder"
    )
    allowExperimentalProxyAccess = true
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
}

android {
    namespace = "tech.antibytes.kmock.integration"

    defaultConfig {
        minSdk = 30
    }

    sourceSets {
        val androidTest = getByName("androidTest")
        androidTest.java.setSrcDirs(setOf("src/androidAndroidTest/kotlin"))
        androidTest.res.setSrcDirs(setOf("src/androidAndroidTest/res"))
    }

    packagingOptions {
        resources.excludes.addAll(
            setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/LICENSE-notice.md",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0"
            )
        )
    }
}
