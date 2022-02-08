/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

infix fun KMockContract.VerificationHandle.union(
    other: KMockContract.VerificationHandle
): KMockContract.VerificationHandle {
    val multiSet = this.callIndices.toMutableSet()
    multiSet.addAll(other.callIndices)

    return VerificationHandle(
        this.id,
        multiSet.sorted()
    )
}

infix fun KMockContract.VerificationHandle.and(
    other: KMockContract.VerificationHandle
): KMockContract.VerificationHandle = this.union(other)


infix fun KMockContract.VerificationHandle.intersect(
    other: KMockContract.VerificationHandle
): KMockContract.VerificationHandle {
    val set = this.callIndices
        .filter { value -> other.callIndices.contains(value) }
        .sorted()

    return VerificationHandle(
        this.id,
        set
    )
}

infix fun KMockContract.VerificationHandle.or(
    other: KMockContract.VerificationHandle
): KMockContract.VerificationHandle = this.intersect(other)

infix fun KMockContract.VerificationHandle.diff(
    other: KMockContract.VerificationHandle
): KMockContract.VerificationHandle {
    val set = this.callIndices
        .toMutableSet()
        .also { it.addAll(other.callIndices) }
        .filterNot { value -> this.callIndices.contains(value) && other.callIndices.contains(value) }
        .sorted()

    return VerificationHandle(
        this.id,
        set
    )
}

infix fun KMockContract.VerificationHandle.xor(
    other: KMockContract.VerificationHandle
): KMockContract.VerificationHandle = this.diff(other)
