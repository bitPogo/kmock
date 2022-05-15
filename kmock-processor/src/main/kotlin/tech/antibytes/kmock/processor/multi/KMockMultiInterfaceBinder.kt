/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.multi

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.MockShared
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COMMON_INDICATOR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.INTERMEDIATE_INTERFACES_FILE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.KmpCodeGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MultiInterfaceBinder
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource

internal class KMockMultiInterfaceBinder(
    private val logger: KSPLogger,
    private val genericResolver: GenericResolver,
    private val rootPackage: String,
    private val codeGenerator: KmpCodeGenerator,
) : MultiInterfaceBinder {
    private fun TypeSpec.Builder.addMockAnnotation(
        indicator: String,
        interfaceName: String
    ): TypeSpec.Builder {
        val annotation = when (indicator) {
            COMMON_INDICATOR -> AnnotationSpec.builder(MockCommon::class)
            else -> AnnotationSpec.builder(MockShared::class).addMember("\"$indicator\"")
        }

        return this.addAnnotation(
            annotation.addMember("$interfaceName::class").build()
        )
    }

    private fun createInterface(
        interfaceName: String,
        indicator: String,
        templates: List<KSClassDeclaration>,
        generics: List<Map<String, List<KSTypeReference>>?>,
    ): TypeSpec {
        val interfaze = TypeSpec.interfaceBuilder(interfaceName)

        val (parents, typeParameter) = genericResolver.remapTypes(templates, generics)
        interfaze.addSuperinterfaces(parents)

        if (typeParameter.isNotEmpty()) {
            interfaze.addTypeVariables(typeParameter)
        }

        interfaze.addMockAnnotation(
            indicator = indicator,
            interfaceName = interfaceName
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
                indicator = source.indicator,
                templates = source.templates,
                generics = source.generics
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
