# Concept
Writing mocks is no witchcraft, but doing them by hand day for day can be dull.
While KMP has come to stay it still lacks a mock library which can deal well with KMPs language features satisfactorily.
Well complaining is easy and "how hard can it"™ be to create such a library? Hence, KMock was born.

## Approach
As other existing KMP mock libraries, KMock utilizes the power of [Kotlin Symbol Processing (KSP)](https://github.com/google/ksp) and generates mocks based on interfaces.
Well, actually, in a strict sense, KMock generates [stubs or fakes](https://www.martinfowler.com/articles/mocksArentStubs.html) to a certain degree.

It draws its biggest inspiration from Python’s [MagicMock](https://docs.python.org/3/library/unittest.mock.html) in terms of the API, since it is easy to work with and offers conceptually a way to deal with the nature of KMP.
While some mocking libraries depend on record-replay-verify pattern, KMock does not.
It's mind model blend into the [arrange-act-assert pattern (AAA)](https://automationpanda.com/2020/07/07/arrange-act-assert-a-pattern-for-writing-good-testspattern) as much as possible.
For example while you write in mockk:
```kotlin
@Test
fun sampleTest() {
    // arrange
    val someInstance: SomeInterface = mockk()

    // record
    every { someInstance.someProperty } returns  "any"
    every { someInstance.someMethod(any(), any()) } returns  "any"

    // act
    val someOtherInstance = SomeClass(someInstance)
    someOtherInstance.run()

    // assert
    verify(exactly = 1) { someInstance.someProperty }
    verify(exactly = 1) { someInstance.someMethod(any(), any()) }
}

```
You'll write in KMock:
```kotlin
@Test
fun sampleTest() {
    // arrange
    val someInstance: SomeInterfaceMock = kmock()

    someInstance._someProperty.get = "any"
    someInstance._someMethod.returns  = "any"

    // act
    val someOtherInstance = SomeClass(someInstance)
    someOtherInstance.run()

    // assert
    assertProxy {
        someInstance._someProperty.wasGotten()
        someInstance._someMethod.hasBeenCalledWith(any(), any())
    }
}

```
In fact KMock will proxy each public member of a given interface instead of proxy the entire interface at once.
This also allows for less intrusive behaviour than traditional mocking libraries.

A second noteworthy side effect due to this approach is that most functionality is directly bound to the generated Mocks, so things will just pop up in your IDE or Editor.
This hopefully flattens the learning curve.
Lastly this allows to minimize any ambiguity in terms of overloading or eases the work with receivers, which are runtime based libraries mostly struggle with.
Another ingredient of KMock is that the library should do the heavy lifting as much as possible.
This means a consumer should not have to engage in complicated configuration to make it work with KMP.

Ideally it should just work, while supporting different tests and code styles.
Therefore KMock implements a basic support for Relaxation or Spying.
Also most features operate on demand and can be turned on/off.

But each luck has its price…

## Trade Offs
While it so far it all sounds appealing, KMock of course has its conceptual trade offs.
First and foremost compile time - while other libraries will try to resolve mostly everything at runtime, KMock will distribute things between compile- and runtime.
This naturally means compiling will need more time, even if we speak about seconds here.
Even though KMock is not optimized yet, it will most likely never be completely able to go toe to toe with pure runtime libraries.
But you pay for accuracy and convenience.

Also direct proxy access is not robust against refactoring since the generated names are detached from the interface it is based on.
This goes especially for overloaded names and their bring their own batch of ugliness.
While this a major drawback, KMock has experimental features to address these problems.
Even though they are not completely matured yet and cannot be used for all use cases, they hopefully ease those issues.

## What is the overall goal?
Aside from the roadmap, KMock 's main aim is to make your life easier as much as possible.
It will offer at some point in the future a proof of concept how implementation can be mocked, while it most likely never makes it into the main branch.
