/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.AsyncFunProxy
import tech.antibytes.kmock.KMockContract.Collector
import tech.antibytes.kmock.KMockContract.PropertyProxy
import tech.antibytes.kmock.KMockContract.Proxy
import tech.antibytes.kmock.KMockContract.SyncFunProxy
import tech.antibytes.util.test.annotations.NativeOnly
import tech.antibytes.util.test.coroutine.AsyncTestReturnValue
import tech.antibytes.util.test.coroutine.runBlockingTest
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import tech.antibytes.util.test.sameAs
import kotlin.js.JsName
import kotlin.test.Test

// NOTE: This is a entry point and the reason for its integration character
class ProxyFactorySpec {
    private val fixture = kotlinFixture()

    @Test
    @JsName("fn0")
    fun `It fulfils ProxyFactory`() {
        ProxyFactory fulfils KMockContract.ProxyFactory::class
    }

    @Test
    @JsName("fn1")
    @Suppress("USELESS_IS_CHECK")
    fun `Given createSyncFunProxy it creates a SyncFunProxy`() {
        // When
        val proxy = ProxyFactory.createSyncFunProxy<Any, (Any, Any) -> Any>(
            id = fixture.fixture()
        )

        // Then
        (proxy is SyncFunProxy<Any, (Any, Any) -> Any>) mustBe true
    }

    @Test
    @JsName("fn2")
    fun `Given createSyncFunProxy it creates a SyncFunProxy while using a Collector`() {
        // Given
        var capturedProxy: Proxy<*, *>? = null
        val collector = Collector { proxy, _ ->
            capturedProxy = proxy
        }

        // When
        val proxy = ProxyFactory.createSyncFunProxy<Any, (Any, Any) -> Any>(
            id = fixture.fixture(),
            collector = collector,
        )

        proxy.returnValue = fixture.fixture()

        proxy.invoke<Any, Any>(fixture.fixture(), fixture.fixture())

        // Then
        capturedProxy sameAs proxy
    }

    @Test
    @JsName("fn3")
    @NativeOnly
    fun `Given createSyncFunProxy it creates a SyncFunProxy while setting freeze`(): AsyncTestReturnValue {
        // Given
        val freeze = true

        // When
        val proxy = ProxyFactory.createSyncFunProxy<Any, (Any, Any) -> Any>(
            id = fixture.fixture(),
            freeze = freeze,
        )

        return runBlockingTest {
            proxy.returnValue = fixture.fixture()

            // Then
            proxy.invoke<Any, Any>(fixture.fixture(), fixture.fixture()) // just runs
        }
    }

    @Test
    @JsName("fn4")
    fun `Given createSyncFunProxy it creates a SyncFunProxy while override ignorableForVerification`() {
        // Given
        val ignorable: Boolean = fixture.fixture()

        // When
        val proxy = ProxyFactory.createSyncFunProxy<Any, (Any, Any) -> Any>(
            id = fixture.fixture(),
            ignorableForVerification = ignorable,
        )

        // Then
        proxy.ignorableForVerification mustBe ignorable
    }

    @Test
    @JsName("fn6")
    @Suppress("USELESS_IS_CHECK")
    fun `Given createAsyncFunProxy it creates a AsyncFunProxy`() {
        // When
        val proxy = ProxyFactory.createAsyncFunProxy<Any, suspend (Any, Any) -> Any>(
            id = fixture.fixture()
        )

        // Then
        (proxy is AsyncFunProxy<Any, suspend (Any, Any) -> Any>) mustBe true
    }

    @Test
    @JsName("fn7")
    fun `Given createAsyncFunProxy it creates a AsyncFunProxy while using a Collector`() = runBlockingTest {
        // Given
        var capturedProxy: Proxy<*, *>? = null
        val collector = Collector { proxy, _ ->
            capturedProxy = proxy
        }

        // When
        val proxy = ProxyFactory.createAsyncFunProxy<Any, suspend (Any, Any) -> Any>(
            id = fixture.fixture(),
            collector = collector,
        )

        proxy.returnValue = fixture.fixture()

        proxy.invoke<Any, Any>(fixture.fixture(), fixture.fixture())

        // Then
        capturedProxy sameAs proxy
    }

    @Test
    @JsName("fn8")
    @NativeOnly
    fun `Given createAsyncFunProxy it creates a AsyncFunProxy while setting freeze`(): AsyncTestReturnValue {
        // Given
        val freeze = true

        // When
        val proxy = ProxyFactory.createAsyncFunProxy<Any, suspend (Any, Any) -> Any>(
            id = fixture.fixture(),
            freeze = freeze,
        )

        return runBlockingTest {
            proxy.returnValue = fixture.fixture()

            // Then
            proxy.invoke<Any, Any>(fixture.fixture(), fixture.fixture()) // just runs
        }
    }

    @Test
    @JsName("fn9")
    fun `Given createAsyncFunProxy it creates a AsyncFunProxy while ignoring ignorableForVerification`() {
        // Given
        val ignorable = true

        // When
        val proxy = ProxyFactory.createAsyncFunProxy<Any, suspend (Any, Any) -> Any>(
            id = fixture.fixture(),
            ignorableForVerification = ignorable,
        )

        // Then
        proxy.ignorableForVerification mustBe false
    }

    @Test
    @JsName("fn11")
    fun `Given createPropertyProxy it creates a PropertyProxy`() {
        // When
        val proxy: Proxy<Int, KMockContract.GetOrSet> = ProxyFactory.createPropertyProxy(
            id = fixture.fixture()
        )

        // Then
        (proxy is PropertyProxy<Int>) mustBe true
    }

    @Test
    @JsName("fn12")
    fun `Given createPropertyProxy it creates a PropertyProxy while using a Collector`() {
        // Given
        var capturedProxy: Proxy<*, *>? = null
        val collector = Collector { proxy, _ ->
            capturedProxy = proxy
        }

        // When
        val proxy: PropertyProxy<Int> = ProxyFactory.createPropertyProxy(
            id = fixture.fixture(),
            collector = collector,
        )

        // Then
        proxy.getValue = fixture.fixture()
        proxy.executeOnGet()

        // Then
        capturedProxy sameAs proxy
    }

    @Test
    @JsName("fn13")
    @NativeOnly
    fun `Given createPropertyProxy it creates a PropertyProxy while setting freeze`(): AsyncTestReturnValue {
        // Given
        val freeze = true

        // When
        val proxy: PropertyProxy<Int> = ProxyFactory.createPropertyProxy(
            id = fixture.fixture(),
            freeze = freeze,
        )

        return runBlockingTest {
            proxy.getValue = fixture.fixture()

            // Then
            proxy.executeOnGet() // just runs
        }
    }
}
