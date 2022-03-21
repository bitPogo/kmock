/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.verification

import tech.antibytes.kmock.KMockContract.VerificationHandle

private fun guardInvocation(
    handle1: VerificationHandle,
    handle2: VerificationHandle,
    method: String
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
 * @see VerificationHandle
 * @author Matthias Geisler
 */
infix fun VerificationHandle.union(
    other: VerificationHandle
): VerificationHandle {
    guardInvocation(this, other, "union")

    val multiSet = this.callIndices.toMutableSet()
    multiSet.addAll(other.callIndices)

    return VerificationHandle(
        this.proxy,
        multiSet.sorted()
    )
}

/**
 * Alias of union
 * @see union
 * @author Matthias Geisler
 */
infix fun VerificationHandle.or(
    other: VerificationHandle
): VerificationHandle = this.union(other)

/**
 * Operator to determine the intersection of 2 VerificationHandles call indices.
 * Both handles must be refer to same Proxy.
 * @param other 2nd handle.
 * @throws IllegalArgumentException if the 2nd handle does not refer to the same proxy.
 * @return VerificationHandle which contains the intersection of both given call indices.
 * @see VerificationHandle
 * @author Matthias Geisler
 */
infix fun VerificationHandle.intersection(
    other: VerificationHandle
): VerificationHandle {
    guardInvocation(this, other, "intersection")

    val set = this.callIndices
        .filter { value -> value in other.callIndices }
        .sorted()

    return VerificationHandle(
        this.proxy,
        set
    )
}

/**
 * Alias of intersect
 * @see intersection
 * @author Matthias Geisler
 */
infix fun VerificationHandle.and(
    other: VerificationHandle
): VerificationHandle = this.intersection(other)

/**
 * Operator to determine the symmetrical difference of 2 VerificationHandles call indices.
 * Both handles must be refer to same Proxy.
 * @param other 2nd handle.
 * @throws IllegalArgumentException if the 2nd handle does not refer to the same proxy.
 * @return VerificationHandle which contains the symmetrical difference of both given call indices.
 * @see VerificationHandle
 * @author Matthias Geisler
 */
infix fun VerificationHandle.diff(
    other: VerificationHandle
): VerificationHandle {
    guardInvocation(this, other, "diff")
    val intersection = this.intersection(other)

    val set = this.callIndices
        .toMutableSet()
        .also { it.addAll(other.callIndices) }
        .filterNot { value -> value in intersection.callIndices }
        .sorted()

    return VerificationHandle(
        this.proxy,
        set
    )
}

/**
 * Alias of diff
 * @see diff
 * @author Matthias Geisler
 */
infix fun VerificationHandle.xor(
    other: VerificationHandle
): VerificationHandle = this.diff(other)
