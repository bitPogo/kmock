/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Source

internal class SpyContainer(
    private val spyOn: Set<String>,
    private val spiesOnly: Boolean,
    private val spyAll: Boolean
) : ProcessorContract.SpyContainer {
    override fun isSpyable(
        template: KSClassDeclaration?,
        packageName: String,
        templateName: String
    ): Boolean {
        return spiesOnly ||
            spyAll ||
            template?.qualifiedName?.asString() in spyOn ||
            "$packageName.$templateName" in spyOn
    }

    private fun List<Source>.deriveQualifiedNames(): List<String> {
        return this.map { source -> "${source.packageName}.${source.templateName}" }
    }

    private fun Set<String>.filterSpies(raw: List<Source>): List<String> {
        val filter = raw.deriveQualifiedNames()
        return this.filter { qualifiedName -> qualifiedName !in filter }
    }

    override fun hasSpies(filter: List<Source>): Boolean = spiesOnly || spyAll || spyOn.filterSpies(filter).isNotEmpty()
}
