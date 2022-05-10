/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.google.devtools.ksp.symbol.KSFile
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryEntryPointGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MockFactoryGenerator
import tech.antibytes.kmock.processor.ProcessorContract.Relaxer
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal object NoopFactoryGenerator : MockFactoryGenerator, MockFactoryEntryPointGenerator {
    override fun writeFactories(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        relaxer: Relaxer?,
        dependencies: List<KSFile>,
    ) = Unit

    override fun generateCommon(
        templateSources: List<TemplateSource>,
        templateMultiSources: List<TemplateMultiSource>,
        totalTemplates: List<TemplateSource>,
        totalMultiSources: List<TemplateMultiSource>,
        dependencies: List<KSFile>,
    ) = Unit

    override fun generateShared(
        templateSources: List<TemplateSource>,
        dependencies: List<KSFile>,
    ) = Unit
}
