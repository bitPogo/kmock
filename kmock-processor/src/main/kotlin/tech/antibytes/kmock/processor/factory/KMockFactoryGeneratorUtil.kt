/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal class KMockFactoryGeneratorUtil(
    private val genericResolver: ProcessorContract.GenericResolver
) : ProcessorContract.MockFactoryGeneratorUtil {
    private val unused = AnnotationSpec.builder(Suppress::class).addMember("%S", "UNUSED_PARAMETER").build()

    private fun FunSpec.Builder.amendGenericValues(
        generics: List<TypeVariableName>
    ): FunSpec.Builder {
        var counter = 0

        this.addTypeVariables(generics)

        generics.forEach { type ->
            this.addParameter(
                ParameterSpec.builder(
                    name = "ignoreMe$counter",
                    type = type.copy(nullable = true)
                ).addAnnotation(unused).defaultValue("null").build()
            )

            counter += 1
        }

        return this
    }

    private fun buildRelaxedParameter(
        hasDefault: Boolean
    ): ParameterSpec {
        val parameter = ParameterSpec.builder("relaxed", Boolean::class)
        if (hasDefault) {
            parameter.defaultValue("false")
        }

        return parameter.addAnnotation(unused).build()
    }

    private fun buildUnitRelaxedParameter(
        hasDefault: Boolean
    ): ParameterSpec {
        val parameter = ParameterSpec.builder("relaxUnitFun", Boolean::class)
        if (hasDefault) {
            parameter.defaultValue("false")
        }

        return parameter.addAnnotation(unused).build()
    }

    private fun buildVerifierParameter(
        hasDefault: Boolean
    ): ParameterSpec {
        val parameter = ParameterSpec.builder("verifier", ProcessorContract.COLLECTOR_NAME)
        if (hasDefault) {
            parameter.defaultValue("Collector { _, _ -> Unit }")
        }

        return parameter.build()
    }

    private fun buildFreezeParameter(
        hasDefault: Boolean
    ): ParameterSpec {
        val parameter = ParameterSpec.builder("freeze", Boolean::class)
        if (hasDefault) {
            parameter.defaultValue("true")
        }
        return parameter.build()
    }

    override fun generateKmockSignature(
        type: TypeVariableName,
        generics: List<TypeVariableName>,
        hasDefault: Boolean,
        modifier: KModifier?
    ): FunSpec.Builder {
        val functionFactory = FunSpec.builder("kmock")

        functionFactory.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addParameter(buildVerifierParameter(hasDefault))
            .addParameter(buildRelaxedParameter(hasDefault))
            .addParameter(buildUnitRelaxedParameter(hasDefault))
            .addParameter(buildFreezeParameter(hasDefault))
            .returns(type).addTypeVariable(type)

        if (modifier != null) {
            functionFactory.addModifiers(modifier)
        }

        return functionFactory.amendGenericValues(generics)
    }

    private fun buildSpyParameter(): ParameterSpec {
        return ParameterSpec.builder("spyOn", TypeVariableName("SpyOn"))
            .build()
    }

    override fun generateKspySignature(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        generics: List<TypeVariableName>,
        hasDefault: Boolean,
        modifier: KModifier?
    ): FunSpec.Builder {
        val spyFactory = FunSpec.builder("kspy")

        spyFactory.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addTypeVariable(mockType.copy(reified = true))
            .addTypeVariable(spyType.copy(reified = true))
            .returns(mockType)
            .addParameter(buildSpyParameter())
            .addParameter(buildVerifierParameter(hasDefault))
            .addParameter(buildFreezeParameter(hasDefault))

        if (modifier != null) {
            spyFactory.addModifiers(modifier)
        }

        return spyFactory.amendGenericValues(generics)
    }

    override fun splitInterfacesIntoRegularAndGenerics(
        templateSources: List<TemplateSource>
    ): Pair<List<TemplateSource>, List<TemplateSource>> {
        val regular: MutableList<TemplateSource> = mutableListOf()
        val generics: MutableList<TemplateSource> = mutableListOf()

        templateSources.forEach { source ->
            if (source.generics == null) {
                regular.add(source)
            } else {
                generics.add(source)
            }
        }

        return Pair(regular, generics)
    }

    override fun resolveGenerics(
        templateSource: TemplateSource,
    ): List<TypeVariableName> {
        val template = templateSource.template
        val typeResolver = template.typeParameters.toTypeParameterResolver()

        return genericResolver.mapDeclaredGenerics(
            generics = templateSource.generics!!,
            typeResolver = typeResolver
        )
    }
}
