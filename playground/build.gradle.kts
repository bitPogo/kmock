/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import tech.antibytes.gradle.kmock.config.publishing.KMockConfiguration
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tech.antibytes.gradle.configuration.apple.ensureAppleDeviceCompatibility
import tech.antibytes.gradle.configuration.sourcesets.iosx

plugins {
    alias(antibytesCatalog.plugins.gradle.antibytes.kmpConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.androidLibraryConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.coverage)

    // Processor
    alias(antibytesCatalog.plugins.gradle.ksp.plugin)

    id(antibytesCatalog.plugins.kotlinx.atomicfu.get().pluginId)
}

atomicfu {
    dependenciesVersion = antibytesCatalog.versions.kotlinx.atomicfu.core.get()
    transformJvm = false
    transformJs = false
}

group = KMockConfiguration(project).group

ksp {
    arg("kmock_rootPackage", "tech.antibytes.kmock.example")
    arg("kmock_isKmp", "true")
    arg("kmock_kspDir", "${project.layout.buildDirectory.get().asFile.absolutePath.trimEnd('/')}/generated/ksp")
    arg("kmock_spyOn_0", "tech.antibytes.kmock.example.contract.ExampleContract.SampleDomainObject")
    arg("kmock_alternativeProxyAccess", "true")
    arg("kmock_allowInterfaces", "true")
    arg("kmock_preventAliasResolving_0", "tech.antibytes.kmock.example.contract.PlatformDecoder")
    arg("kmock_freeze", "false")
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
        publishLibraryVariantsGroupedByFlavor = true
    }

    js(IR) {
        nodejs()
        browser()
    }

    jvm()

    iosx()
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
            kotlin.srcDir("build/generated/ksp/common/commonTest")

            dependencies {
                implementation(antibytesCatalog.testUtils.annotations)
                implementation(antibytesCatalog.testUtils.core)
                implementation(antibytesCatalog.testUtils.coroutine)
                implementation(antibytesCatalog.kfixture)
                implementation(antibytesCatalog.common.test.kotlin.core)

                implementation(antibytesCatalog.common.stately.freeze)

                implementation(antibytesCatalog.common.kotlinx.atomicfu.core)

                implementation(projects.kmock)
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

        val androidUnitTest by getting {
            dependsOn(concurrentTest)
            kotlin.srcDir("build/generated/ksp/android/androidUnitTest")

            dependencies {
                implementation(antibytesCatalog.jvm.test.junit.junit4)
                implementation(antibytesCatalog.jvm.test.kotlin.junit4)
                implementation(antibytesCatalog.android.test.robolectric)
            }
        }

        val androidInstrumentedTest by getting {
            dependsOn(concurrentTest)
            kotlin.srcDir("build/generated/ksp/android/androidInstrumentedTest")

            dependencies {
                implementation(antibytesCatalog.android.test.junit.core)
                implementation(antibytesCatalog.android.test.junit.ktx)
                implementation(antibytesCatalog.android.test.compose.junit4)
                implementation(antibytesCatalog.android.test.espresso.core)
                implementation(antibytesCatalog.android.test.uiAutomator)
                implementation(antibytesCatalog.jvm.test.junit.junit4)
                implementation(antibytesCatalog.jvm.test.kotlin.junit4)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(antibytesCatalog.js.kotlin.stdlib)
                implementation(antibytesCatalog.js.kotlinx.nodeJs)
            }
        }
        val jsTest by getting {
            kotlin.srcDir("build/generated/ksp/js/jsTest")

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
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest")
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
            kotlin.srcDir("src-gen/generated/ksp/linuxX64/linuxX64Test")
            dependsOn(otherTest)
        }

        val iosMain by getting {
            dependsOn(darwinMain)
        }
        val iosTest by getting {
            dependsOn(darwinTest)
        }

        val iosX64Test by getting {
            kotlin.srcDir("build/generated/ksp/iosX64/iosX64Test")
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

dependencies {
    add("kspAndroidAndroidTest", projects.kmockProcessor)
    add("kspJvmTest", projects.kmockProcessor)
    add("kspAndroidTest", projects.kmockProcessor)
    add("kspJsTest", projects.kmockProcessor)
    add("kspLinuxX64Test", projects.kmockProcessor)
    add("kspIosX64Test", projects.kmockProcessor)
    add("kspIosSimulatorArm64Test", projects.kmockProcessor)
}

val kspTasks = tasks.matching { task -> task.name.startsWith("ksp") }

kspTasks.configureEach {
    doLast {
        if (!name.startsWith("android")) {
            val platform = name.substringAfter("kspTestKotlin")

            project.file("${project.project.layout.buildDirectory.get().asFile.absolutePath.trimEnd('/')}/generated/ksp/${platform}Test")
                .walkBottomUp()
                .toList()
                .forEach { file ->
                    if (file.absolutePath.endsWith("KMockMultiInterfaceArtifacts.kt")) {
                        file.delete()
                    }
                }
        }
    }
}


tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
}

android {
    namespace = "tech.antibytes.kmock.example"

    defaultConfig {
        minSdk = 30
    }

    packaging {
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
