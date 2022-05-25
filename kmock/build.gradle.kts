/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import tech.antibytes.gradle.dependency.Dependency
import tech.antibytes.gradle.kmock.config.KMockConfiguration
import tech.antibytes.gradle.kmock.dependency.Dependency as LocalDependency
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    id("org.jetbrains.kotlin.multiplatform")

    // Android
    id("com.android.library")

    id("tech.antibytes.gradle.configuration")
    id("tech.antibytes.gradle.publishing")
    id("tech.antibytes.gradle.coverage")

    id("kotlinx-atomicfu")

    id("org.jetbrains.dokka") version "1.6.10"
}

group = KMockConfiguration.group

antiBytesPublishing {
    packageConfiguration = KMockConfiguration.publishing.packageConfiguration
    repositoryConfiguration = KMockConfiguration.publishing.repositories
    versioning = KMockConfiguration.publishing.versioning
}

android {
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
}

kotlin {
    explicitApi()

    android()

    js(IR) {
        nodejs()
        browser()
    }

    jvm()

    ios()
    iosSimulatorArm64()

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
                implementation(Dependency.multiplatform.atomicFu.common)
                implementation(Dependency.multiplatform.stately.collections)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Dependency.multiplatform.test.common)
                implementation(Dependency.multiplatform.test.annotations)
                implementation(Dependency.multiplatform.coroutines.common)

                implementation(Dependency.multiplatform.stately.concurrency)
                implementation(Dependency.multiplatform.stately.freeze)

                implementation(LocalDependency.antibytes.test.core)
                implementation(LocalDependency.antibytes.test.annotations)
                implementation(LocalDependency.antibytes.test.coroutine)
                implementation(LocalDependency.antibytes.test.fixture)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Dependency.multiplatform.kotlin.android)
            }
        }
        val androidAndroidTestRelease by getting
        val androidTestFixtures by getting
        val androidTestFixturesDebug by getting
        val androidTestFixturesRelease by getting
        val androidTest by getting {
            dependsOn(androidAndroidTestRelease)
            dependsOn(androidTestFixtures)
            dependsOn(androidTestFixturesDebug)
            dependsOn(androidTestFixturesRelease)

            dependencies {
                implementation(Dependency.multiplatform.test.jvm)
                implementation(Dependency.multiplatform.test.junit)
                implementation(Dependency.android.test.robolectric)
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
            dependencies {
                implementation(Dependency.multiplatform.kotlin.jdk8)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(Dependency.multiplatform.test.jvm)
                implementation(Dependency.multiplatform.test.junit)
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        val nativeTest by creating {
            dependsOn(commonTest)
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

        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}


tasks.withType<DokkaTask>(DokkaTask::class.java).configureEach {
    outputDirectory.set(buildDir.resolve("dokka"))

    moduleName.set("KMock")
    offlineMode.set(false)
    suppressObviousFunctions.set(true)

    dokkaSourceSets {
        configureEach {
            reportUndocumented.set(true)
            skipEmptyPackages.set(true)
            jdkVersion.set(8)
            noStdlibLink.set(false)
            noJdkLink.set(false)
            noAndroidSdkLink.set(false)

            sourceLink {
                localDirectory.set(rootDir.resolve("docs/api"))

                remoteUrl.set(
                    URL("https://github.com/bitPogo/kmock/blob/main/docs/api")
                )

                remoteLineSuffix.set("#L")
            }
        }
    }
}
