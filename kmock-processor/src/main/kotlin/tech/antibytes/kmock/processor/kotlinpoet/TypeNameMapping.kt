/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ktlint:standard:filename")

package tech.antibytes.kmock.processor.kotlinpoet

import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver

internal fun KSTypeReference.toTypeName(
    typeParameterResolver: TypeParameterResolver,
): TypeName = mapArgumentType(
    typeParameterResolver = typeParameterResolver,
    mapping = emptyMap(),
)
