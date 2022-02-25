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
}

group = KMockConfiguration.group

ksp {
    arg("rootPackage", "tech.antibytes.kmock.example")
    arg("isKmp", true.toString())
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

                api(project(":kmock"))
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
                kotlin.srcDir("build/generated/ksp/android/androidTest")
                dependsOn(commonTest)

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
                dependsOn(commonMain)
                implementation(Dependency.multiplatform.kotlin.jdk8)
            }
        }
        val jvmTest by getting {
            kotlin.srcDir("build/generated/ksp/jvm/jvmTest")

            dependencies {
                dependsOn(commonTest)
                implementation(Dependency.multiplatform.test.jvm)
                implementation(Dependency.multiplatform.test.junit)
            }
        }

        val nativeMain by creating {
            dependencies {
                dependsOn(commonMain)
            }
        }
        val nativeTest by creating {
            dependencies {
                dependsOn(commonTest)
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

afterEvaluate {
    val copyToCommon by tasks.creating(Copy::class) {
        description = "Extract Common Sources"
        group = "Code Generation"
        dependsOn("kspTestKotlinJvm")
        mustRunAfter("kspTestKotlinJvm")

        this.from("${project.buildDir.absolutePath}/generated/ksp/jvm/jvmTest")
        this.into("${project.buildDir.absolutePath}/generated/ksp/common/commonTest")
        this.include("**/*.kt")
        this.exclude { details: FileTreeElement ->
            if (details.file.isFile) {
                val indicator = details.file.bufferedReader().readLine()
                indicator != "// COMMON SOURCE"
            } else {
                false
            }
        }
    }

    val cleanDuplicatesJvm by tasks.creating(DefaultTask::class) {
        description = "Removes Contradicting Sources"
        group = "Code Generation"

        dependsOn(copyToCommon)
        mustRunAfter(copyToCommon)
        mustRunAfter("kspTestKotlinJvm")

        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/jvm/jvmTest").toList()

            files.forEach { file ->
                if (!file.absolutePath.contains("commonTest")) {
                    val indicator = file.bufferedReader().readLine()

                    if (indicator == "// COMMON SOURCE") {
                        file.delete()
                    }
                }
            }
        }
    }

    val cleanDuplicatesAndroidDebug by tasks.creating(DefaultTask::class) {
        description = "Removes Contradicting Sources"
        group = "Code Generation"

        dependsOn(copyToCommon)
        mustRunAfter(copyToCommon)
        mustRunAfter("kspDebugUnitTestKotlinAndroid")

        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/android/androidDebugUnitTest").toList()

            files.forEach { file ->
                if (!file.absolutePath.contains("commonTest")) {
                    val indicator = file.bufferedReader().readLine()

                    if (indicator == "// COMMON SOURCE") {
                        file.delete()
                    }
                }
            }
        }
    }

    val cleanDuplicatesAndroidRelease by tasks.creating(DefaultTask::class) {
        description = "Removes Contradicting Sources"
        group = "Code Generation"

        dependsOn(copyToCommon)
        mustRunAfter(copyToCommon)
        mustRunAfter("kspReleaseUnitTestKotlinAndroid")

        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/android/androidReleaseUnitTest").toList()

            files.forEach { file ->
                if (!file.absolutePath.contains("commonTest")) {
                    val indicator = file.bufferedReader().readLine()

                    if (indicator == "// COMMON SOURCE") {
                        file.delete()
                    }
                }
            }
        }
    }

    val cleanDuplicatesJs by tasks.creating(DefaultTask::class) {
        description = "Removes Contradicting Sources"
        group = "Code Generation"

        dependsOn(copyToCommon)
        mustRunAfter(copyToCommon)
        mustRunAfter("kspTestKotlinJs")

        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/js/jsTest").toList()

            files.forEach { file ->
                if (!file.absolutePath.contains("commonTest")) {
                    val indicator = file.bufferedReader().readLine()

                    if (indicator == "// COMMON SOURCE") {
                        file.delete()
                    }
                }
            }
        }
    }

    val cleanDuplicatesIosX64 by tasks.creating(DefaultTask::class) {
        description = "Removes Contradicting Sources"
        group = "Code Generation"

        dependsOn(copyToCommon)
        mustRunAfter(copyToCommon)
        mustRunAfter("kspTestKotlinIosX64")

        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/iosX64/iosX64Test").toList()

            files.forEach { file ->
                if (!file.absolutePath.contains("commonTest")) {
                    val indicator = file.bufferedReader().readLine()

                    if (indicator == "// COMMON SOURCE") {
                        file.delete()
                    }
                }
            }
        }
    }

    val cleanDuplicatesLinuxX64 by tasks.creating(DefaultTask::class) {
        description = "Removes Contradicting Sources"
        group = "Code Generation"

        dependsOn(copyToCommon)
        mustRunAfter(copyToCommon)
        mustRunAfter("kspTestKotlinLinuxX64")

        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/linuxX64/linuxX64Test").toList()

            files.forEach { file ->
                if (!file.absolutePath.contains("commonTest")) {
                    val indicator = file.bufferedReader().readLine()

                    if (indicator == "// COMMON SOURCE") {
                        file.delete()
                    }
                }
            }
        }
    }

    tasks.getByName("compileTestKotlinJvm").dependsOn(cleanDuplicatesJvm, copyToCommon)
    tasks.getByName("compileTestKotlinJs").dependsOn(cleanDuplicatesJs, copyToCommon)
    tasks.getByName("compileTestKotlinJs").mustRunAfter(copyToCommon)
    tasks.getByName("compileTestKotlinIosX64").dependsOn(cleanDuplicatesIosX64, copyToCommon)
    tasks.getByName("compileTestKotlinIosX64").mustRunAfter(copyToCommon)
    tasks.getByName("compileTestKotlinLinuxX64").dependsOn(cleanDuplicatesLinuxX64, copyToCommon)
    tasks.getByName("compileDebugUnitTestKotlinAndroid").mustRunAfter(copyToCommon)
    tasks.getByName("compileDebugUnitTestKotlinAndroid").dependsOn(cleanDuplicatesAndroidDebug, copyToCommon)
    tasks.getByName("compileReleaseUnitTestKotlinAndroid").mustRunAfter(copyToCommon)
    tasks.getByName("compileReleaseUnitTestKotlinAndroid").dependsOn(cleanDuplicatesAndroidRelease, copyToCommon)

    tasks.getByName("kspTestKotlinJs").mustRunAfter(copyToCommon)
    tasks.getByName("kspTestKotlinLinuxX64").mustRunAfter(copyToCommon)
    tasks.getByName("kspTestKotlinIosX64").mustRunAfter(copyToCommon)
    tasks.getByName("kspDebugUnitTestKotlinAndroid").mustRunAfter(copyToCommon)
    tasks.getByName("kspReleaseUnitTestKotlinAndroid").mustRunAfter(copyToCommon)
}

dependencies {
    add("kspJvmTest", project(":kmock-processor"))
    add("kspAndroidTest", project(":kmock-processor"))
    add("kspJsTest", project(":kmock-processor"))
    add("kspLinuxX64Test", project(":kmock-processor"))
    add("kspIosX64Test", project(":kmock-processor"))
}
