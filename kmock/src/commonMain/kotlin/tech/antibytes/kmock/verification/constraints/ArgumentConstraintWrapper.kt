/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification.constraints

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockContract.ArgumentConstraint

internal object ArgumentConstraintWrapper : KMockContract.ArgumentConstraintWrapper {
    override fun wrapValue(value: Any?): ArgumentConstraint {
        return if (value is ArgumentConstraint) {
            value
        } else {
            eq(value)
        }
    }

    override fun wrapNegatedValue(value: Any?): ArgumentConstraint {
        val wrappedValue = wrapValue(value)
        return not(wrappedValue)
    }
}
