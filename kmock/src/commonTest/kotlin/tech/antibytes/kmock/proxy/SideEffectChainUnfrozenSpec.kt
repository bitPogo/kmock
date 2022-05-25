/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.util.test.coroutine.clearBlockingTest
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
    fun `Given add is called it addAll a SideEffect and notifies its host`() {
        // Given
        var notifications = 0
        val chain = SideEffectChain<Unit, () -> Unit>(false) {
            notifications += 1
        }

        // When
        chain.addAll(
            listOf(
                { /* Do nothing */ },
                { /* Do nothing */ }
            )
        )

        // Then
        notifications mustBe 1
    }

    @Test
    @JsName("fn3")
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
    @JsName("fn4")
    fun `Given next is called it returns the SideEffects in the order they were stored via add`() {
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
    @JsName("fn5")
    fun `Given next is called it returns the SideEffects in the order they were stored via addAll`() {
        // Given
        val chain = SideEffectChain<Unit, () -> Unit>(false) { }
        val sideEffects: MutableList<() -> Unit> = mutableListOf(
            {},
            {},
            {},
            {}
        )

        // When
        chain.addAll(sideEffects)

        // Then
        sideEffects.forEach { sideEffect ->
            chain.next() mustBe sideEffect
        }
    }

    @Test
    @JsName("fn6")
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

        repeat(10) {
            chain.next() mustBe sideEffects.last()
        }
    }

    @Test
    @JsName("fn7")
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
