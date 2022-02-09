/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.mock.FunMockeryStub
import tech.antibytes.util.test.coroutine.TestScopeDispatcher
import tech.antibytes.util.test.coroutine.runBlockingTestInContext
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.math.absoluteValue
import kotlin.test.Test

class VerifierSpec {
    private val fixture = kotlinFixture()
    private val testScope1 = TestScopeDispatcher.dispatch("test1")
    private val testScope2 = TestScopeDispatcher.dispatch("test2")

    @Test
    @JsName("fn0")
    fun `It fulfils Verifier`() {
        Verifier() fulfils KMockContract.Verifier::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils Collector`() {
        Verifier() fulfils KMockContract.Collector::class
    }

    @Test
    @JsName("fn2")
    fun `It has a emptyMap of references by default`() {
        Verifier().references mustBe emptyList()
    }

    @Test
    @JsName("fn3")
    fun `Given add reference is called it adds a refrenence entry threadsafe`() {
        // Given
        val index: Int = fixture.fixture<Int>().absoluteValue
        val mockery = FunMockeryStub(fixture.fixture(), fixture.fixture())

        val verifier = Verifier()

        // When
        runBlockingTestInContext(testScope1.coroutineContext) {
            verifier.addReference(mockery, index)
        }

        // Then
        runBlockingTestInContext(testScope2.coroutineContext) {
            verifier.references.first().mockery sameAs mockery
            verifier.references.first().callIndex mustBe index
        }
    }
}
