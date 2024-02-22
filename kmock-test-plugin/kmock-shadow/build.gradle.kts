import tech.antibytes.gradle.configuration.sourcesets.nativeCoroutine
import tech.antibytes.gradle.configuration.apple.ensureAppleDeviceCompatibility

plugins {
    alias(antibytesCatalog.plugins.gradle.antibytes.kmpConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.androidLibraryConfiguration)
    alias(antibytesCatalog.plugins.gradle.antibytes.dokkaConfiguration)
    id(antibytesCatalog.plugins.kotlinx.atomicfu.get().pluginId)
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

        val androidMain by getting {
            dependencies {
                implementation(antibytesCatalog.jvm.kotlin.stdlib.jdk8)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(antibytesCatalog.js.kotlin.stdlib)
                implementation(antibytesCatalog.js.kotlinx.nodeJs)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(antibytesCatalog.jvm.kotlin.stdlib.jdk)
            }
        }

        val nativeMain by getting

        val nativeTest by getting
    }
}
