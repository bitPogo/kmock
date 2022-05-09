/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.multi

import com.google.devtools.ksp.symbol.KSClassDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.Aggregated
import tech.antibytes.kmock.processor.ProcessorContract.ParentFinder
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal object KMockParentFinder : ParentFinder {
    override fun find(
        templateSource: TemplateSource,
        templateMultiSources: Aggregated<TemplateMultiSource>,
    ): List<KSClassDeclaration> {
        val parentsIdx = templateMultiSources.extractedTemplates.indexOfFirst { parent ->
            templateSource.packageName == parent.packageName &&
                templateSource.indicator == parent.indicator &&
                templateSource.templateName == parent.templateName
        }

        return if (parentsIdx == -1) {
            emptyList()
        } else {
            templateMultiSources.extractedTemplates[parentsIdx].templates
        }
    }
}
