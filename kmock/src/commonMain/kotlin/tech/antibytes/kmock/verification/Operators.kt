/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.Expectation
import tech.antibytes.kmock.KMockExperimental

private fun guardInvocation(
    handle1: Expectation,
    handle2: Expectation,
    method: String,
) {
    if (handle1.proxy !== handle2.proxy) {
        throw IllegalArgumentException("$method cannot be applied to handles which refer to different proxies.")
    }
}

/**
 * Operator to determine the union of 2 VerificationHandles call indices.
 * Both handles must be refer to same Proxy.
 * @param other 2nd handle.
 * @throws IllegalArgumentException if the 2nd handle does not refer to the same proxy.
 * @return VerificationHandle which contains the union of both given call indices.
 * @see Expectation
 * @author Matthias Geisler
 */
@KMockExperimental
public infix fun Expectation.union(
    other: Expectation,
): Expectation {
    guardInvocation(this, other, "union")

    val multiSet = this.callIndices.toMutableSet()
    multiSet.addAll(other.callIndices)

    return Expectation(
        this.proxy,
        multiSet.sorted(),
    )
}

/**
 * Alias of union
 * @see union
 * @author Matthias Geisler
 */
@KMockExperimental
public infix fun Expectation.or(
    other: Expectation,
): Expectation = this.union(other)

/**
 * Operator to determine the intersection of 2 VerificationHandles call indices.
 * Both handles must be refer to same Proxy.
 * @param other 2nd handle.
 * @throws IllegalArgumentException if the 2nd handle does not refer to the same proxy.
 * @return VerificationHandle which contains the intersection of both given call indices.
 * @see Expectation
 * @author Matthias Geisler
 */
@KMockExperimental
public infix fun Expectation.intersection(
    other: Expectation,
): Expectation {
    guardInvocation(this, other, "intersection")

    val set = this.callIndices
        .filter { value -> value in other.callIndices }
        .sorted()

    return Expectation(
        this.proxy,
        set,
    )
}

/**
 * Alias of intersect
 * @see intersection
 * @author Matthias Geisler
 */
@KMockExperimental
public infix fun Expectation.and(
    other: Expectation,
): Expectation = this.intersection(other)

/**
 * Operator to determine the symmetrical difference of 2 VerificationHandles call indices.
 * Both handles must be refer to same Proxy.
 * @param other 2nd handle.
 * @throws IllegalArgumentException if the 2nd handle does not refer to the same proxy.
 * @return VerificationHandle which contains the symmetrical difference of both given call indices.
 * @see Expectation
 * @author Matthias Geisler
 */
@KMockExperimental
public infix fun Expectation.diff(
    other: Expectation,
): Expectation {
    guardInvocation(this, other, "diff")
    val intersection = this.intersection(other)

    val set = this.callIndices
        .toMutableSet()
        .also { it.addAll(other.callIndices) }
        .filterNot { value -> value in intersection.callIndices }
        .sorted()

    return Expectation(
        this.proxy,
        set,
    )
}

/**
 * Alias of diff
 * @see diff
 * @author Matthias Geisler
 */
@KMockExperimental
public infix fun Expectation.xor(
    other: Expectation,
): Expectation = this.diff(other)
