/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import co.touchlab.stately.collections.IsoMutableList
import co.touchlab.stately.collections.sharedMutableListOf
import co.touchlab.stately.concurrency.AtomicReference
import kotlinx.coroutines.yield
import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.TestScopeDispatcher
import tech.antibytes.util.test.coroutine.clearBlockingTest
import tech.antibytes.util.test.coroutine.resolveMultiBlockCalls
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SideEffectChainUnfrozenSpec {
    @BeforeTest
    fun setUp() {
        clearBlockingTest()
    }

    @Test
    @JsName("fn0")
    fun `It fulfils SideEffectChain`() {
        SideEffectChain<Unit, Function<Unit>>(false) { } fulfils KMockContract.SideEffectChain::class
    }

    @Test
    @JsName("fn1")
    fun `Given add is called it adds a SideEffect and notifies its host`() {
        // Given
        var notifications = 0
        val chain = SideEffectChain<Unit, () -> Unit>(false) {
            notifications += 1
        }

        // When
        chain.add { /* Do nothing */ }.add { /* Do nothing */ }

        // Then
        notifications mustBe 2
    }

    @Test
    @JsName("fn2")
    fun `Given next is called it fails if no SideEffect was stored`() {
        // Given
        val chain = SideEffectChain<Unit, () -> Unit>(false) { }

        // Then
        val error = assertFailsWith<IllegalStateException> {
            // When
            chain.next()
        }

        error.message mustBe "No SideEffect was stored."
    }

    @Test
    @JsName("fn3")
    fun `Given next is called it returns the SideEffects in the order they were stored`() {
        // Given
        val chain = SideEffectChain<Unit, () -> Unit>(false) { }
        val sideEffects: MutableList<() -> Unit> = mutableListOf(
            {},
            {},
            {},
            {}
        )

        // When
        sideEffects.forEach { sideEffect ->
            chain.add(sideEffect)
        }

        // Then
        sideEffects.forEach { sideEffect ->
            chain.next() mustBe sideEffect
        }
    }

    @Test
    @JsName("fn4")
    fun `Given next is called it returns the SideEffects in the order they while repeating the last one indefinitely if the invocation exceed the size of the chain`() {
        // Given
        val chain = SideEffectChain<Unit, () -> Unit>(false) { }
        val sideEffects: MutableList<() -> Unit> = mutableListOf(
            {},
            {},
        )

        // When
        sideEffects.forEach { sideEffect ->
            chain.add(sideEffect)
        }

        // Then
        sideEffects.forEach { sideEffect ->
            chain.next() mustBe sideEffect
        }

        for (x in 0..10) {
            chain.next() mustBe sideEffects.last()
        }
    }

    @Test
    @JsName("fn5")
    fun `Given clear is called it purges the chain of any values`() {
        // Given
        val chain = SideEffectChain<Unit, () -> Unit>(false) { }

        // Then
        val error = assertFailsWith<IllegalStateException> {
            // When
            chain.add { /* Do nothing */ }

            chain.clear()

            chain.next()
        }

        error.message mustBe "No SideEffect was stored."
    }
}
