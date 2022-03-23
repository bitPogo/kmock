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
    arg("kmock_enableSpies", true.toString())
    arg("kmock_kspDir", "${project.buildDir.absolutePath.trimEnd('/')}/generated/ksp")
    arg("kmock_buildIn_0", "tech.antibytes.kmock.example.contract.ExampleContract.SampleDomainObject")
}

kotlin {
    android()

    js(IR) {
        nodejs()
        browser()
    }

    jvm()

    ios()

    linuxX64()

    sourceSets {
        removeAll { sourceSet ->
            setOf(
                "androidAndroidTestRelease",
                "androidTestFixtures",
                "androidTestFixturesDebug",
                "androidTestFixturesRelease",
            ).contains(sourceSet.name)
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
            dependencies {
                dependsOn(commonMain)
            }
        }

        val concurrentTest by creating {
            dependencies {
                dependsOn(commonTest)
            }
        }

        val androidMain by getting {
            dependencies {
                dependsOn(concurrentMain)
                implementation(Dependency.multiplatform.kotlin.android)
            }
        }
        val androidTest by getting {
            dependencies {
                kotlin.srcDir("build/generated/ksp/android/androidTest")
                dependsOn(concurrentTest)

                implementation(Dependency.multiplatform.test.jvm)
                implementation(Dependency.multiplatform.test.junit)
                implementation(Dependency.android.test.robolectric)
            }
        }

        val jsMain by getting {
            dependencies {
                dependsOn(commonMain)
                implementation(Dependency.multiplatform.kotlin.js)
                implementation(Dependency.js.nodejs)
            }
        }
        val jsTest by getting {
            kotlin.srcDir("build/generated/ksp/js/jsTest")

            dependencies {
                dependsOn(commonTest)

                implementation(Dependency.multiplatform.test.js)
            }
        }

        val jvmMain by getting {
            dependencies {
                dependsOn(concurrentMain)
                implementation(Dependency.multiplatform.kotlin.jdk8)
            }
        }
        val jvmTest by getting {
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest")

            dependencies {
                dependsOn(concurrentTest)
                implementation(Dependency.multiplatform.test.jvm)
                implementation(Dependency.multiplatform.test.junit)
            }
        }

        val nativeMain by creating {
            dependencies {
                dependsOn(concurrentMain)
            }
        }
        val nativeTest by creating {
            dependencies {
                dependsOn(concurrentTest)
            }
        }

        val darwinMain by creating {
            dependencies {
                dependsOn(nativeMain)
            }
        }
        val darwinTest by creating {
            dependencies {
                dependsOn(nativeTest)
            }
        }

        val otherMain by creating {
            dependencies {
                dependsOn(nativeMain)
            }
        }
        val otherTest by creating {
            dependencies {
                dependsOn(nativeTest)
            }
        }

        val linuxX64Main by getting {
            dependencies {
                dependsOn(otherMain)
            }
        }
        val linuxX64Test by getting {
            kotlin.srcDir("src-gen/generated/ksp/linuxX64/linuxX64Test")
            dependencies {
                dependsOn(otherTest)
            }
        }

        val iosMain by getting {
            dependencies {
                dependsOn(darwinMain)
            }
        }
        val iosTest by getting {
            dependencies {
                dependsOn(darwinTest)
            }
        }

        val iosX64Test by getting {
            kotlin.srcDir("build/generated/ksp/iosX64/iosX64Test")
            dependencies {
                dependsOn(iosTest)
            }
        }
    }
}

dependencies {
    add("kspJvmTest", project(":kmock-processor"))
    add("kspAndroidTest", project(":kmock-processor"))
    add("kspJsTest", project(":kmock-processor"))
    add("kspLinuxX64Test", project(":kmock-processor"))
    add("kspIosX64Test", project(":kmock-processor"))
}
