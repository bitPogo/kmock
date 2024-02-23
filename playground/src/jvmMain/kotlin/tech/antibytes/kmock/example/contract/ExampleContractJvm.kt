/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.example.contract

import java.util.Base64

interface ExampleContractJvm {
    interface JvmDecoder {
        fun createJvmDecoder(): Base64
    }
}
