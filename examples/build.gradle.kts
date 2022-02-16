/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import com.google.devtools.ksp.gradle.KspTask
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
                kotlin.srcDir("build/generated/ksp/commonTest")

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
            dependencies {
                dependsOn(commonTest)

                kotlin.srcDir("build/generated/ksp/jsTest")
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
            dependencies {
                dependsOn(commonTest)
                kotlin.srcDir("build/generated/ksp/jvmTest")
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
            dependencies {
                dependsOn(otherTest)
                kotlin.srcDir("src-gen/generated/ksp/linuxX64Test")
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
            dependencies {
                dependsOn(iosTest)

                kotlin.srcDir("build/generated/ksp/iosX64Test")
            }
        }
    }
}

afterEvaluate {
    tasks.findByName("cleanJvmTest")?.doLast {
        delete("${project.buildDir.absolutePath}/generated/ksp/jvmTest")
    }
    tasks.findByName("cleanJsNodeTestClean")?.doLast {
        delete("${project.buildDir.absolutePath}/generated/ksp/hsNodeTest")
    }
    tasks.findByName("cleanJsBrowserTestClean")?.doLast {
        delete("${project.buildDir.absolutePath}/generated/ksp/jsBrowserTest")
    }
    tasks.findByName("cleanLinuxX64TestClean")?.doLast {
        delete("${project.buildDir.absolutePath}/generated/ksp/linuxX64Test")
    }
    tasks.findByName("cleanIosX64TestClean")?.doLast {
        delete("${project.buildDir.absolutePath}/generated/ksp/iosX64Test")
    }

    val moveToCommon by tasks.creating(Copy::class) {
        description = "Extract Common Sources"
        group = "Code Generation"
        dependsOn("kspTestKotlinJvm")
        mustRunAfter("kspTestKotlinJvm")

        this.from("${project.buildDir.absolutePath}/generated/ksp/jvmTest")
        this.into("${project.buildDir.absolutePath}/generated/ksp/commonTest")
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
        mustRunAfter(moveToCommon)
        mustRunAfter("kspTestKotlinJvm")

        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/jvmTest").toList()

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
        mustRunAfter("kspTestKotlinJs")

        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/jsTest").toList()

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
        mustRunAfter("kspTestKotlinIosX64")

        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/iosX64Test").toList()

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
        mustRunAfter("kspTestKotlinLinuxX64")

        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/linuxX64Test").toList()

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

    tasks.getByName("kspDebugUnitTestKotlinAndroid") {
        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/androidDebugUnitTest").toList()

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

    tasks.getByName("kspReleaseUnitTestKotlinAndroid") {
        doLast {
            val files = project.fileTree("${project.buildDir.absolutePath}/generated/ksp/androidReleaseUnitTest").toList()

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

    tasks.getByName("compileTestKotlinJvm").dependsOn(cleanDuplicatesJvm, moveToCommon)
    tasks.getByName("compileTestKotlinJs").dependsOn(cleanDuplicatesJs, moveToCommon)
    tasks.getByName("compileTestKotlinJs").mustRunAfter(moveToCommon)
    tasks.getByName("compileTestKotlinIosX64").dependsOn(cleanDuplicatesIosX64, moveToCommon)
    tasks.getByName("compileTestKotlinIosX64").mustRunAfter(moveToCommon)
    tasks.getByName("compileTestKotlinLinuxX64").dependsOn(cleanDuplicatesLinuxX64, moveToCommon)
    tasks.getByName("compileDebugUnitTestKotlinAndroid").mustRunAfter(moveToCommon)
    tasks.getByName("compileDebugUnitTestKotlinAndroid").dependsOn(moveToCommon)
    tasks.getByName("compileReleaseKotlinAndroid").mustRunAfter(moveToCommon)
    tasks.getByName("compileReleaseKotlinAndroid").dependsOn(moveToCommon)
}

dependencies {
    add("kspJvmTest", project(":kmock-processor"))
    add("kspAndroidTest", project(":kmock-processor"))
    add("kspJsTest", project(":kmock-processor"))
    add("kspLinuxX64Test", project(":kmock-processor"))
    add("kspIosX64Test", project(":kmock-processor"))
}
