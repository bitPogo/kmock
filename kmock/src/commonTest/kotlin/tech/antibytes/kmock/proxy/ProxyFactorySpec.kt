/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.SyncFunProxy
import tech.antibytes.kmock.KMockContract.AsyncFunProxy
import tech.antibytes.util.test.fixture.fixture
import tech.antibytes.util.test.fixture.kotlinFixture
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.js.JsName
import kotlin.test.Test

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
    fun `Given createSyncFunProxy, it creates a SyncFunProxy`() {
        // When
        val proxy = ProxyFactory.createSyncFunProxy<Any, (Any, Any) -> Any>(
            id = fixture.fixture()
        )

        // Then
        (proxy is SyncFunProxy<Any, (Any, Any) -> Any>) mustBe true
    }

    @Test
    @JsName("fn2")
    @Suppress("USELESS_IS_CHECK")
    fun `Given createAsyncFunProxy, it creates a AsyncFunProxy`() {
        // When
        val proxy = ProxyFactory.createAsyncFunProxy<Any, suspend (Any, Any) -> Any>(
            id = fixture.fixture()
        )

        // Then
        (proxy is AsyncFunProxy<Any, suspend (Any, Any) -> Any>) mustBe true
    }

    @Test
    @JsName("fn3")
    fun `Given createPropertyProxy, it creates a PropertyProxy`() {
        // When
        val proxy: KMockContract.Proxy<Int, KMockContract.GetOrSet> = ProxyFactory.createPropertyProxy(
            id = fixture.fixture()
        )

        // Then
        (proxy is KMockContract.PropertyProxy<Int>) mustBe true
    }
}
