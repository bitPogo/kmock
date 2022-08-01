/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import kotlin.js.JsName
import kotlin.test.Test
import tech.antibytes.kmock.KMockContract
import tech.antibytes.mock.SyncFunProxyStub
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class NoopCollectorSpec {
    @Test
    @JsName("fn0")
    fun `It fulfils Collector`() {
        NoopCollector fulfils KMockContract.Collector::class
    }

    @Test
    @JsName("fn1")
    fun `addReference is called with a Proxy and call index it does nothing`() {
        val actual = NoopCollector.addReference(
            SyncFunProxyStub("", -23),
            23,
        )

        actual mustBe Unit
    }
}
