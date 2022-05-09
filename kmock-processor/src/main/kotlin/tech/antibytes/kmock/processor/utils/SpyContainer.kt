/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import tech.antibytes.kmock.processor.ProcessorContract

internal class SpyContainer(
    private val spiesOnly: Boolean,
    private val spyOn: Set<String>,
) : ProcessorContract.SpyContainer {
    override fun isSpyable(
        template: KSClassDeclaration?,
        packageName: String,
        templateName: String
    ): Boolean {
        return template?.qualifiedName?.asString() in spyOn ||
            "$packageName.$templateName" in spyOn ||
            spiesOnly
    }

    override fun hasSpies(): Boolean = spyOn.isNotEmpty() || spiesOnly
}
