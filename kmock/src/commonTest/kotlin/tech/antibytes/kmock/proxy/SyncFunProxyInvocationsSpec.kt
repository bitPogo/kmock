/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.concurrency.AtomicReference
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.TestScopeDispatcher
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.resolveMultiBlockCalls
import tech.antibytes.util.test.coroutine.runBlockingTest
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
import tech.antibytes.util.test.mustBe

class SyncFunProxyInvocationsSpec {
    private val fixture = kotlinFixture()
    private val testScope1 = TestScopeDispatcher.dispatch("test1")
    private val testScope2 = TestScopeDispatcher.dispatch("test2")

    @BeforeTest
    fun setUp() {
        clearBlockingTest()
    }

    @Test
    @JsName("fn1")
    fun `Given invoke is called it calls the given SideEffect with 0 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val expected: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = {
                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.invoke()

            // Then
            actual mustBe expected
            proxy.getArgumentsForCall(0).toList() mustBe arrayOf<Any>().toList()
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn2")
    fun `Given invoke is called it calls the given SideEffectChain with 0 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, () -> Any>(fixture.fixture())
        val expected: Any = fixture.fixture()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add {
                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            val actual = proxy.invoke()

            // Then
            actual mustBe expected
            proxy.getArgumentsForCall(0).toList() mustBe arrayOf<Any>().toList()
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn3")
    fun `Given invoke is called it calls the given Spy with 0 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val expected: Any = fixture.fixture()
        val implementation = Implementation<Any>()
        val proxy = SyncFunProxy<Any, () -> Any>(
            fixture.fixture(),
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun0 = {
                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke {
                useSpyIf(implementation) {
                    implementation.fun0()
                }
            }

            // Then
            actual mustBe expected
            proxy.getArgumentsForCall(0).toList() mustBe arrayOf<Any>().toList()
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn4")
    fun `Given invoke is called it calls the given SideEffect with 1 Argument and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0 ->
                actualArgument0.set(givenArg0)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 1
            arguments[0] mustBe argument0
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn5")
    fun `Given invoke is called it calls the given SideEffectChain with 1 Argument and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0 ->
                actualArgument0.set(givenArg0)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 1
            arguments[0] mustBe argument0
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn6")
    fun `Given invoke is called it calls the given Spy with 1 Argument and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val argument0: String = fixture.fixture()
        val expected: Any = fixture.fixture()
        val implementation = Implementation<Any>()

        val proxy = SyncFunProxy<Any, (String) -> Any>(
            fixture.fixture(),
        )

        val actualArgument0 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun1 = { givenArg0 ->
                actualArgument0.set(givenArg0 as String?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
            ) {
                useSpyIf(implementation) {
                    implementation.fun1(
                        argument0,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 1
            arguments[0] mustBe argument0
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn7")
    fun `Given invoke is called it calls the given SideEffect with 2 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1 ->

                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 2
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn8")
    fun `Given invoke is called it calls the given SideEffectChain with 2 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1 ->

                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 2
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn9")
    fun `Given invoke is called it calls the given Spy with 2 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val proxy = SyncFunProxy<Any, (String, Int) -> Any>(
            fixture.fixture(),
        )

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun2 = { givenArg0, givenArg1 ->

                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
            ) {
                useSpyIf(implementation) {
                    implementation.fun2(
                        argument0,
                        argument1,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 2
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn10")
    fun `Given invoke is called it calls the given SideEffect with 3 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1, givenArg2 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1, argument2)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 3
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn11")
    fun `Given invoke is called it calls the given SideEffectChain with 3 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1, givenArg2 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1, argument2)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 3
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn12")
    fun `Given invoke is called it calls the given Spy with 3 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)

        val proxy = SyncFunProxy<Any, (String, Int, String) -> Any>(
            fixture.fixture(),
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun3 = { givenArg0, givenArg1, givenArg2 ->
                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)
                actualArgument2.set(givenArg2 as String?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
            ) {
                useSpyIf(implementation) {
                    implementation.fun3(
                        argument0,
                        argument1,
                        argument2,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 3
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn13")
    fun `Given invoke is called it calls the given SideEffect with 4 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1, argument2, argument3)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 4
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn14")
    fun `Given invoke is called it calls the given SideEffectChain with 4 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1, givenArg2, givenArg3 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1, argument2, argument3)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 4
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn15")
    fun `Given invoke is called it calls the given Spy with 4 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val expected: Any = fixture.fixture()

        val proxy = SyncFunProxy<Any, (String, Int, String, Int) -> Any>(
            fixture.fixture(),
        )

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun4 = { givenArg0, givenArg1, givenArg2, givenArg3 ->
                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)
                actualArgument2.set(givenArg2 as String?)
                actualArgument3.set(givenArg3 as Int?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
            ) {
                useSpyIf(implementation) {
                    implementation.fun4(
                        argument0,
                        argument1,
                        argument2,
                        argument3,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 4
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn16")
    fun `Given invoke is called it calls the given SideEffect with 5 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)

        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)

                expected
            }
        }

        runBlockingTest {
            // When
            val actual = proxy.invoke(argument0, argument1, argument2, argument3, argument4)
            actual mustBe expected

            // Then
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 5
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn17")
    fun `Given invoke is called it calls the given SideEffectChain with 5 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)

        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)

                expected
            }
        }

        runBlockingTest {
            // When
            val actual = proxy.invoke(argument0, argument1, argument2, argument3, argument4)
            actual mustBe expected

            // Then
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 5
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn18")
    fun `Given invoke is called it calls the given Spy with 5 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)

        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String) -> Any>(
            fixture.fixture(),
        )

        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun5 = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4 ->
                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)
                actualArgument2.set(givenArg2 as String?)
                actualArgument3.set(givenArg3 as Int?)
                actualArgument4.set(givenArg4 as String?)

                expected
            }
        }

        runBlockingTest {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
            ) {
                useSpyIf(implementation) {
                    implementation.fun5(
                        argument0,
                        argument1,
                        argument2,
                        argument3,
                        argument4,
                    )
                }
            }
            actual mustBe expected

            // Then
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 5
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn19")
    fun `Given invoke is called it calls the given SideEffect with 6 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1, argument2, argument3, argument4, argument5)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 6
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn20")
    fun `Given invoke is called it calls the given SideEffectChain with 6 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1, argument2, argument3, argument4, argument5)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 6
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn21")
    fun `Given invoke is called it calls the given Spy with 6 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)

        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int) -> Any>(
            fixture.fixture(),
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun6 = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5 ->
                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)
                actualArgument2.set(givenArg2 as String?)
                actualArgument3.set(givenArg3 as Int?)
                actualArgument4.set(givenArg4 as String?)
                actualArgument5.set(givenArg5 as Int?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
            ) {
                useSpyIf(implementation) {
                    implementation.fun6(
                        argument0,
                        argument1,
                        argument2,
                        argument3,
                        argument4,
                        argument5,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 6
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn22")
    fun `Given invoke is called it calls the given SideEffect with 7 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1, argument2, argument3, argument4, argument5, argument6)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 7
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn23")
    fun `Given invoke is called it calls the given SideEffectChain with 7 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(argument0, argument1, argument2, argument3, argument4, argument5, argument6)

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 7
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn24")
    fun `Given invoke is called it calls the given Spy with 7 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)

        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String) -> Any>(
            fixture.fixture(),
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun7 = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6 ->
                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)
                actualArgument2.set(givenArg2 as String?)
                actualArgument3.set(givenArg3 as Int?)
                actualArgument4.set(givenArg4 as String?)
                actualArgument5.set(givenArg5 as Int?)
                actualArgument6.set(givenArg6 as String?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
            ) {
                useSpyIf(implementation) {
                    implementation.fun7(
                        argument0,
                        argument1,
                        argument2,
                        argument3,
                        argument4,
                        argument5,
                        argument6,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 7
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn25")
    fun `Given invoke is called it calls the given SideEffect with 8 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 8
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn26")
    fun `Given invoke is called it calls the given SideEffectChain with 8 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 8
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn27")
    fun `Given invoke is called it calls the given Spy with 8 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)

        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int) -> Any>(
            fixture.fixture(),
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun8 = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7 ->
                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)
                actualArgument2.set(givenArg2 as String?)
                actualArgument3.set(givenArg3 as Int?)
                actualArgument4.set(givenArg4 as String?)
                actualArgument5.set(givenArg5 as Int?)
                actualArgument6.set(givenArg6 as String?)
                actualArgument7.set(givenArg7 as Int?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
            ) {
                useSpyIf(implementation) {
                    implementation.fun8(
                        argument0,
                        argument1,
                        argument2,
                        argument3,
                        argument4,
                        argument5,
                        argument6,
                        argument7,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 8
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn28")
    fun `Given invoke is called it calls the given SideEffect with 9 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 9
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn29")
    fun `Given invoke is called it calls the given SideEffectChain with 9 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String) -> Any>(fixture.fixture())
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 9
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn30")
    fun `Given invoke is called it calls the given Spy with 9 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)

        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String) -> Any>(
            fixture.fixture(),
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun9 = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8 ->
                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)
                actualArgument2.set(givenArg2 as String?)
                actualArgument3.set(givenArg3 as Int?)
                actualArgument4.set(givenArg4 as String?)
                actualArgument5.set(givenArg5 as Int?)
                actualArgument6.set(givenArg6 as String?)
                actualArgument7.set(givenArg7 as Int?)
                actualArgument8.set(givenArg8 as String?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
            ) {
                useSpyIf(implementation) {
                    implementation.fun9(
                        argument0,
                        argument1,
                        argument2,
                        argument3,
                        argument4,
                        argument5,
                        argument6,
                        argument7,
                        argument8,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 9
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn31")
    fun `Given invoke is called it calls the given SideEffect with 10 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int) -> Any>(
            fixture.fixture(),
        )
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 10
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn32")
    fun `Given invoke is called it calls the given SideEffectChain with 10 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int) -> Any>(
            fixture.fixture(),
        )
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 10
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn33")
    fun `Given invoke is called it calls the given Spy with 10 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)

        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int) -> Any>(
            fixture.fixture(),
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun10 = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9 ->
                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)
                actualArgument2.set(givenArg2 as String?)
                actualArgument3.set(givenArg3 as Int?)
                actualArgument4.set(givenArg4 as String?)
                actualArgument5.set(givenArg5 as Int?)
                actualArgument6.set(givenArg6 as String?)
                actualArgument7.set(givenArg7 as Int?)
                actualArgument8.set(givenArg8 as String?)
                actualArgument9.set(givenArg9 as Int?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
            ) {
                useSpyIf(implementation) {
                    implementation.fun10(
                        argument0,
                        argument1,
                        argument2,
                        argument3,
                        argument4,
                        argument5,
                        argument6,
                        argument7,
                        argument8,
                        argument9,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 10
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn34")
    fun `Given invoke is called it calls the given SideEffect with 11 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String) -> Any>(
            fixture.fixture(),
        )
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)
                actualArgument10.set(givenArg10)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When

            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 11
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn35")
    fun `Given invoke is called it calls the given SideEffectChain with 11 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String) -> Any>(
            fixture.fixture(),
        )
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)
                actualArgument10.set(givenArg10)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When

            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 11
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn36")
    fun `Given invoke is called it calls the given Spy with 11 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)

        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String) -> Any>(
            fixture.fixture(),
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun11 = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10 ->
                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)
                actualArgument2.set(givenArg2 as String?)
                actualArgument3.set(givenArg3 as Int?)
                actualArgument4.set(givenArg4 as String?)
                actualArgument5.set(givenArg5 as Int?)
                actualArgument6.set(givenArg6 as String?)
                actualArgument7.set(givenArg7 as Int?)
                actualArgument8.set(givenArg8 as String?)
                actualArgument9.set(givenArg9 as Int?)
                actualArgument10.set(givenArg10 as String?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10,
            ) {
                useSpyIf(implementation) {
                    implementation.fun11(
                        argument0,
                        argument1,
                        argument2,
                        argument3,
                        argument4,
                        argument5,
                        argument6,
                        argument7,
                        argument8,
                        argument9,
                        argument10,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 11
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn37")
    fun `Given invoke is called it calls the given SideEffect with 12 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String, Int) -> Any>(
            fixture.fixture(),
        )
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()
        val argument11: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)
        val actualArgument11 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10, givenArg11 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)
                actualArgument10.set(givenArg10)
                actualArgument11.set(givenArg11)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10,
                argument11,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10
            actualArgument11.get() mustBe argument11

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 12
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
            arguments[11] mustBe argument11
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn38")
    fun `Given invoke is called it calls the given SideEffectChain with 12 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String, Int) -> Any>(
            fixture.fixture(),
        )
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()
        val argument11: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)
        val actualArgument11 = AtomicReference<Int?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10, givenArg11 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)
                actualArgument10.set(givenArg10)
                actualArgument11.set(givenArg11)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10,
                argument11,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10
            actualArgument11.get() mustBe argument11

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 12
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
            arguments[11] mustBe argument11
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn39")
    fun `Given invoke is called it calls the given Spy with 12 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()
        val argument11: Int = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)
        val actualArgument11 = AtomicReference<Int?>(null)

        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String, Int) -> Any>(
            fixture.fixture(),
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun12 = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10, givenArg11 ->
                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)
                actualArgument2.set(givenArg2 as String?)
                actualArgument3.set(givenArg3 as Int?)
                actualArgument4.set(givenArg4 as String?)
                actualArgument5.set(givenArg5 as Int?)
                actualArgument6.set(givenArg6 as String?)
                actualArgument7.set(givenArg7 as Int?)
                actualArgument8.set(givenArg8 as String?)
                actualArgument9.set(givenArg9 as Int?)
                actualArgument10.set(givenArg10 as String?)
                actualArgument11.set(givenArg11 as Int?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10,
                argument11,
            ) {
                useSpyIf(implementation) {
                    implementation.fun12(
                        argument0,
                        argument1,
                        argument2,
                        argument3,
                        argument4,
                        argument5,
                        argument6,
                        argument7,
                        argument8,
                        argument9,
                        argument10,
                        argument11,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10
            actualArgument11.get() mustBe argument11

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 12
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
            arguments[11] mustBe argument11
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn40")
    fun `Given invoke is called it calls the given SideEffect with 13 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String, Int, String) -> Any>(
            fixture.fixture(),
        )
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()
        val argument11: Int = fixture.fixture()
        val argument12: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)
        val actualArgument11 = AtomicReference<Int?>(null)
        val actualArgument12 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffect = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10, givenArg11, givenArg12 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)
                actualArgument10.set(givenArg10)
                actualArgument11.set(givenArg11)
                actualArgument12.set(givenArg12)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10,
                argument11,
                argument12,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10
            actualArgument11.get() mustBe argument11
            actualArgument12.get() mustBe argument12

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 13
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
            arguments[11] mustBe argument11
            arguments[12] mustBe argument12
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn41")
    fun `Given invoke is called it calls the given SideEffectChain with 13 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String, Int, String) -> Any>(
            fixture.fixture(),
        )
        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()
        val argument11: Int = fixture.fixture()
        val argument12: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)
        val actualArgument11 = AtomicReference<Int?>(null)
        val actualArgument12 = AtomicReference<String?>(null)

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            proxy.sideEffects.add { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10, givenArg11, givenArg12 ->
                actualArgument0.set(givenArg0)
                actualArgument1.set(givenArg1)
                actualArgument2.set(givenArg2)
                actualArgument3.set(givenArg3)
                actualArgument4.set(givenArg4)
                actualArgument5.set(givenArg5)
                actualArgument6.set(givenArg6)
                actualArgument7.set(givenArg7)
                actualArgument8.set(givenArg8)
                actualArgument9.set(givenArg9)
                actualArgument10.set(givenArg10)
                actualArgument11.set(givenArg11)
                actualArgument12.set(givenArg12)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10,
                argument11,
                argument12,
            )

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10
            actualArgument11.get() mustBe argument11
            actualArgument12.get() mustBe argument12

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 13
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
            arguments[11] mustBe argument11
            arguments[12] mustBe argument12
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn42")
    fun `Given invoke is called it calls the given Spy with 13 Arguments and delegates values threadsafe`(): AsyncTestReturnValue {
        // Given
        val implementation = Implementation<Any>()

        val argument0: String = fixture.fixture()
        val argument1: Int = fixture.fixture()
        val argument2: String = fixture.fixture()
        val argument3: Int = fixture.fixture()
        val argument4: String = fixture.fixture()
        val argument5: Int = fixture.fixture()
        val argument6: String = fixture.fixture()
        val argument7: Int = fixture.fixture()
        val argument8: String = fixture.fixture()
        val argument9: Int = fixture.fixture()
        val argument10: String = fixture.fixture()
        val argument11: Int = fixture.fixture()
        val argument12: String = fixture.fixture()

        val expected: Any = fixture.fixture()

        val actualArgument0 = AtomicReference<String?>(null)
        val actualArgument1 = AtomicReference<Int?>(null)
        val actualArgument2 = AtomicReference<String?>(null)
        val actualArgument3 = AtomicReference<Int?>(null)
        val actualArgument4 = AtomicReference<String?>(null)
        val actualArgument5 = AtomicReference<Int?>(null)
        val actualArgument6 = AtomicReference<String?>(null)
        val actualArgument7 = AtomicReference<Int?>(null)
        val actualArgument8 = AtomicReference<String?>(null)
        val actualArgument9 = AtomicReference<Int?>(null)
        val actualArgument10 = AtomicReference<String?>(null)
        val actualArgument11 = AtomicReference<Int?>(null)
        val actualArgument12 = AtomicReference<String?>(null)

        val proxy = SyncFunProxy<Any, (String, Int, String, Int, String, Int, String, Int, String, Int, String, Int, String) -> Any>(
            fixture.fixture(),
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            implementation.fun13 = { givenArg0, givenArg1, givenArg2, givenArg3, givenArg4, givenArg5, givenArg6, givenArg7, givenArg8, givenArg9, givenArg10, givenArg11, givenArg12 ->
                actualArgument0.set(givenArg0 as String?)
                actualArgument1.set(givenArg1 as Int?)
                actualArgument2.set(givenArg2 as String?)
                actualArgument3.set(givenArg3 as Int?)
                actualArgument4.set(givenArg4 as String?)
                actualArgument5.set(givenArg5 as Int?)
                actualArgument6.set(givenArg6 as String?)
                actualArgument7.set(givenArg7 as Int?)
                actualArgument8.set(givenArg8 as String?)
                actualArgument9.set(givenArg9 as Int?)
                actualArgument10.set(givenArg10 as String?)
                actualArgument11.set(givenArg11 as Int?)
                actualArgument12.set(givenArg12 as String?)

                expected
            }
        }

        runBlockingTestInContext(testScope2.coroutineContext) {
            // When
            val actual = proxy.invoke(
                argument0,
                argument1,
                argument2,
                argument3,
                argument4,
                argument5,
                argument6,
                argument7,
                argument8,
                argument9,
                argument10,
                argument11,
                argument12,
            ) {
                useSpyIf(implementation) {
                    implementation.fun13(
                        argument0,
                        argument1,
                        argument2,
                        argument3,
                        argument4,
                        argument5,
                        argument6,
                        argument7,
                        argument8,
                        argument9,
                        argument10,
                        argument11,
                        argument12,
                    )
                }
            }

            // Then
            actual mustBe expected
            actualArgument0.get() mustBe argument0
            actualArgument1.get() mustBe argument1
            actualArgument2.get() mustBe argument2
            actualArgument3.get() mustBe argument3
            actualArgument4.get() mustBe argument4
            actualArgument5.get() mustBe argument5
            actualArgument6.get() mustBe argument6
            actualArgument7.get() mustBe argument7
            actualArgument8.get() mustBe argument8
            actualArgument9.get() mustBe argument9
            actualArgument10.get() mustBe argument10
            actualArgument11.get() mustBe argument11
            actualArgument12.get() mustBe argument12

            val arguments = proxy.getArgumentsForCall(0)
            arguments.size mustBe 13
            arguments[0] mustBe argument0
            arguments[1] mustBe argument1
            arguments[2] mustBe argument2
            arguments[3] mustBe argument3
            arguments[4] mustBe argument4
            arguments[5] mustBe argument5
            arguments[6] mustBe argument6
            arguments[7] mustBe argument7
            arguments[8] mustBe argument8
            arguments[9] mustBe argument9
            arguments[10] mustBe argument10
            arguments[11] mustBe argument11
            arguments[12] mustBe argument12
        }

        return resolveMultiBlockCalls()
    }

    @JsName("ImplementationFunAsync")
    private class Implementation<T> {
        private val _fun0: AtomicRef<(() -> T)?> = atomic(null)
        private val _fun1: AtomicRef<((Any?) -> T)?> = atomic(null)
        private val _fun2: AtomicRef<((Any?, Any?) -> T)?> = atomic(null)
        private val _fun3: AtomicRef<((Any?, Any?, Any?) -> T)?> = atomic(null)
        private val _fun4: AtomicRef<((Any?, Any?, Any?, Any?) -> T)?> = atomic(null)
        private val _fun5: AtomicRef<((Any?, Any?, Any?, Any?, Any?) -> T)?> = atomic(null)
        private val _fun6: AtomicRef<((Any?, Any?, Any?, Any?, Any?, Any?) -> T)?> = atomic(null)
        private val _fun7: AtomicRef<((Any?, Any?, Any?, Any?, Any?, Any?, Any?) -> T)?> = atomic(null)
        private val _fun8: AtomicRef<((Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?) -> T)?> = atomic(null)
        private val _fun9: AtomicRef<((Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?) -> T)?> = atomic(null)
        private val _fun10: AtomicRef<((Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?) -> T)?> = atomic(null)
        private val _fun11: AtomicRef<((Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?) -> T)?> = atomic(null)
        private val _fun12: AtomicRef<((Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?) -> T)?> = atomic(null)
        private val _fun13: AtomicRef<((Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?, Any?) -> T)?> = atomic(null)

        @JsName("fun0a")
        var fun0 by _fun0

        @JsName("fun1a")
        var fun1 by _fun1

        @JsName("fun2a")
        var fun2 by _fun2

        @JsName("fun3a")
        var fun3 by _fun3

        @JsName("fun4a")
        var fun4 by _fun4

        @JsName("fun5a")
        var fun5 by _fun5

        @JsName("fun6a")
        var fun6 by _fun6

        @JsName("fun7a")
        var fun7 by _fun7

        @JsName("fun8a")
        var fun8 by _fun8

        @JsName("fun9a")
        var fun9 by _fun9

        @JsName("fun10a")
        var fun10 by _fun10

        @JsName("fun11a")
        var fun11 by _fun11

        @JsName("fun12a")
        var fun12 by _fun12

        @JsName("fun13a")
        var fun13 by _fun13

        fun fun0(): T {
            return _fun0.value?.invoke() ?: throw RuntimeException("Missing sideeffect fun0")
        }

        fun fun1(arg0: Any?): T {
            return _fun1.value?.invoke(arg0) ?: throw RuntimeException("Missing sideeffect fun1")
        }

        fun fun2(arg0: Any?, arg1: Any?): T {
            return _fun2.value?.invoke(arg0, arg1) ?: throw RuntimeException("Missing sideeffect fun2")
        }

        fun fun3(arg0: Any?, arg1: Any?, arg2: Any?): T {
            return _fun3.value?.invoke(arg0, arg1, arg2) ?: throw RuntimeException("Missing sideeffect fun3")
        }

        fun fun4(arg0: Any?, arg1: Any?, arg2: Any?, arg3: Any?): T {
            return _fun4.value?.invoke(arg0, arg1, arg2, arg3) ?: throw RuntimeException("Missing sideeffect fun4")
        }

        fun fun5(arg0: Any?, arg1: Any?, arg2: Any?, arg3: Any?, arg4: Any?): T {
            return _fun5.value?.invoke(arg0, arg1, arg2, arg3, arg4) ?: throw RuntimeException("Missing sideeffect fun5")
        }

        fun fun6(arg0: Any?, arg1: Any?, arg2: Any?, arg3: Any?, arg4: Any?, arg5: Any?): T {
            return _fun6.value?.invoke(arg0, arg1, arg2, arg3, arg4, arg5) ?: throw RuntimeException("Missing sideeffect fun6")
        }

        fun fun7(arg0: Any?, arg1: Any?, arg2: Any?, arg3: Any?, arg4: Any?, arg5: Any?, arg6: Any?): T {
            return _fun7.value?.invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
                ?: throw RuntimeException("Missing sideeffect fun7")
        }

        fun fun8(
            arg0: Any?,
            arg1: Any?,
            arg2: Any?,
            arg3: Any?,
            arg4: Any?,
            arg5: Any?,
            arg6: Any?,
            arg7: Any?,
        ): T {
            return _fun8.value?.invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
                ?: throw RuntimeException("Missing sideeffect fun8")
        }

        fun fun9(
            arg0: Any?,
            arg1: Any?,
            arg2: Any?,
            arg3: Any?,
            arg4: Any?,
            arg5: Any?,
            arg6: Any?,
            arg7: Any?,
            arg8: Any?,
        ): T {
            return _fun9.value?.invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
                ?: throw RuntimeException("Missing sideeffect fun9")
        }

        fun fun10(
            arg0: Any?,
            arg1: Any?,
            arg2: Any?,
            arg3: Any?,
            arg4: Any?,
            arg5: Any?,
            arg6: Any?,
            arg7: Any?,
            arg8: Any?,
            arg9: Any?,
        ): T {
            return _fun10.value?.invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
                ?: throw RuntimeException("Missing sideeffect fun10")
        }

        fun fun11(
            arg0: Any?,
            arg1: Any?,
            arg2: Any?,
            arg3: Any?,
            arg4: Any?,
            arg5: Any?,
            arg6: Any?,
            arg7: Any?,
            arg8: Any?,
            arg9: Any?,
            arg10: Any?,
        ): T {
            return _fun11.value?.invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)
                ?: throw RuntimeException("Missing sideeffect fun11")
        }

        fun fun12(
            arg0: Any?,
            arg1: Any?,
            arg2: Any?,
            arg3: Any?,
            arg4: Any?,
            arg5: Any?,
            arg6: Any?,
            arg7: Any?,
            arg8: Any?,
            arg9: Any?,
            arg10: Any?,
            arg11: Any?,
        ): T {
            return _fun12.value?.invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11)
                ?: throw RuntimeException("Missing sideeffect fun12")
        }

        fun fun13(
            arg0: Any?,
            arg1: Any?,
            arg2: Any?,
            arg3: Any?,
            arg4: Any?,
            arg5: Any?,
            arg6: Any?,
            arg7: Any?,
            arg8: Any?,
            arg9: Any?,
            arg10: Any?,
            arg11: Any?,
            arg12: Any?,
        ): T {
            return _fun13.value?.invoke(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12)
                ?: throw RuntimeException("Missing sideeffect fun13")
        }
    }
}
