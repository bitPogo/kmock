/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockContract
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.error.MockError.MissingCall

/**
 * Retrieves arguments of a call at a given index for a given type of FunProxy.
 * @param T the type to look out for.
 * @param callIndex the index to look at.
 * @throws MissingCall if there is no call recorded at given index
 * @return List of T
 */
@KMockExperimental
public inline fun <reified T : Any> KMockContract.FunProxy<*, *>.getArgumentsByType(
    callIndex: Int
): List<T> = this.getArgumentsForCall(callIndex).filterIsInstance<T>()

/**
 * Retrieves arguments for given type of FunProxy.
 * @param T the type to look out for.
 * @throws MissingCall if there is no call recorded at given index
 * @return List of T
 */
@KMockExperimental
public inline fun <reified T : Any> KMockContract.FunProxy<*, *>.getAllArgumentsByType(): List<T> {
    val aggregated: MutableList<T> = mutableListOf()

    for (idx in 0 until this.calls) {
        this.getArgumentsForCall(idx).filterIsInstance<T>().also { arguments ->
            aggregated.addAll(arguments)
        }
    }

    return aggregated
}

/**
 * Retrieves arguments for given type of FunProxy, while boxing them in sub lists for each call.
 * @param T the type to look out for.
 * @throws MissingCall if there is no call recorded at given index
 * @return List of List of T
 */
@KMockExperimental
public inline fun <reified T : Any> KMockContract.FunProxy<*, *>.getAllArgumentsBoxedByType(): List<List<T>> {
    val aggregated: MutableList<List<T>> = mutableListOf()

    for (idx in 0 until this.calls) {
        this.getArgumentsForCall(idx).filterIsInstance<T>().also { arguments ->
            aggregated.add(arguments)
        }
    }

    return aggregated
}
