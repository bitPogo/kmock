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
    id("tech.antibytes.gradle.coverage")

    // Processor
    id("com.google.devtools.ksp")

    id("kotlinx-atomicfu")
}

group = KMockConfiguration.group

ksp {
    arg("kmock_rootPackage", "tech.antibytes.kmock.example")
    arg("kmock_isKmp", true.toString())
    arg("kmock_kspDir", "${project.buildDir.absolutePath.trimEnd('/')}/generated/ksp")
    arg("kmock_spyOn_0", "tech.antibytes.kmock.example.contract.ExampleContract.SampleDomainObject")
    arg("kmock_alternativeProxyAccess", "true")
}

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

    linuxX64()

    sourceSets {
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
            kotlin.srcDir("build/generated/ksp/common/commonTest")

            dependencies {
                implementation(Dependency.multiplatform.test.common)
                implementation(Dependency.multiplatform.test.annotations)

                implementation(LocalDependency.antibytes.test.annotations)
                implementation(LocalDependency.antibytes.test.coroutine)
                implementation(LocalDependency.antibytes.test.fixture)

                implementation(Dependency.multiplatform.stately.freeze)

                implementation(Dependency.multiplatform.atomicFu.common)

                api(project(":kmock"))
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
        val androidAndroidTestRelease by getting {
            kotlin.srcDir("build/generated/ksp/android/androidReleaseAndroidTest")
        }
        val androidAndroidTestDebug by getting {
            kotlin.srcDir("build/generated/ksp/android/androidDebugAndroidTest")
        }
        val androidTestFixtures by getting
        val androidTestFixturesDebug by getting
        val androidTestFixturesRelease by getting
        val androidTest by getting {
            kotlin.srcDir("build/generated/ksp/android/androidTest")

            dependsOn(concurrentTest)
            dependsOn(androidTestFixtures)
            dependsOn(androidTestFixturesDebug)
            dependsOn(androidTestFixturesRelease)
            dependsOn(androidAndroidTestRelease)

            dependencies {
                implementation(Dependency.multiplatform.test.jvm)
                implementation(Dependency.multiplatform.test.junit)
                implementation(Dependency.android.test.robolectric)
            }
        }

        val androidAndroidTest by getting {
            dependsOn(concurrentTest)

            dependencies {
                implementation(Dependency.multiplatform.test.jvm)
                implementation(Dependency.android.test.junit)
                implementation(Dependency.android.test.junit5)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(Dependency.multiplatform.kotlin.js)
                implementation(Dependency.js.nodejs)
            }
        }
        val jsTest by getting {
            kotlin.srcDir("build/generated/ksp/js/jsTest")

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
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest")
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
    androidTestImplementation("org.junit.jupiter:junit-jupiter")
    add("kspAndroidAndroidTest", project(":kmock-processor"))
    add("kspJvmTest", project(":kmock-processor"))
    add("kspAndroidTest", project(":kmock-processor"))
    add("kspJsTest", project(":kmock-processor"))
    add("kspLinuxX64Test", project(":kmock-processor"))
    add("kspIosX64Test", project(":kmock-processor"))
    add("kspIosSimulatorArm64Test", project(":kmock-processor"))
}

val kspTasks = tasks.matching { task -> task.name.startsWith("kspTest") }

kspTasks.configureEach {
    doLast {
        File("${project.buildDir.absolutePath.trimEnd('/')}/generated/ksp").walkBottomUp().toList().forEach { file ->
            if (file.absolutePath.endsWith("KMockMultiInterfaceArtifacts.kt")) {
                file.delete()
            }
        }
    }
}

android {
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
