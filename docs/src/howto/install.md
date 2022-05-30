# Installation
KMock is capable to work with are variety of projects.
However it is currently distributed via [GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package), but will go soon to MavenCentral.

## Preparation
Before you can consume KMock you need to add the GitHub Package to you repositories.

=== "BuildScript"

    In your build.gradle.kts:
    ```kotlin
    buildScript {
        repositories {
            ...
            maven {
                url = uri("https://maven.pkg.github.com/bitPogo/kmock")
                credentials {
                    username = project.findProperty("gpr.user") ?: System.getenv("PACKAGE_REGISTRY_USERNAME")
                    password = project.findProperty("gpr.key") ?: System.getenv("PACKAGE_REGISTRY_DOWNLOAD_TOKEN")
                }
            }
        }
        dependencies {
            ...
            classpath("tech.antibytes.kmock:kmock-gradle:$KMockVersion")
        }
    }

    allprojects {
            ...
        repositories {
            ...
            maven {
                url = java.net.URI("https://maven.pkg.github.com/bitPogo/kmock")
                credentials {
                    username = project.findProperty("gpr.user")?.toString()
                        ?: System.getenv("GITHUB_USER")
                    password = project.findProperty("gpr.key")?.toString()
                        ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
    ```

=== "BuildSrc"

    In your buildSrc/build.gradle.kts:
    ```kotlin
    repositories {
        ...
        maven {
            url = uri("https://maven.pkg.github.com/bitPogo/kmock")
            credentials {
                username = project.findProperty("gpr.user") ?: System.getenv("PACKAGE_REGISTRY_USERNAME")
                password = project.findProperty("gpr.key") ?: System.getenv("PACKAGE_REGISTRY_DOWNLOAD_TOKEN")
            }
        }
    }

    dependencies {
        implementation("tech.antibytes.kmock:kmock-gradle:$KMockVersion")
    }
    ```

    In your root project build.gradle.kts:
    ```kotlin
    allprojects {
            ...
        repositories {
            ...
            maven {
                url = java.net.URI("https://maven.pkg.github.com/bitPogo/kmock")
                credentials {
                    username = project.findProperty("gpr.user")?.toString()
                        ?: System.getenv("GITHUB_USER")
                    password = project.findProperty("gpr.key")?.toString()
                        ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
    ```

In case you need to consume snapshots:
=== "BuildScript"

    In your build.gradle.kts:
    ```kotlin
    buildScript {
        repositories {
            ...
            maven {
                url = java.net.URI("https://raw.github.com/bitPogo/maven-snapshots/main/snapshots")

                content {
                    includeGroup("tech.antibytes.kmock")
                }
            }
        }
    }
    ...

    allprojects {
            ...
        repositories {
            ...
            maven {
                url = java.net.URI("https://raw.github.com/bitPogo/maven-snapshots/main/snapshots")
                content {
                    includeGroup("tech.antibytes.kmock")
                }
            }
        }
    }
    ```

=== "BuildSrc"

    In your buildSrc/build.gradle.kts:
    ```kotlin
    repositories {
        ...
        maven {
            url = java.net.URI("https://raw.github.com/bitPogo/maven-snapshots/main/snapshots")

            content {
                includeGroup("tech.antibytes.kmock")
            }
        }
    }
    ...
    ```
    In your root project build.grade.kts:
    ```kotlin
    allprojects {
            ...
        repositories {
            ...
            maven {
                url = java.net.URI("https://raw.github.com/bitPogo/maven-snapshots/main/snapshots")
                content {
                    includeGroup("tech.antibytes.kmock")
                }
            }
        }
    }
    ```

!!!warning
    Snapshots are not meant to be stable and will be removed after a release.
    Also if you need a stable snapshot due to a specific issue please ask for a pined snapshot.

## Add it to your project
=== "Android"
    ```kotlin
    plugins {
        ...
        id("tech.antibytes.kmock.kmock-gradle")
    }

    kmock {
        rootPackage = "my.root.package"
    }

    android {
        ...
        sourceSets {
            ...
            // Note: Do not use this while running debug and release together aka check/test/build
            /*getByName("test") {
                java.srcDirs(
                    "${project.buildDir.absolutePath.trimEnd('/')}/generated/ksp/debugUnitTest", // Just to make the IDE happy
                )
            }*/
        }
    }

    dependencies {
        ...
        testImplementation("tech.antibytes.kmock:kmock:$KMockVersion")
        ...
    }
    ```

=== "JVM"
    ```kotlin
    plugins {
        ...
        id("tech.antibytes.kmock.kmock-gradle")
    }

    kmock {
        rootPackage = "my.root.package"
    }

    dependencies {
        ...
        testImplementation("tech.antibytes.kmock:kmock:$KMockVersion")
        ...
    }
    ```

=== "JS"
    ```kotlin
    import tech.antibytes.gradle.kmock.KMockExtension
    ...

    plugins {
        id("org.jetbrains.kotlin.js")
        ...

        id("tech.antibytes.kmock.kmock-gradle") apply false
    }

    kotlin {
        ...
    }

    plugins.apply("tech.antibytes.kmock.kmock-gradle")

    project.extensions.configure<KMockExtension>(KMockExtension::class.java) {
        rootPackage = "my.root.package"
    }

    dependencies {
        ...
        testImplementation("tech.antibytes.kmock:kmock:$KMockVersion")
        ...
    }
    ```

=== "Kotlin Multiplatform"
    ```kotlin
    import tech.antibytes.gradle.kmock.KMockExtension
    ...

    plugins {
        ...

        id("tech.antibytes.kmock.kmock-gradle") apply false
    }

    kotlin {
        ...

        sourceSets {
            ...
            val commonTest by getting {
                dependencies {
                    ...
                    implementation("tech.antibytes.kmock:kmock:$KMockVersion")
                    ...
                }
            }
            ...
        }
        ...
    }

    plugins.apply("tech.antibytes.kmock.kmock-gradle")

    project.extensions.configure<KMockExtension>("kmock") {
        rootPackage = "my.root.package"
    }
    ```

!!!warning
    For KMP or KJs you must apply the plugin after your projects `kotlin` block has been set up as shown above.
    This is due to the fact that Kotlin brings its own system of source sets and [Kotlin Symbol Processing (KSP)](https://github.com/google/ksp)
    must be configured during the evaluation of your project.

!!!tip
    You may find a full examples, how to set up KMock properly, in the [Playground](https://github.com/bitPogo/kmock-playground).

## Newer KSP or Kotlin Versions
KMock eventually not use the latest version of Kotlin or KSP.
This is mainly done to enable keep it open to projects which cannot update immediately.
However since this might cause problems or noise you can force a new version of KSP:

=== "BuildScript"

    In your build.gradle.kts:
    ```kotlin
    buildscript {
        ...
        dependencies {
            classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
            classpath("tech.antibytes.kmock:kmock-gradle:0.1.1-SNAPSHOT") {
                exclude(
                    group = "com.google.devtools.ksp",
                    module = "symbol-processing-api"
                )
                exclude(
                    group = "com.google.devtools.ksp",
                    module = "com.google.devtools.ksp.gradle.plugin"
                )
            }
            classpath("com.google.devtools.ksp:symbol-processing-api:1.6.21-1.0.5")
            classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.6.21-1.0.5")
        }
    }
    ```
=== "BuildSrc"

    In your buildSrc/build.gradle.kts:
    ```kotlin
    dependencies {
        ...
        implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        implementation("tech.antibytes.kmock:kmock-gradle:0.1.1-SNAPSHOT") {
            exclude(
                group = "com.google.devtools.ksp",
                module = "symbol-processing-api"
            )
            exclude(
                group = "com.google.devtools.ksp",
                module = "com.google.devtools.ksp.gradle.plugin"
            )
        }
        implementation("com.google.devtools.ksp:symbol-processing-api:1.6.21-1.0.5")
        implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.6.21-1.0.5")
    }
    ```

!!!warning
    Do not replace the Kotlin Version in the Runtime part of KMock!
