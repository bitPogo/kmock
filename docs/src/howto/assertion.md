# Assertion and Verification
As other libraries KMock offers a way to assertion or verify invocations of Proxies (not Mock!).
Please make sure you have read on the [Terminology](terminology.md) before you start.

## Expectations Method

Assertion and Verification share the same API if you reference a Proxy via an Expectation Method.
While those methods will behave similarly in each Context, they have different nuances.

```kotlin
@Test
fun sampleTest() {
    // arrange
    val someInstance: SampleRemoteRepositoryMock = kmock()
    
    ...

    // assert
    verify(exactly = 1) { someInstance._fetch.hasBeenCalledWith("someUrl") }
    assertProxy { someInstance._find.hasBeenCalledWith("something") }
}
```

`hasBeenCalledWith` for example is such a Expectation Method.
Other Expectation Method for FunProxies are:

| Method                           | What it does                    |
| -------------------------------- | ------------------------------- |
| `hasBeenCalled`             | determines if the Proxy was invoked. |
| `hasBeenCalledWithVoid`     | determines if the Proxy was invoked without any arguments. |
| `hasBeenCalledWith`         | determines if the Proxy was invoked with the given arguments.<br/>The arguments must follow the order of the Templates arguments but can contain gaps and do not need exhaustive. |
| `hasBeenStrictlyCalledWith` | determines if the Proxy was invoked with the given arguments.<br/>The arguments must follow the order of the Template arguments strictly and must provide all arguments in their order. |
| `hasBeenCalledWithout`      | determines if the Proxy was invoked without the given arguments. |

PropertyProxies have their own specialized set of Expectation Methods:

| Method                  | What it does                         |
| ----------------------- | ------------------------------------ |
| `wasGotten`             | determines if the Proxy was invoked as a getter. |
| `wasSet`                | determines if the Proxy was invoked as a setter. |
| `wasSetTo`              | determines if the Proxy was invoked as a setter with the given argument. |

## Argument Constraints
While it might not always be desirable to assert/verify against a concrete value, KMock also offers ArgumentConstraints.
For example if you simply want to confirm that a Proxy was called with a certain type you may do the following:

```kotlin
@Test
fun sampleTest() {
   val someInstance: SampleRemoteRepositoryMock = kmock()
   ...

   verify(exactly = 1) {
       someInstance._fetch.hasBeenCalledWith(any(String::class))
   }
}
```

Internally all concrete types will be converted in a `eq` constraint.
For example an Expectation Method is called with `42`. 
`42` will be converted into `eq(42)`.
Currently the following constraints are implemented:

| ArgumentConstraint      | What it does                         |
| ----------------------- | ------------------------------------ |
| `any`                   | matches always (including null).<br/>If a concrete type was given it matches only if a recorded argument fulfils the expected type (exclusive null). |
| `eq`                    | matches if the recorded argument is equal to the expected argument. |
| `isNot`                 | matches if the recorded argument is not equal to the expected argument. |
| `isSame`                | matches if the recorded argument is identical as the expected argument. |
| `isNotSame`             | matches if the recorded argument is not identical as the expected argument. |
| `not`                   | matches if the negated matcher of a given ArgumentConstraint matches. |
| `or`                    | which allows to chain multiple values or constraints together with a logical or. |

## Single Proxy Assertion/Verification
As you might now already seen KMock offers for Single Proxy Assertion/Verification `verify` and `assertProxy`.
So what is now exactly the difference and what has that to do with the Expectation Methods?
Well, `verify` will look if any invocation of the Proxy will match the given Expectation.
`assertProxy` will go through the invocation from the first to latest.
In short `verify` can skip invocations while `assertProxy` cannot.

Also both functions are not capable be used to make statements about the interactions of multiple Proxies.
But you can use them with multiple Proxies:
```kotlin
@Test
fun sampleTest() {
    val someInstance: SampleRemoteRepositoryMock = kmock()
    ...

    verify(atLeast = 1) {
        someInstance._fetch.hasBeenCalledWith(any(String::class))
    }

    verify(atLeast = 1) {
        someInstance._find.hasBeenCalledWith(any(String::class))
    }
    
    assertProxy {
        someInstance._fetch.hasBeenCalledWith(any(String::class))
        someInstance._fetch.hasBeenCalledWith(any(String::class))
        
        someInstance._find.hasBeenCalledWith(any(String::class))
    }
}
```
As shown above you can and assuming `fetch` has been called 2x and with that `_fetch` was invoked 2x.
Also assuming `find` was invoked, `verify` and `assertProxy` will pass.
However you cannot say if `fetch` was called before or after `find`.
Assuming now `fetch` has been called only once, `verify` will still pass, while `assertProxy` will fail.
Since `verify` can be less strict about the order it can check if a Proxy was invoked `exactly`, `atLeast` or `atMost` certain times.
`assertProxy` on the other will always tell what is wrong and where but will only cover those invocation you told it cover.

!!!tip
    As a rule of thumb - if you want to cover a Proxy which is invoked multiple times you should consider using verify.
    If you cover a proxy which is invoked only once you should consider `assertProxy`.
    If you want really make sure a proxy is invoked in a certain order and certain times use both.

!!!note
    You need only one `assertProxy` per test case, strictly speaking.
    In terms of `verify` you need one per Verification.

### Experimental Operators
Due to the special nature of `verify` KMock offers a additional way to make `verify` more versatile and expressive - Operators:
Consider following source:
```kotlin
interface SampleThing {
    fun method(arg0: String, arg1: Int, arg2: Any)
}
```
KMock will offer you following possibility to verify the invocations:
```kotlin
@Test
fun sampleTest() {
    // arrange
    val someInstance: SampleThingMock = kmock()
    
    ...
    verify(atLeast = 1) {
        someInstance._find.method("payload", 23, "any") or
            someInstance._find.method("payload", 42, "notAny")
    }

}
```
Instead of:
```kotlin
@Test
fun sampleTest() {
    // arrange
    val someInstance: SampleThingMock = kmock()
    
    ...
    verify(atLeast = 1) {
        someInstance._find.method("payload", or(eq(23), eq(42)), or(eq("any"), eq("notAny")))
    }

}
```

These Operators are still experimental but will become stable soon.
Other Operators are:

| Operator                | What it does                         |
| ----------------------- | ------------------------------------ |
| `union`                 | resolves the union of 2 Expectations. |
| `or`                    | is an alias of `union`. |
| `intersection`          | resolves the intersection of 2 Expectations. |
| `and`                   | is an alias of `intersection`. |
| `diff`                  | resolves the symmetrical difference of 2 Expectations. |
| `xor`                   | is an alias of `diff` |

## Multi Proxy Assertion/Verification
To complete Assertion/Verification for Proxies KMock offers a way to keep track of interaction between Proxies of one or Mocks.
To make this possible you need to inject a Asserter/Verifier into the Mocks when they are initialized via the `collector` argument of `kmock` or `kspy`.

!!!note
    Asserter and Verifier are the same. In fact Verifier is a type alias of Asserter.

```kotlin
fun sampleTest() {
    // arrange
    val asserter = Asserter()
    val someInstance: SomeInterfaceMock = kmock(collector = asserter)
    val someInstanceOther: SomeInterfaceMock = kmock(collector = asserter)

   ...
    // assert
    asserter.assertOrder {
       someInstance._someProperty.wasGotten()
       someInstanceOther._someMethod.hasBeenCalled()
       someInstance._someMethod.hasBeenCalled()
   }
}
```

KMock offers 3 flavours to deal with interaction of multiple Proxies - `assertOrder`, `verifyStrictOrder` and `verifyOrder`.

* `assertOrder` is super strict - you must cover all invocations of all collected Proxies in order and exhaustively.
* `verifyStrictOrder` is less strict - it allows you to skip an arbitrary number of invocations of Proxies, but once you started to verify them it acts like `assertOrder`.
* `verifyOrder` is even less strict but also less descriptive in terms of errors - it allows you to skip an arbitrary invocations of Proxies and will only care about the relative order of Proxies.

All 3 flavours use the same API as verify/assertProxy to determine if a invocation is valid or not.
For example:

```kotlin
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

* `assertOrder` will fail if the invocation will not exactly in the order `someProperty` - `someMethod` - `someMethod` and are not only these invocations.
* `verifyStrictOrder` will fail if the invocation will not exactly in the order `someProperty` - `someMethod` - `someMethod` but it will not care if any invocation happens before that chain.
* `verifyOrder` will fail if the invocation is not in the relative order `someProperty` - `someMethod` - `someMethod` but it will not care if any invocation happens before, after or in between them.

## Compatibility with Proxy Access Methods

In case you use Proxy Access Methods with Coroutines (aka `asyncProxyOf`) you may have to use the corresponding Verification/Assertion methods.
They act the very same way as describe the described methods above and start with the prefix `async` like `asyncVerify`.
Do not use them with direct Proxy Access together - their soul purpose is bound to `asyncProxyOf` and `asyncProxyOf` only.
