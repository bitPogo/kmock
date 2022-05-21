/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import kotlin.reflect.KClass

@KMockExperimental
interface ReflectionExpansionContract {
    interface TypeKClass<T> where T : Any {
        val type: KClass<T>
    }

    interface GenericKClass<T> where T : Any {
        val type: KClass<T>
    }

    interface VarargKClass<T> where T: Any {
        val type: KClass<T>
    }

    interface GenericVarargKClass<T> where T: Any {
        val type: KClass<T>
    }
}

@OptIn(KMockExperimental::class)
@PublishedApi internal data class TypeKClass<T>(
    override val type: KClass<T>
) : ReflectionExpansionContract.TypeKClass<T> where T : Any

@OptIn(KMockExperimental::class)
@PublishedApi internal data class GenericKClass<T>(
    override val type: KClass<T>
) : ReflectionExpansionContract.GenericKClass<T> where T : Any

@OptIn(KMockExperimental::class)
@PublishedApi internal data class VarargKClass<T>(
    override val type: KClass<T>
) : ReflectionExpansionContract.VarargKClass<T> where T : Any

@OptIn(KMockExperimental::class)
@PublishedApi internal data class GenericVarargKClass<T>(
    override val type: KClass<T>
) : ReflectionExpansionContract.GenericVarargKClass<T> where T : Any

