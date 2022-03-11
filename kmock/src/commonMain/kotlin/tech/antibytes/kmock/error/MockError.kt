/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.error

/**
 * Base error class.
 * @param message the given error message.
 * @author Matthias Geisler
 */
sealed class MockError(
    message: String
) : RuntimeException(message) {
    /**
     * Indicates that a Proxy is missing a defined behaviour.
     * @param message the given error message.
     * @author Matthias Geisler
     */
    class MissingStub(message: String) : MockError(message)

    /**
     * Indicates that a Proxy was not called.
     * @param message the given error message
     * @author Matthias Geisler
     */
    class MissingCall(message: String) : MockError(message)
}
