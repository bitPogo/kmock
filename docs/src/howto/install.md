# Installation
KMock is capable to work with a variety of projects.

## Preparation
=== "BuildScript"

    In your build.gradle.kts:
    ```kotlin
    buildScript {
        repositories {
            ...
            mavenCentral()
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
            mavenCentral()
        }
    }
    ```

=== "BuildSrc"

    In your buildSrc/build.gradle.kts:
    ```kotlin
    repositories {
        ...
        mavenCentral()
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
            mavenCentral()
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

        id("tech.antibytes.kmock.kmock-gradle")
    }

    kotlin {
        ...
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

!!!note
    While the Gradle Plugin will add for `release` and `debug` the source folders, it will not configure additional build types or flavours.
    If make use of that feature you need to add the generated files yourself.
    You may find all generated sources in the project's build folder under `generated/ksp` and the documentation on build-variants can be found [here](https://developer.android.com/studio/build/build-variants).

=== "Kotlin Multiplatform"
    ```kotlin
    import tech.antibytes.gradle.kmock.KMockExtension
    ...

    plugins {
        ...

        id("tech.antibytes.kmock.kmock-gradle")
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

    kmock {
        rootPackage = "my.root.package"
    }
    ```

!!!warning
    For KMP or KJs it is mandatory to configure KMock after your projects `kotlin` block has been set up as shown above.
    This is due to the fact that Kotlin brings its own system of source sets and [Kotlin Symbol Processing (KSP)](https://github.com/google/ksp)
    must be configured during the evaluation of your project.

!!!tip
    You may find a full examples, how to set up KMock properly, in the [Playground](https://github.com/bitPogo/kmock-playground).

!!!important
    Since Kotlin 1.7.0 KmpTest are running always in parallel, which can cause Mocks not to be generated properly.
    To counter this KMock will chain the test in a arbitrary order which effectively disables the parallel execution.
    If you want to deactivate this behaviour you may add `kmock.noParallelTests` in your gradle.properties to false.

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
            classpath("tech.antibytes.kmock:kmock-gradle:$KMockVersion") {
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
        implementation("tech.antibytes.kmock:kmock-gradle:$KMockVersion") {
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
