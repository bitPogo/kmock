/* ktlint-disable filename */
/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.squareup.kotlinpoet.TypeName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NULLABLE_ANY
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration

internal fun GenericDeclaration.resolveGeneric(): TypeName {
    return if (this.types.size > 1) {
        NULLABLE_ANY.copy(nullable = isNullable)
    } else {
        this.types.first().copy(nullable = isNullable)
    }
}
