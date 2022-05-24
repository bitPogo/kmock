/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.nullableAny
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration

internal fun GenericDeclaration.resolveGeneric(): TypeName {
    return if (this.types.size > 1) {
        nullableAny.copy(nullable = nullable)
    } else {
        this.types.first().copy(nullable = nullable)
    }
}
