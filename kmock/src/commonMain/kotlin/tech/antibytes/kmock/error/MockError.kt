/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.error

sealed class MockError(
    message: String
) : RuntimeException(message) {
    class MissingStub(message: String) : MockError(message)
}
