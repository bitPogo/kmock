/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration.contract

interface ExampleContractJs {
    interface JsDecoder {
        fun createJsDecoder(): dynamic
        fun createJsDecoder(seed: Int): dynamic
    }
}
