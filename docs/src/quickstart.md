# Quickstart
## Install
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

## Setup
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

   someInstance._fetch.returnValue = Any()
   someInstance._find returns "any"

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
