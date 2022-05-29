# Declaring a Mock
To declare Mocks you have to use KMock's annotations or those you have declared in the KMock's Gradle Extension.
You can declare Mocks in a central place (per platform) or per test suite.
However declaring them in a central place is discouraged since it violates the [DAMP unit tests](https://enterprisecraftsmanship.com/posts/dry-damp-unit-tests/) principle.

## Build-In Annotations
KMock offers you 6 annotations which are considered as stable to declare a Mock:

* Mock/MultiMock for Platform specific Mocks,
* MockShared/MultiMockShared for Shared Sources (like native),
* MockCommon/MultiMockCommon for CommonTest.

### Single Interface Mocks
Declaring a Mock for common or platforms working the same.
Mock/MockCommon take a arbitrary amount of interfaces which will used as Templates to generate Mocks of:
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

MockShared takes an additional argument `sourceSetName` in order to associate with the Shared Source Set like `iosTest`.
You can also you only a short reference instead of the full name (e.g. long: `iosTest`, short: `ios`):
```kotlin
import tech.antibytes.kmock.MockShared

@MockShared(
   "native",
   SampleInterface::class,
   OtherSampleInterface::class,
   ...
)
class SampleTestSet {
   ...
}
```

!!!tip
    You do not have to mix those Annotations per test set.
    KMock is totally fine if you declare a Template in one source as common and in another source as platform source.
    It will simply resolve any conflicts according to their precedence in Kotlin's hierarchical source sets.
    As a rule of thumb - you need for Single Interface Mocks one Annotation per test set.

### Multi Interface Mocks
KMock is also capable of generate Mocks based on multiple interfaces.
Similar to Single Interface Mocks, MultiMock and MultiMockCommon can declare Mocks in the very similar way:
```kotlin
import tech.antibytes.kmock.MultiMockCommon

@MultiMockCommon(
    "MergedCommon",
    SampleDomainObject::class,
    ExampleContract.DecoderFactory::class
)
class SampleTestSet {
    ...
}
```
In difference to Single Interface Mocks, Multi Interface Mocks Annotations can be applied multiple times per test set.
Also all Multi Interface Mocks Annotations need an additional argument `name` which will be used as name for the generated Mock.
All referenced Interfaces will then be merged into one Mock.
As with Single Interface Mocks, the Annotation for Shared Sources needs an argument `sourceSetName` to bind the generated Mock to an Source Set:
```kotlin
import tech.antibytes.kmock.MultiMockShared

@MultiMockShared(
    sourceSetName = "nativeTest",
    name = "MergedShared",
    SampleDomainObject::class,
    ExampleContract.DecoderFactory::class
)
class SampleTestSet {
    ...
}
```
!!!warning
    Do not use this feature to create one Mock for all tests. It was never intended for that.

## Custom Annotations
You can also use your custom Annotations in order to ease the handling with Shared sources.
Your Annotations are not allowed to reference common and have to take a arbitrary amount of KClass (aka vararg) for Single Interface Mocks.
For Multi Interface Mocks your Annotations have to take as the very first argument a String additionally to the KClasses:
```kotlin
package my.package

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class Custom(vararg val interfaces: KClass<*>)

@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class MultiCustom(val name: String, vararg val interfaces: KClass<*>)
```

## Experimental Annotations
With 0.2.0 KMock offers 2 new experimental Annotation - `KMock` and `KMockMulti` which lift the need of telling KMock to which Source Set a Mock binds.
Those Annotations will determine the Source Set of a Mock based on the contexts it is declared.
However those Annotations are considers as experimental since there might be edge cases which are not covered by them.
They can be used in the exact same way as `Mock` or `MockMulti`:
```kotlin
import tech.antibytes.kmock.MultiMockShared

@KMock(
    SampleInterface::class,
    OtherSampleInterface::class,
    ...
)
@KMockMulti(
    name = "MergedShared",
    SampleDomainObject::class,
    ExampleContract.DecoderFactory::class
)
class SampleTestSet {
    ...
}
```
