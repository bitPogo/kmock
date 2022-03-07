/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.mock.FunProxyStub
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.math.absoluteValue
import kotlin.test.Test

class NonFreezingVerifierSpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils Verifier`() {
        NonfreezingVerifier() fulfils KMockContract.Verifier::class
    }

    @Test
    @JsName("fn1")
    fun `It fulfils Collector`() {
        NonfreezingVerifier() fulfils KMockContract.Collector::class
    }

    @Test
    @JsName("fn2")
    fun `It has a emptyMap of references by default`() {
        NonfreezingVerifier().references mustBe emptyList()
    }

    @Test
    @JsName("fn3")
    fun `Given add reference is called it adds a refrenence entry`() {
        // Given
        val index: Int = fixture.fixture<Int>().absoluteValue
        val Proxy = FunProxyStub(fixture.fixture(), fixture.fixture())

        val verifier = NonfreezingVerifier()

        // When
        verifier.addReference(Proxy, index)

        // Then
        verifier.references.first().Proxy sameAs Proxy
        verifier.references.first().callIndex mustBe index
    }

    @Test
    @JsName("fn4")
    fun `Given clear is called it clears the verifier`() {
        // Given
        val Proxy = FunProxyStub(fixture.fixture(), fixture.fixture())
        val verifier = NonfreezingVerifier()

        // When
        verifier.addReference(Proxy, fixture.fixture())

        verifier.clear()

        // Then
        verifier.references mustBe emptyList()
    }
}
