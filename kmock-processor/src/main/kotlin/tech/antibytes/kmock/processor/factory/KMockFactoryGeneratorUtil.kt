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

    private fun buildGenericFactoryArgument(
        identifier: TypeVariableName,
        generics: List<TypeVariableName>
    ): TypeVariableName {
        val mockType = identifier
            .bounds
            .first()
            .toString()
            .substringBeforeLast('<')

        val genericTypes = List(generics.size) { "*" }

        return TypeVariableName(
            "kotlin.reflect.KClass<$mockType<${genericTypes.joinToString(", ")}>>"
        )
    }

    private fun FunSpec.Builder.amendGenericValues(
        identifier: TypeVariableName,
        generics: List<TypeVariableName>
    ): FunSpec.Builder {
        this.addTypeVariables(generics)

        if (generics.isNotEmpty()) {
            this.addParameter(
                ParameterSpec.builder(
                    name = "templateType",
                    type = buildGenericFactoryArgument(identifier, generics)
                ).addAnnotation(unused).build()
            )
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
        val kmock = FunSpec.builder("kmock")

        kmock.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addParameter(buildVerifierParameter(hasDefault))
            .addParameter(buildRelaxedParameter(hasDefault))
            .addParameter(buildUnitRelaxedParameter(hasDefault))
            .addParameter(buildFreezeParameter(hasDefault))
            .returns(type).addTypeVariable(type)

        if (modifier != null) {
            kmock.addModifiers(modifier)
        }

        return kmock.amendGenericValues(type, generics)
    }

    private fun buildSpyParameter(): ParameterSpec {
        return ParameterSpec.builder("spyOn", TypeVariableName("SpyOn"))
            .addAnnotation(unused)
            .build()
    }

    override fun generateKspySignature(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        generics: List<TypeVariableName>,
        hasDefault: Boolean,
        modifier: KModifier?
    ): FunSpec.Builder {
        val kspy = FunSpec.builder("kspy")

        kspy.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addTypeVariable(mockType.copy(reified = true))
            .addTypeVariable(spyType.copy(reified = true))
            .returns(mockType)
            .addParameter(buildSpyParameter())
            .addParameter(buildVerifierParameter(hasDefault))
            .addParameter(buildFreezeParameter(hasDefault))

        if (modifier != null) {
            kspy.addModifiers(modifier)
        }

        return kspy.amendGenericValues(spyType, generics)
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
