/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.collections.IsoMutableList
import co.touchlab.stately.collections.sharedMutableListOf
import co.touchlab.stately.concurrency.AtomicReference
import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.TestScopeDispatcher
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.resolveMultiBlockCalls
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SideEffectChainSpec {
    private val testScope1 = TestScopeDispatcher.dispatch("test1")
    private val testScope2 = TestScopeDispatcher.dispatch("test2")

    @BeforeTest
    fun setUp() {
        clearBlockingTest()
    }

    @Test
    @JsName("fn0")
    fun `It fulfils SideEffectChain`() {
        SideEffectChain<Unit, Function<Unit>>(true) { } fulfils KMockContract.SideEffectChain::class
    }

    @Test
    @JsName("fn1")
    fun `Given add is called it adds a SideEffect and notifies its host threadsafe`(): AsyncTestReturnValue {
        // Given
        val notifications = AtomicReference(0)
        val chain = SideEffectChain<Unit, () -> Unit>(true) {
            val invocations = notifications.get()
            notifications.set(invocations + 1)
        }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            chain.add { /* Do nothing */ }
                .add { /* Do nothing */ }
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            notifications.get() mustBe 2
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn2")
    fun `Given addAll is called it adds a SideEffect and notifies its host threadsafe`(): AsyncTestReturnValue {
        // Given
        val notifications = AtomicReference(0)
        val chain = SideEffectChain<Unit, () -> Unit>(true) {
            val invocations = notifications.get()
            notifications.set(invocations + 1)
        }

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            chain.addAll(
                listOf(
                    { /* Do nothing */ },
                    { /* Do nothing */ }
                )
            )
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            notifications.get() mustBe 1
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn3")
    fun `Given next is called it fails if no SideEffect was stored`() {
        // Given
        val chain = SideEffectChain<Unit, () -> Unit>(true) { }

        // Then
        val error = assertFailsWith<IllegalStateException> {
            // When
            chain.next()
        }

        error.message mustBe "No SideEffect was stored."
    }

    @Test
    @JsName("fn4")
    fun `Given next is called it returns the SideEffects in the order they were stored via add threadsafe`(): AsyncTestReturnValue {
        // Given
        val chain = SideEffectChain<Unit, () -> Unit>(true) { }
        val sideEffects: IsoMutableList<() -> Unit> = sharedMutableListOf(
            {},
            {},
            {},
            {}
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            sideEffects.forEach { sideEffect ->
                chain.add(sideEffect)
            }
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            sideEffects.forEach { sideEffect ->
                chain.next() mustBe sideEffect
            }
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn5")
    fun `Given next is called it returns the SideEffects in the order they were stored via addAll threadsafe`(): AsyncTestReturnValue {
        // Given
        val chain = SideEffectChain<Unit, () -> Unit>(true) { }
        val sideEffects: IsoMutableList<() -> Unit> = sharedMutableListOf(
            {},
            {},
            {},
            {}
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            chain.addAll(sideEffects)
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            sideEffects.forEach { sideEffect ->
                chain.next() mustBe sideEffect
            }
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn6")
    fun `Given next is called it returns the SideEffects in the order they while repeating the last one indefinitely if the invocation exceed the size of the chain`(): AsyncTestReturnValue {
        // Given
        val chain = SideEffectChain<Unit, () -> Unit>(true) { }
        val sideEffects: IsoMutableList<() -> Unit> = sharedMutableListOf(
            {},
            {},
        )

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            sideEffects.forEach { sideEffect ->
                chain.add(sideEffect)
            }
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            sideEffects.forEach { sideEffect ->
                chain.next() mustBe sideEffect
            }

            for (x in 0..10) {
                chain.next() mustBe sideEffects.last()
            }
        }

        return resolveMultiBlockCalls()
    }

    @Test
    @JsName("fn7")
    fun `Given clear is called it purges the chain of any values`() {
        // Given
        val chain = SideEffectChain<Unit, () -> Unit>(true) { }

        runBlockingTestInContext(testScope1.coroutineContext) {
            chain.add { /* Do nothing */ }
        }

        // Then
        runBlockingTestInContext(testScope1.coroutineContext) {
            val error = assertFailsWith<IllegalStateException> {
                // When
                chain.clear()

                chain.next()
            }

            error.message mustBe "No SideEffect was stored."
        }
    }
}
