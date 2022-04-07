/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.renamed

import tech.antibytes.kmock.MockShared

@MockShared(
    "sharedTest",
    Shared::class
)
interface Shared<K, L> where L : Any, L : Comparable<L>, K : Any {
    var template: L
    val ozz: Int

    fun bar(arg0: Int): Any
    suspend fun buzz(arg0: String): L
}
