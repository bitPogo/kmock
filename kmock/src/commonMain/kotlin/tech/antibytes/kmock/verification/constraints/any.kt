/* ktlint-disable filename */
/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ClassName")

package tech.antibytes.kmock.verification.constraints

import kotlin.reflect.KClass
import tech.antibytes.kmock.KMockContract.ArgumentConstraint
import tech.antibytes.kmock.util.extractKClassName

/**
 * VerificationConstraint which allows any value including null.
 * @param expected KClass of the expected value. If set the matcher is restricted to the given class type (excluding null).
 * If null any value (type) is accepted. Default is null.
 * @property expected KClass of the expected value. If set the matcher is restricted to the given class type (excluding null).
 * If null any value (type) is accepted. Default is null.
 * @see ArgumentConstraint
 * @author Matthias Geisler
 */
public class any(
    private val expected: KClass<*>? = null,
) : ArgumentConstraint {
    override fun matches(actual: Any?): Boolean {
        return when {
            expected == null -> true
            actual == null -> false
            else -> actual::class == expected
        }
    }

    override fun toString(): String {
        return if (expected == null) {
            "(Any value)"
        } else {
            "(Any value of ${extractKClassName(expected)})"
        }
    }
}
