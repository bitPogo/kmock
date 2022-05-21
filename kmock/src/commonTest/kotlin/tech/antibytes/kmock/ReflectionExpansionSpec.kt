/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe
import kotlin.test.Test

class ReflectionExpansionSpec {
    @Test
    fun `It fulfils TypeKClass`() {
        TypeKClass(Any::class) fulfils ReflectionExpansionContract.TypeKClass::class
    }

    @Test
    fun `TypeKClass exposes the referenced KClass`() {
        TypeKClass(Int::class).type mustBe Int::class
    }

    @Test
    fun `It fulfils GenericKClass`() {
        GenericKClass(Any::class) fulfils ReflectionExpansionContract.GenericKClass::class
    }

    @Test
    fun `GenericKClass exposes the referenced KClass`() {
        GenericKClass(Int::class).type mustBe Int::class
    }

    @Test
    fun `It fulfils VarargKClass`() {
        VarargKClass(Any::class) fulfils ReflectionExpansionContract.VarargKClass::class
    }

    @Test
    fun `VarargKClass exposes the referenced KClass`() {
        VarargKClass(Int::class).type mustBe Int::class
    }

    @Test
    fun `It fulfils GenericVarargKClass`() {
        GenericVarargKClass(Any::class) fulfils ReflectionExpansionContract.GenericVarargKClass::class
    }

    @Test
    fun `GenericVarargKClass exposes the referenced KClass`() {
        GenericVarargKClass(Int::class).type mustBe Int::class
    }
}
