/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract
import tech.antibytes.mock.AsyncFunProxyStub
import tech.antibytes.mock.PropertyProxyStub
import tech.antibytes.mock.SyncFunProxyStub
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
    fun `Given addReference is called it adds a refrenence entry threadsafe`() {
        // Given
        val index: Int = fixture.fixture<Int>().absoluteValue
        val proxy = SyncFunProxyStub(fixture.fixture(), fixture.fixture())

        val verifier = NonfreezingVerifier()

        // When
        verifier.addReference(proxy, index)

        // Then
        verifier.references.first().Proxy sameAs proxy
        verifier.references.first().callIndex mustBe index
    }

    @Test
    @JsName("fn4")
    fun `Given addReference is called it always adds PropertyProxies`() {
        // Given
        val proxy = PropertyProxyStub(fixture.fixture(), fixture.fixture())

        val verifier = NonfreezingVerifier()

        // When
        verifier.addReference(proxy, fixture.fixture())

        // Then
        verifier.references.first().Proxy sameAs proxy
    }

    @Test
    @JsName("fn5")
    fun `Given addReference is called it always adds AsyncFunProxies`() {
        // Given
        val proxy = AsyncFunProxyStub(fixture.fixture(), fixture.fixture())

        val verifier = NonfreezingVerifier()

        // When
        verifier.addReference(proxy, fixture.fixture())

        // Then
        verifier.references.first().Proxy sameAs proxy
    }

    @Test
    @JsName("fn6")
    fun `Given addReference is called it adds SyncFunProxies if they are not marked for ignoring`() {
        // Given
        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            fixture.fixture()
        )

        val verifier = NonfreezingVerifier()

        // When
        verifier.addReference(proxy, fixture.fixture())

        // Then
        verifier.references.first().Proxy sameAs proxy
    }

    @Test
    @JsName("fn7")
    fun `Given addReference is called it ignores SyncFunProxies if they are marked for ignoring`() {
        // Given
        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            fixture.fixture(),
            ignorableForVerification = true
        )

        val verifier = NonfreezingVerifier()

        // When
        verifier.addReference(proxy, fixture.fixture())

        // Then
        verifier.references.firstOrNull() mustBe null
    }

    @Test
    @JsName("fn8")
    fun `Given addReference is called it adds SyncFunProxies if they are marked for ignoring but are overruled by the Verifier`() {
        // Given
        val proxy = SyncFunProxyStub(
            fixture.fixture(),
            fixture.fixture(),
            ignorableForVerification = true
        )

        val verifier = NonfreezingVerifier(coverAllInvocations = true)

        // When
        verifier.addReference(proxy, fixture.fixture())

        // Then
        verifier.references.first().Proxy mustBe proxy
    }

    @Test
    @JsName("fn9")
    fun `Given clear is called it clears the verifier`() {
        // Given
        val proxy = SyncFunProxyStub(fixture.fixture(), fixture.fixture())
        val verifier = NonfreezingVerifier()

        // When
        verifier.addReference(proxy, fixture.fixture())

        verifier.clear()

        // Then
        verifier.references mustBe emptyList()
    }
}
