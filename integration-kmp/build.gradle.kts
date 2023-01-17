/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.dependency.Dependency
import tech.antibytes.gradle.kmock.config.publishing.KMockConfiguration
import tech.antibytes.gradle.kmock.dependency.Dependency as LocalDependency
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tech.antibytes.gradle.configuration.apple.ensureAppleDeviceCompatibility
import tech.antibytes.gradle.configuration.isIdea

plugins {
    alias(antibytesCatalog.plugins.gradle.antibytes.kmpConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.androidLibraryConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.coverage)

    // Processor
    alias(antibytesCatalog.plugins.gradle.ksp)
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
                implementation(Dependency.multiplatform.kotlin.common)
                implementation(Dependency.multiplatform.coroutines.common)
                implementation(Dependency.multiplatform.stately.isolate)
                implementation(Dependency.multiplatform.stately.concurrency)

                implementation(LocalDependency.antibytes.test.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Dependency.multiplatform.test.common)
                implementation(Dependency.multiplatform.test.annotations)

                implementation(LocalDependency.antibytes.test.annotations)
                implementation(LocalDependency.antibytes.test.coroutine)
                implementation(LocalDependency.antibytes.test.fixture)

                implementation(Dependency.multiplatform.stately.freeze)

                implementation(Dependency.multiplatform.atomicFu.common)

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
                implementation(Dependency.multiplatform.kotlin.android)
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
                implementation(Dependency.multiplatform.test.jvm)
                implementation(Dependency.multiplatform.test.junit)
                implementation(Dependency.android.test.robolectric)
            }
        }

        val androidAndroidTest by getting {
            dependsOn(concurrentTest)

            dependencies {
                implementation(Dependency.jvm.test.junit)
                implementation(Dependency.android.test.junit)
                implementation(Dependency.android.test.composeJunit4)
                implementation(Dependency.android.test.espressoCore)
                implementation(Dependency.android.test.uiAutomator)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(Dependency.multiplatform.kotlin.js)
                implementation(Dependency.js.nodejs)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(Dependency.multiplatform.test.js)
            }
        }

        val jvmMain by getting {
            dependsOn(concurrentMain)
            dependencies {
                implementation(Dependency.multiplatform.kotlin.jdk8)
            }
        }
        val jvmTest by getting {
            dependsOn(concurrentTest)

            dependencies {
                implementation(Dependency.multiplatform.test.jvm)
                implementation(Dependency.multiplatform.test.junit)
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
