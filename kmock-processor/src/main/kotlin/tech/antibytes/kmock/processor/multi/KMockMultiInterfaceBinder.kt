/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.multi

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.writeTo
import tech.antibytes.kmock.MockCommon
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.Companion.INTERMEDIATE_INTERFACES_FILE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.KmpCodeGenerator
import tech.antibytes.kmock.processor.ProcessorContract.MultiInterfaceBinder
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource

internal class KMockMultiInterfaceBinder(
    private val logger: KSPLogger,
    private val genericResolver: GenericResolver,
    private val rootPackage: String,
    private val codeGenerator: KmpCodeGenerator,
) : MultiInterfaceBinder {
    private fun resolveTypeParameter(
        typeParameter: Map<String, List<KSTypeReference>>?,
        typeResolver: TypeParameterResolver,
        suffix: Int,
    ): List<TypeVariableName> {
        return if (typeParameter == null) {
            emptyList()
        } else {
            genericResolver.mapDeclaredGenerics(
                generics = typeParameter,
                typeResolver = typeResolver,
                suffix = suffix,
            )
        }
    }

    private fun remapTypes(
        templates: List<KSClassDeclaration>,
        generics: List<Map<String, List<KSTypeReference>>?>
    ): Pair<List<TypeName>, List<TypeVariableName>> {
        var counter = 0
        val aggregatedTypeParameter: MutableList<TypeVariableName> = mutableListOf()
        val parameterizedParents = templates.mapIndexed { idx, parent ->
            val typeParameter = resolveTypeParameter(
                typeParameter = generics[idx],
                typeResolver = parent.typeParameters.toTypeParameterResolver(),
                suffix = counter
            )
            val raw = parent.toClassName()
            counter += generics[idx]?.size?.plus(1) ?: 0

            if (typeParameter.isNotEmpty()) {
                aggregatedTypeParameter.addAll(typeParameter)
                raw.parameterizedBy(typeParameter)
            } else {
                raw
            }
        }

        return Pair(parameterizedParents, aggregatedTypeParameter)
    }

    private fun createInterface(
        interfaceName: String,
        templates: List<KSClassDeclaration>,
        generics: List<Map<String, List<KSTypeReference>>?>,
    ): TypeSpec {
        val interfaze = TypeSpec.interfaceBuilder(interfaceName)

        val (parents, typeParameter) = remapTypes(templates, generics)
        interfaze.addSuperinterfaces(parents)

        if (typeParameter.isNotEmpty()) {
            interfaze.addTypeVariables(typeParameter)
        }

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
