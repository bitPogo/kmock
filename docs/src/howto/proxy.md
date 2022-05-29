# Using Mocks
## Before you start
Once you have declared the Mocks you are going to use, please run your test suite once even if you have no meaningful tests defined.
This triggers the Generation the Mocks and you may benefit from autocompletion in your Editor or IDE right away.
All mocks (including MultiMocks) end with a `Mock` to ensure a unified way to reference them.

## Proxies
KMock will add double members - __Proxy Properties__ - (do not confuse them with __PropertyProxies__) to the generated Mock.
All Proxy Properties are named with leading `_` followed by the Templates original name, except overloaded for names.
In case Templates are overloaded the proxy names will get additional suffix lead by `With` and derived from the Templates arguments types to ensure its uniqueness as much as possible.
In case there is are still collisions you have additional options via KMocks [Gradle Plugin Extension](setup.md)

KMock in general distinguishes between PropertyProxies and FunProxies, which are referring to either Properties or Methods of a Template.

## FunProxies
FunProxies don't differentiate between asynchronous and synchronous, while you assign them values.
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

   someInstance._fetch.returns = Any()
   someInstance._find.returns = "any"

   // act
   val someOtherInstance = SomeClass(someInstance)
   someOtherInstance.run()
   ...
}
```

In short KMock allows to assign simply canned values which are return once the Template Method is invoked.
In case you need a more elaborate behaviour you can use SideEffects:
```kotlin
@Test
fun sampleTest() {
   // arrange
   val someInstance: SampleRemoteRepositoryMock = kmock()

   someInstance._fetch.sideEffect = {
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

| Property       | What it does                         |
| -------------- | ------------------------------------ |
| `returns`      | the Proxy will return the always the given value. |
| `returnMany`   | the Proxy will return each value of the given list.<br/> If only one value is left it will return it until the run is completed. |
| `throws`       | the Proxy will throw the given error/exception. |
| `throwsMany`   | the Proxy will throw each value of the given list.<br/> If only one error/exception is left it will throw it until the run is completed. |
| `sideEffect`   | the Proxy will execute the given SideEffect and returns its result. |
| `sideEffects`  | the Proxy will execute each SideEffect and returns its result. <br/> If only one SideEffect is left it will execute it until the run is completed. |
| `run`          | is an alias to `sideEffect. |
| `runs`         | is an alias to `sideEffects. |

!!!note
    Please be aware there is a precedence of invocation.
    `throwsMany` is used over  `throws`, returns` is used over `throwsMany`, `returnsMany` is used over `returns`, `sideEffect` is used over `returnsMany` and `sideEffects` is used over `sideEffect`.
    If no behaviour is set the Proxy simply fails and acts therefore intrusively.

A last word to SideEffects.
While they in general using the signature of the Template Method and therefore you can the full power of the Kotlin's type system, there is one exception to this.
If you work with [Multi-Boundary Generics](https://kotlinlang.org/docs/generics.html) and declare them on Method level KMock cannot derive a certain type, since Kotlin has no Union Types per se.
This means those types are derived as `Any` and you have to make sure to cast them correctly if you use them.

## PropertyProxies
PropertyProxies work in a very similar fashion as FunProxies.
Consider following source:
```kotlin
interface SampleDomainObject {
    val id: String
    var something: Int
}
```
In short KMock allows to assign simply canned values which are return once the Template Property is invoked:
```kotlin
@Test
fun sampleTest() {
   // arrange
   val someInstance: SampleDomainObjectMock = kmock()

   someInstance._id.get = "Any"
   someInstance._something.set = { value ->
       if (value == 0) {
           throw RuntimeException()
       }
   }

   // act
   val someOtherInstance = SomeClass()
   someOtherInstance.run(someInstance)
   ...
}
```
Setters are can be only a SideEffect are not required to run Mock to be assigned.
Getters can be either stubs or a SideEffect.
Both - Setter and Getter - do not allow multiple SideEffects since this should be considered as a clear sign that the usage of method is more appropriate instead.

PropertyProxies have the following properties:

| Property       | What it does                         |
| -------------- | ------------------------------------ |
| `get`          | the Proxy will return the always the given value. |
| `getMany`      | the Proxy will return each value of the given list.<br/> If only one value is left it will return it until the run is completed. |
| `getSideEffect`| the Proxy will execute the given SideEffect and return its result. |
| `set`          | the Proxy will execute the given SideEffect. |

!!!note
    Please be aware there is a precedence of invocation.
    `getMany` is used over `get` and `getSideEffect` is used over `getMany`.
    If no behaviour is set the Proxy simply fails and acts intrusively.


## ReceiverProxies
While receivers are not technically a special type of Proxy they are still special.
KMock will do what the compiler does with them and sees them as methods regardless if it declared as a property or not.
The first argument of those methods will be always the type of the Receiver.
In case the Template is a Property Receiver, KMock generates one or two methods which depends on the mutability of the Template Receiver.
Additionally the generated Proxy is suffixed with `Getter`/`Setter`.
In case the Template is a Method Receiver, KMock will just suffix the Name with a additional `Receiver`.
Unlike other libraries this makes it possible to access Receivers directly without the need of any workaround to reference them.

## The KMock Factory
As you might already seen at this point KMock generates also Factories to ease the initialization of Mocks.
`kmock` takes 4 arguments: `collector`, `relaxed`, `relaxUnitFun` and `freeze`.
`relax` and `relaxUnitFun` determine if relaxing should be used and are false by default.
`freeze` determines which flavour for the Memory Model is used and is true by default if no custom default value is set.

In case of Single Interface Mocks and the Template uses Generics a additional argument is mandatory - `templateType`.
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

In case of Multi Interface Mocks and the Templates use Generics a additional arguments are mandatory - `templateType$Idx`.
Those type take the KClass of the Templates:

```kotlin
@Test
fun sampleTest() {
    // arrange
    val genericThing: GenericMultiMock<Int, String> = kmock(
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
As with 0.2.0 KMock introduces a new way to work with Proxies.
This is done to provide a more robust way in terms of refactoring to work with Proxies.
However since those methods need a lot more thought and optimization they are considered as experimental but get hopefully in a much more stable state soon.

| Method           | What it does                         |
| ---------------- | ------------------------------------ |
| `propertyProxyOf`| will reference a PropertyProxy |
| `syncFunProxyOf` | will reference a non suspending FunProxy. |
| `asyncFunProxyOf`| will reference a suspending FunProxy. |

For example can they used as followed:

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

In case you deal with overloaded methods you need to give additionally those methods a hint, which is composed of the argument types.
Also if you mixin generics on the methods you have to reference them as well.
Consider following source:
```kotlin
interface SampleGenericRemoteRepositoryMock {
    suspend fun <T> fetch(): Any
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
    someOtherInstance.run()
    ...
}
```

While this may sound amazing these access method have their natural limitations due to Kotlin itself.
You will not be able to address Receivers in that way.
The signature of the Template may be too ambiguous in certain cases which makes them unable to resolve the correct Proxy.
In those cases you may access Proxies directly.