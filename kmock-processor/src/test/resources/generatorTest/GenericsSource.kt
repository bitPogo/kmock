/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package generatorTest

import tech.antibytes.kmock.Mock

@Mock(Generics::class)
interface Generics<K : Any, L> {
    var template: K
    fun <T> foo(payload: T)
    fun <K: Int> foo(payload: K)
}

