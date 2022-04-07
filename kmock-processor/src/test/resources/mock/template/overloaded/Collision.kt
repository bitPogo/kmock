/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package mock.template.overloaded

import tech.antibytes.kmock.Mock

@Mock(Collision::class)
interface Collision {
    val foo: Any
    var hashCode: Int
    fun foo(fuzz: Int, ozz: Any): Any
    fun foo(fuzz: Any, ozz: Int): Any
    fun foo(fuzz: Any, ozz: String): Any
    fun foo(fuzz: String, ozz: Any): Any
    fun foo(fuzz: String, ozz: Abc): Any
    fun foo(fuzz: String, ozz: Scope.Abc): Any
    fun foo(fuzz: (Any) -> Unit): Any
    fun <T> foo(fuzz: T)
    fun <T : Collision> foo(fuzz: T)
    fun <T : LPG> foo(fuzz: T)
    fun foo(vararg fuzz: Any): Any
}

data class Abc(
    val value: String
)

interface Scope {
    data class Abc(
        val value: String
    )
}

interface LPG
