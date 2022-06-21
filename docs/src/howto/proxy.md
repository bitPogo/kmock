# Using Mocks
## Before you start
Once you have declared the Mocks you are going to use, please run your test suite once.
(Even if you have no meaningful tests defined yet.)
This triggers the  Code Generation of Mocks and you may benefit from autocompletion in your Editor or IDE right away.
All Mocks (including Multi Interface Mocks) end on`Mock` to ensure a unified way to reference them.

## Proxies
KMock will add double members - __Proxy Properties__ - (do not confuse them with __PropertyProxies__) to the generated Mock.
All Proxy Properties are named with leading `_` followed by the Templates original name.
In case Templates are overloaded the proxy names will get additional suffix lead by `With` and derived from the Templates arguments types to ensure its uniqueness as much as possible.
In case there is are still collisions you have additional options via KMocks [Gradle Plugin Extension](setup.md)

KMock in general distinguishes between PropertyProxies and FunProxies, which are referring to either to properties or methods of a Template.

## FunProxies
FunProxies don't differentiate between asynchronous and synchronous in their API.
Therefore anything works for both modi alike.
KMock offers a simple way to facilitate stubbing.
Consider following source:
```kotlin
interface SampleRemoteRepository {
    suspend fun fetch(url: String): Any
    fun find(id: String): Any
}
```
You can deal with the resulting Mock for example like this:
```kotlin
@Test
fun sampleTest() {
   // arrange
   val someInstance: SampleRemoteRepositoryMock = kmock()

   someInstance._fetch.returnValue = Any()
   someInstance._find returns "any"

   // act
   val someOtherInstance = SomeClass(someInstance)
   someOtherInstance.execute()
   ...
}
```

In short KMock allows to assign canned values which are returned once the Template Method is invoked.
In case you need a more elaborate behaviour you can use SideEffects:
```kotlin
@Test
fun sampleTest() {
   // arrange
   val someInstance: SampleRemoteRepositoryMock = kmock()

   someInstance._fetch run {
       delay(20)
       return Any()
   }

   someInstance._find.sideEffect = { id ->
       return if (id.isEmpty()) {
           throw RuntimeException()
       } else {
           Any()
       }
   }

   // act
   val someOtherInstance = SomeClass(someInstance)
   someOtherInstance.run()
   ...
}
```

You can use the following properties of FunProxies:

| Property        | What it does                         |
| --------------- | ------------------------------------ |
| `returnValue`   | The Proxy will return the always the given value. |
| `returnValues`  | The Proxy will return each value of the given list.<br/> If only one value is left it will return it until the run is completed. |
| `error`         | The Proxy will throw the given error/exception. |
| `errors`        | The Proxy will throw each value of the given list.<br/> If only one error/exception is left it will throw it until the run is completed. |
| `sideEffect`    | The Proxy will execute the given SideEffect and returns its result. |
| `sideEffects`   | The Proxy will execute each SideEffect and returns its result. <br/> If only one SideEffect is left it will execute it until the run is completed. |


!!!note
    Please be aware there is a precedence of invocation.
    `errors` is used over  `error`, `returnValue` is used over `errors`, `returnValues` is used over `returnValues`, `sideEffect` is used over `returnValues` and `sideEffects` is used over `sideEffect`.
    If no behaviour is set the Proxy simply fails and acts therefore intrusively.

Additionally you can use the following infix methods to mutate its values:

| Method          | What it does                         |
| --------------- | ------------------------------------ |
| `returns`       | Alias setter of `returnValue`.<br/>The Proxy will return the always the given value. |
| `returnsMany`   | Alias setter of `returnValues`.<br/>The Proxy will return each value of the given list.<br/> If only one value is left it will return it until the run is completed. |
| `throws`        | Alias setter of `error`.<br/>The Proxy will throw the given error/exception. |
| `throwsMany`    | Alias setter of `errors`.<br/>The Proxy will throw each value of the given list.<br/> If only one error/exception is left it will throw it until the run is completed. |
| `run`           | Alias setter of `sideEffect`.<br/>The Proxy will execute the given SideEffect and returns its result. |
| `runs`          | Alias setter of `sideEffects`.<br/>The Proxy will execute each SideEffect and returns its result. <br/> If only one SideEffect is left it will execute it until the run is completed. |

A last word to SideEffects.
While they in general using the signature of the Template and therefore you can utilize the full power of the Kotlin's type system, there is 2 exceptions to this.
If you work with [Multi-Boundary Generics](https://kotlinlang.org/docs/generics.html) and declare them on Method level KMock cannot derive a certain type, since Kotlin has no Union Types per se.
The same goes for recursive types as well (e.g. `T : Comparable<T>`).
Those types are derived as `Any` and you have to make sure to cast them correctly if you use them.

## PropertyProxies
PropertyProxies work in a very similar fashion as FunProxies.
Consider following source:
```kotlin
interface SampleDomainObject {
    val id: String
    var something: Int
}
```

In short KMock allows to assign simple canned values which are returned once the Template Property is invoked:
```kotlin
@Test
fun sampleTest() {
   // arrange
   val someInstance: SampleDomainObjectMock = kmock()

   someInstance._id.getValue = "Any"
   someInstance._something.set = { value ->
       if (value == 0) {
           throw RuntimeException()
       }
   }

   // act
   val someOtherInstance = SomeClass()
   someOtherInstance.execute(someInstance)
   ...
}
```

Property Setters are always using SideEffects but work completely non intrusive and have no need for a explicit setup.
Property Getters can be use either stub values or a SideEffect.
Both - Setter and Getter - do not allow multiple SideEffects since this should be considered as a clear sign that the usage of method is more appropriate instead.

PropertyProxies have the following properties:

| Property       | What it does                         |
| -------------- | ------------------------------------ |
| `getValue`     | The Proxy will return the always the given value. |
| `getValues`    | The Proxy will return each value of the given list.<br/> If only one value is left it will return it until the run is completed. |
| `get`          | The Proxy will execute the given SideEffect and return its result. |
| `set`          | The Proxy will execute the given SideEffect. |


!!!note
    Please be aware there is a precedence of invocation.
    `getValues` is used over `getValue` and `get` is used over `getValues`.
    If no behaviour is set the Proxy simply fails and acts intrusively.

Additionally you can use the following infix methods to set values:

| Method         | What it does                         |
| -------------- | ------------------------------------ |
| `returns`      | Alias setter of `getValue`.<br/> Proxy will return the always the given value. |
| `returnsMany`  | Alias setter of `getValues`.<br/> Proxy will return each value of the given list.<br/> If only one value is left it will return it until the run is completed. |
| `runOnGet`     | Alias setter of `get`.<br/> Proxy will execute the given SideEffect and return its result. |
| `runOnSet`     | Alias setter of `set`.<br/> Proxy will execute the given SideEffect. |

## ReceiverProxies
While receivers are technically not a special type of Proxy they are still special.
KMock will do what the compiler does with them and sees them as methods regardless if they are a property or not.
The first argument of those methods will be always the type of the Receiver.
In case the Template is a Property Receiver, KMock generates one or two methods which depends on the mutability of the Template Receiver.
Additionally the generated Proxy is suffixed with `Getter`/`Setter`.
In case the Template is a Method Receiver, KMock will just suffix the name with an additional `Receiver`.
Unlike other libraries this makes it possible to access Receivers directly without the need of any workaround to reference them.

## The KMock Factory
As you might already seen at this point KMock generates also Factories to ease the initialization of Mocks.
`kmock` takes 4 arguments: `collector`, `relaxed`, `relaxUnitFun` and `freeze`.
`relax` and `relaxUnitFun` determine if relaxing should be used and are false by default.
`freeze` determines which flavour for the Memory Model is used and is true by default if no custom default value is set.

In case of Single Interface Mocks and the Template uses Generics an additional argument is mandatory - `templateType`.
This argument takes the KClass of the Template:

```kotlin
@Test
fun sampleTest() {
    // arrange
    val genericThing: SomethingGenericMock<Int> = kmock(
        templateType = SomethingGeneric::class,
    )

    ...
}
```

In case of Multi-Interface Mocks and a Template uses Generics a additional arguments are mandatory - `templateType$Idx`.
Those type take the KClass of the Templates:

```kotlin
@Test
fun sampleTest() {
    // arrange
    val genericThing: GenericMultiMock<Int, String, *> = kmock(
        templateType0 = SomethingGeneric::class,
        templateType1 = AnythingGeneric::class,
    )

    ...
}
```

Also KMock will not merge compatible Generic Types, since it cannot be sure if they are independent or not.
This means if you reference the Mock you always have to explicitly declare all generics.

## Teardown
All Mocks are generated with a `_clearMock` method which clears all Proxies owned by the Mock.
This enables you to use Mocks per test suite and not only per test case:

```kotlin
@MockCommon(
    SampleInterface::class,
    ...
)
class SampleTestSet {
    private val someInstance: SomeInterfaceMock = kmock()

    @AfterTest
    fun tearDown() {
        someInstance._clearMock()
    }

    @Test
    fun sampleTest() {
        ...
    }

}
```

## Experimental Proxy Access Methods
0.2.0 KMock introduces a new way to work with Proxies.
This is done to provide a more robust way in terms of refactoring.
However since those methods need a lot more thought and optimization they are considered as experimental but get hopefully soon in a much more stable state.

| Method           | What it does                         |
| ---------------- | ------------------------------------ |
| `propertyProxyOf`| will reference a PropertyProxy |
| `syncFunProxyOf` | will reference a non suspending FunProxy. |
| `asyncFunProxyOf`| will reference a suspending FunProxy. |

For example they can used as followed:

```kotlin
@Test
fun sampleTest() {
    // arrange
    val someInstance: SampleRemoteRepositoryMock = kmock()

    someInstance.asyncFunProxyOf(someInstance::fetch).sideEffect = {
        delay(20)
        return Any()
    }

    someInstance.asyncFunProxyOf(someInstance::find).sideEffect = { id ->
        return if (id.isEmpty()) {
            throw RuntimeException()
        } else {
            Any()
        }
    }

    // act
    val someOtherInstance = SomeClass(someInstance)
    someOtherInstance.run()
    ...
}
```

In case you deal with overloaded methods or methods with generics you need to provide those methods a hint, which is composed of the argument types of the Template.
Consider following source:
```kotlin
interface SampleGenericRemoteRepositoryMock {
    suspend fun fetch(): Any
    suspend fun <T> fetch(url: T): Any
    fun find(id: String): Any
    fun find(id: String, code: Int): Any
}
```
Which my be address by:
```kotlin
@Test
fun sampleTest() {
    // arrange
    val someInstance: SampleGenericRemoteRepositoryMock = kmock()

    someInstance.asyncFunProxyOf<Any>(someInstance::fetch, hint<Any>()).sideEffect = {
        delay(20)
        return Any()
    }

    someInstance.syncFunProxyOf(someInstance::find, hint<String, Int>()).sideEffect = { id ->
        return if (id.isEmpty()) {
            throw RuntimeException()
        } else {
            Any()
        }
    }

    // act
    val someOtherInstance = SomeClass(someInstance)
    someOtherInstance.execute()
    ...
}
```

While this may sound amazing these access method have their natural limitations due to Kotlin itself.
You will not be able to address Receivers in that way.
The signature of the Template maybe too ambiguous in certain cases which makes them unable to resolve the correct Proxy.
In those cases you may access Proxies directly.
Also you may encounter problems with incremental builds when you use expect/actual Aliases.
In those cases take a look at `preventResolvingOfAliases` in the Gradle Plugin [setup](setup.md).
