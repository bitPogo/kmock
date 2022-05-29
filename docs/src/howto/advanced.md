# Advanced Concepts

## Spying
KMock supports Spying to a certain degree.
While you still need to instantiate the _subject to spy on_ (SO) by hand, you can delegate the SO to `kspy` and voila habemus spy!

However Spying is disabled by default and is selective.
To enable it please take a look at the [Configuration of the Gradle Plugin](setup.md).

Once it is enabled KMock will generate an additional factory `kspy`.
`kspy` works similar to `kmock` and takes 3 arguments `spyOn`, `collect` and `freeze`.
`spyOn` expects the SO and you need to reference the Mock Class.

```kotlin
@Test
fun sampleTest() {
    // arrange
    val subjectToSpyOn = AnyImplementation()
    val someInstance: SomeInterfaceMock = kspy(spyOn = subjectToSpyOn)

   ...
}
```

Also as with `kmock`, if the Template is generic you have to delegate an additional argument `templateType` which must be the KClass of the Template the Spy is referring to.
Spies for MultiMocks will always treated like they are generic.
This means you have to delegate all template types the  multi mock is referring to:

```kotlin
@Test
fun sampleTest() {
    // arrange
    val subjectToSpyOn = AnyMultiImplementation()
    val someInstance: SomeInterfaceMock = kspy(
        spyOn = subjectToSpyOn,
        templateType0 = CharSequence::class,
        templateType1 = Comparable::class,
    )
   ...
}
```

Also you still can override specific methods or properties by using the corresponding Proxy Property.
In other words methods or properties of an SO are used instead of throwing an error, if no behaviour or stub was assigned to a Proxy, so it acts completely non intrusive.

!!!note
    Setters are always invoked.

### Special behaviour of Spies
In terms of Spies there is one big difference you need to be aware of.
Since build-in methods like `equals` are delegated to the SO as well it might result in a behaviour which is not obvious right away:

```kotlin
@Test
fun sampleTest() {
   val subjectToSpyOn = AnyImplementation()
   val someInstance: SomeInterfaceMock = kspy(subjectToSpyOn)

   assertTrue((someInstance as Any) == someInstance) // will pass
   assertTrue((someInstance as Any) == subjectToSpyOn) // will pass
   assertTrue((subjectToSpyOn as Any) == someInstance) // will fail
}

```

While the first and second Assertion passes the last won't.
Why is it that?
KMock uses inheritance not reflection as you remember.
The last assertion fails since `AnyImplementation` has no custom implementation of `equals` and uses the default.
If `AnyImplementation` makes a custom comparison with taking properties for example into account, the Assertion could pass.

## Relaxing
KMock also facilitates Relaxation to a certain degree and will hopefully get better over time.
However it requires boilerplate code done by you.
However, Relaxation of methods which return `Unit` is build-in feature, so do not worry about it.
You only need to switch `relaxUnitFun` to `true` and be done with it.
To get full Relaxation support you need to implement a relaxer function and annotate it properly:
```kotlin
@Relaxer
internal inline fun <reified T> relax(id: String): T {
   ...
}
```
The annotation `@Relaxer` tells KMock that a Relaxer is present and is up for usage.
The Relaxer can be an arbitrary function but has to follow a specific signature, similar as shown above.
The function can be either inline or not.
The type parameter can be either `reified` or not, but must be present and used as the return value.
There can be only one type parameter.
The function can be marked as internal or not, but must be visible by the Mocks.
Also the function must take exactly one argument, which is a String.
This argument will be provided when invoked by a Proxy and is its id.
You may use it to differentiate between Proxies.
At last you need to switch relaxing on by adding `relaxed = true` to `kmock`.

However since this sounds cumbersome there is already a project - KFixture - on its way to ease this, so stay tuned.

Well, in certain cases the type parameter will not work as return value - Generics.
If KMock encounters Generics as return values of a method/property it add additional arguments (`type$Idx`) to invocation of the Relaxer Function for example:
```kotlin
relax(
    proxyId,
    type0 = Any::class,
    type1 = Comparable::class,
)
```
This means KMock expects you to provide a proper definition of `fun relax(proxyId: String, type0: KClass<Any>, type1: KClass<Comparable<Any>>): Any`.
The amount of type arguments depends on the boundaries of the generic return type.
For example `<T> fun method(): T where T: Any, T: Comparable<Any>` will resolved to the invocation above.

## Memory Model
KMock still uses Kotlin's ‘old’ Memory Model.
Therefore you might run into some trouble in certain cases.

Both factory functions - `kmock` and `kspy` - take an argument `freeze` which is true by default.
This means everything which is delegated to an Proxy (including Relaxation) or Verifier must conform with the freezing Memory Model of Kotlin.
By switching `freeze` to `false` it changes the flavour to non-freezing.

If you use non freezing Mocks, make sure you use the corresponding Asserter/Verifier (`NonFreezingVerifier`/`NonFreezingAsserter`) and all interacting Mocks are non-freezing as well.
This is inconvenient for now but will go away once the new Memory Model is more stable.

## Customization
### ArgumentConstraints
KMock is capable of using your custom defined ArgumentConstraints.
You can simply extend `ArgumentConstraint` of the `KMockContract` and implement the functionality you require and use it. Done!

### Verification
While nearly all Assertion/Verification methods are not able to extended, you are still able to customize `verify` due to the power of [extension function](https://kotlinlang.org/docs/extensions.html) for either the `Proxy`, `FunProxy`, `PropertyProxy`, `SyncFunProxy` or `AsyncFunProxy` Interface of the `KMockContract`.
KMock exposes 4 functions which are intended for this purpose.
`getArgumentsByType`, `getAllArgumentsByType` and `getAllArgumentsBoxedByType` working only for FunProxies, while `getArgumentsForCall` works for all Proxy types.
`getArgumentsByType` can be used to retrieve Arguments for a specific call and type, `getAllArgumentsByType` and `getAllArgumentsBoxedByType` collecting over all invocations.
The main difference between the latter methods is simply that `getAllArgumentsByType` will collect arguments in a linear order,
while `getAllArgumentsBoxedByType` will box them in a List per call.

Use those methods with caution, since they are likely subject to changes, once the concept of assertions gets a second look.
KMock itself uses `getArgumentsForCall` which you might wanna use as well while working with PropertyProxies or you need a type unaware method to extract arguments from Proxies.

Happy coding!
