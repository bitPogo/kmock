# Quickstart
## Install
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

## Setup
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

## Generate a Mock
```kotlin
import tech.antibytes.kmock.MockCommon

@MockCommon(
   SampleInterface::class,
   OtherSampleInterface::class,
   ...
)
class SampleTestSet {
   ...
}
```

## Use your Mocks
```kotlin
...
@Test
fun sampleTest() {
   // arrange
   val someInstance: OtherSampleInterfaceMock = kmock()

   someInstance._fetch.returns = Any()
   someInstance._find.returns = "any"

   // act
   val someOtherInstance = SomeClass(someInstance)
   someOtherInstance.run()
   ...
}
...
```

## Verify/Assert
```kotlin
@Test
fun sampleTest() {
    // arrange
    val asserter = Asserter()
    val someInstance: SomeInterfaceMock = kmock(collector = asserter)
    val someInstanceOther: SomeInterfaceMock = kmock(collector = asserter)

   ...
    // assert
    asserter.verifOrder {
       someInstance._someProperty.wasGotten()
       someInstanceOther._someMethod.hasBeenCalled()
       someInstance._someMethod.hasBeenCalled()
   }
}
```
