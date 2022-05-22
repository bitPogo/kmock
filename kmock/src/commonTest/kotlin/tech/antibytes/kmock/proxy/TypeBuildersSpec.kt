/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.ReflectionExpansionContract.GenericKClass
import tech.antibytes.kmock.ReflectionExpansionContract.GenericVarargKClass
import tech.antibytes.kmock.ReflectionExpansionContract.TypeKClass
import tech.antibytes.kmock.ReflectionExpansionContract.VarargKClass
import tech.antibytes.util.test.fulfils
import kotlin.test.Test

@OptIn(KMockExperimental::class)
class TypeBuildersSpec {
    @Test
    fun `typeOf returns a TypeKClass`() {
        typeOf<Any>() fulfils TypeKClass::class
    }

    @Test
    fun `genericOf returns a GenericKClass`() {
        genericOf<Any>() fulfils GenericKClass::class
    }

    @Test
    fun `varargOf returns a VarargKClass`() {
        varargOf<Any>() fulfils VarargKClass::class
    }

    @Test
    fun `genericVarargOf returns a GenericVarargKClass`() {
        genericVarargOf<Any>() fulfils GenericVarargKClass::class
    }
}
