/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.proxy

import tech.antibytes.kmock.GenericKClass
import tech.antibytes.kmock.GenericVarargKClass
import tech.antibytes.kmock.KMockExperimental
import tech.antibytes.kmock.ReflectionExpansionContract
import tech.antibytes.kmock.TypeKClass
import tech.antibytes.kmock.VarargKClass

@KMockExperimental
inline fun <reified T> typeOf(): ReflectionExpansionContract.TypeKClass<T> where T : Any {
    return TypeKClass(T::class)
}

@KMockExperimental
inline fun <reified T> genericOf(): ReflectionExpansionContract.GenericKClass<T> where T : Any {
    return GenericKClass(T::class)
}

@KMockExperimental
inline fun <reified T> varargOf(): ReflectionExpansionContract.VarargKClass<T> where T : Any {
    return VarargKClass(T::class)
}

@KMockExperimental
inline fun <reified T> genericVarargOf(): ReflectionExpansionContract.GenericVarargKClass<T> where T : Any {
    return GenericVarargKClass(T::class)
}
