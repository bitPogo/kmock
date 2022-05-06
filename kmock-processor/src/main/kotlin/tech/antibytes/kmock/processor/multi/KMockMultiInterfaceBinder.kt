/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.multi

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.processor.ProcessorContract.KmpCodeGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MultiInterfaceBinder
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource

internal class KMockMultiInterfaceBinder(
    private val logger: KSPLogger,
    private val codeGenerator: KmpCodeGenerator,
) : MultiInterfaceBinder {
    private fun createInterface(
        interfaceName: String,
        templates: List<KSClassDeclaration>
    ): TypeSpec {
        val interfaze = TypeSpec.interfaceBuilder(interfaceName)
        interfaze.addSuperinterfaces(
            templates.map { parent -> parent.asStarProjectedType().toTypeName() }
        )
        interfaze.addAnnotation(
            AnnotationSpec.builder(MockCommon::class).addMember("$interfaceName::class").build()
        )

        return interfaze.build()
    }

    private fun writeInterface(
        interfaceName: String,
        packageName: String,
        templates: List<KSClassDeclaration>,
        dependencies: KSFile,
    ) {
        val file = FileSpec.builder(
            packageName,
            interfaceName
        )

        val implementation = createInterface(
            interfaceName = interfaceName,
            templates = templates
        )

        file.addType(implementation)
        file.build().writeTo(
            codeGenerator = codeGenerator,
            aggregating = true,
            originatingKSFiles = listOf(dependencies)
        )
    }

    override fun bind(
        templateSources: List<TemplateMultiSource>,
        dependencies: List<KSFile>
    ) {
        templateSources.mapIndexed { idx, source ->
            writeInterface(
                interfaceName = source.templateName,
                packageName = source.packageName,
                templates = source.templates,
                dependencies = dependencies[idx]
            )
        }
    }
}
