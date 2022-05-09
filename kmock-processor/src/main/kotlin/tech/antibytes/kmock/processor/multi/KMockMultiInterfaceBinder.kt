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
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.processor.ProcessorContract.Companion.INTERMEDIATE_INTERFACES_FILE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.KmpCodeGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MultiInterfaceBinder
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource

internal class KMockMultiInterfaceBinder(
    private val logger: KSPLogger,
    private val rootPackage: String,
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
        interfaze.addModifiers(KModifier.PRIVATE)

        return interfaze.build()
    }

    override fun bind(
        templateSources: List<TemplateMultiSource>,
        dependencies: List<KSFile>
    ) {
        val file = FileSpec.builder(
            rootPackage,
            INTERMEDIATE_INTERFACES_FILE_NAME
        )

        templateSources.map { source ->
            val implementation = createInterface(
                interfaceName = source.templateName,
                templates = source.templates
            )

            file.addType(implementation)
        }

        file.build().writeTo(
            codeGenerator = codeGenerator,
            aggregating = true,
            originatingKSFiles = dependencies
        )
    }
}
