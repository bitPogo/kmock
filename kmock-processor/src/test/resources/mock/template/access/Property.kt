/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.access

import tech.antibytes.kmock.Mock

@Mock(Property::class)
interface Property<L, T> where T : CharSequence, T : Comparable<T> {
    val foo: String
    val bar: Int
        get() = foo.length

    val uzz: String
    val izz: L

    val tuz: T


    var buzz: Any
}
