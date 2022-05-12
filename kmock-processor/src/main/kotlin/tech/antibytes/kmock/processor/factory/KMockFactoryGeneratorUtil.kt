/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SHARED_MOCK_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal class KMockFactoryGeneratorUtil(
    freezeOnDefault: Boolean,
    isKmp: Boolean,
    private val genericResolver: ProcessorContract.GenericResolver
) : ProcessorContract.MockFactoryGeneratorUtil {
    private val modifier: KModifier? = if (isKmp) {
        KModifier.ACTUAL
    } else {
        null
    }

    private val spyOn = ParameterSpec.builder(
        "spyOn",
        TypeVariableName("SpyOn").copy(nullable = false)
    ).build()
    private val spyOnNullable = ParameterSpec.builder(
        "spyOn",
        TypeVariableName("SpyOn").copy(nullable = true)
    ).build()

    private val relaxed = ParameterSpec.builder("relaxed", Boolean::class).build()
    private val relaxedWithDefault = ParameterSpec.builder("relaxed", Boolean::class)
        .defaultValue("false")
        .build()

    private val relaxedUnit = ParameterSpec.builder("relaxUnitFun", Boolean::class).build()
    private val relaxedUnitWithDefault = ParameterSpec.builder("relaxUnitFun", Boolean::class)
        .defaultValue("false")
        .build()

    private val verifier = ParameterSpec.builder("verifier", ProcessorContract.COLLECTOR_NAME).build()
    private val verifierWithDefault = ParameterSpec.builder("verifier", ProcessorContract.COLLECTOR_NAME)
        .defaultValue("NoopCollector")
        .build()

    private val freeze = ParameterSpec.builder("freeze", Boolean::class).build()
    private val freezeWithDefault = ParameterSpec.builder("freeze", Boolean::class)
        .defaultValue(freezeOnDefault.toString())
        .build()

    private fun resolveArgumentType(identifier: TypeName): String {
        return if (identifier is TypeVariableName) {
            identifier
                .bounds
                .first()
                .toString()
                .substringBeforeLast('<')
        } else {
            identifier.toString()
        }
    }

    private fun resolveGenericParameter(
        generics: List<TypeVariableName>
    ): String {
        return if (generics.isEmpty()) {
            ""
        } else {
            val genericTypes = List(generics.size) { "*" }

            "<${genericTypes.joinToString(", ")}>"
        }
    }

    private fun buildGenericFactoryArgument(
        identifier: TypeName,
        generics: List<TypeVariableName>
    ): TypeVariableName {
        val mockType = resolveArgumentType(identifier)
        val parameter = resolveGenericParameter(generics)

        return TypeVariableName("kotlin.reflect.KClass<$mockType$parameter>")
    }

    private fun FunSpec.Builder.amendGenericValues(
        identifier: TypeName,
        generics: List<TypeVariableName>
    ): FunSpec.Builder {
        this.addTypeVariables(generics)

        if (generics.isNotEmpty()) {
            this.addParameter(
                ParameterSpec.builder(
                    name = "templateType",
                    type = buildGenericFactoryArgument(identifier, generics)
                ).build()
            )
        }

        return this
    }

    private fun FunSpec.Builder.amendMultiBounded(
        identifier: TypeVariableName,
    ): FunSpec.Builder {
        identifier.bounds.forEachIndexed { idx, boundary ->
            this.addParameter(
                ParameterSpec.builder(
                    name = "templateType$idx",
                    type = buildGenericFactoryArgument(boundary, emptyList())
                ).build()
            )
        }

        return this
    }

    private fun buildRelaxedParameter(
        hasDefault: Boolean
    ): ParameterSpec {
        return if (hasDefault) {
            relaxedWithDefault
        } else {
            relaxed
        }
    }

    private fun buildUnitRelaxedParameter(
        hasDefault: Boolean
    ): ParameterSpec {
        return if (hasDefault) {
            relaxedUnitWithDefault
        } else {
            relaxedUnit
        }
    }

    private fun buildVerifierParameter(
        hasDefault: Boolean
    ): ParameterSpec {
        return if (hasDefault) {
            verifierWithDefault
        } else {
            verifier
        }
    }

    private fun buildFreezeParameter(
        hasDefault: Boolean,
    ): ParameterSpec {
        return if (hasDefault) {
            freezeWithDefault
        } else {
            freeze
        }
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
            .returns(type).addTypeVariable(type.copy(reified = true))

        if (modifier != null) {
            kmock.addModifiers(modifier)
        }

        return if (type.bounds.size > 1) {
            kmock.amendMultiBounded(type)
        } else {
            kmock.amendGenericValues(type, generics)
        }
    }

    private fun buildSpyParameter(nullable: Boolean = false): ParameterSpec {
        return if (nullable) {
            spyOnNullable
        } else {
            spyOn
        }
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

        return if (spyType.bounds.size > 1) {
            kspy.amendMultiBounded(spyType)
        } else {
            kspy.amendGenericValues(spyType, generics)
        }
    }

    override fun generateSharedMockFactorySignature(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        generics: List<TypeVariableName>,
    ): FunSpec.Builder {
        val mockFactory = FunSpec.builder(SHARED_MOCK_FACTORY)

        mockFactory.addModifiers(KModifier.PRIVATE, KModifier.INLINE)
            .addTypeVariable(mockType.copy(reified = true))
            .addTypeVariable(spyType.copy(reified = true))
            .returns(mockType)
            .addParameter(buildSpyParameter(true))
            .addParameter(buildVerifierParameter(false))
            .addParameter(buildRelaxedParameter(false))
            .addParameter(buildUnitRelaxedParameter(false))
            .addParameter(buildFreezeParameter(false))

        return if (spyType.bounds.size > 1) {
            mockFactory.amendMultiBounded(spyType)
        } else {
            mockFactory.amendGenericValues(spyType, generics)
        }
    }

    override fun splitInterfacesIntoRegularAndGenerics(
        templateSources: List<TemplateSource>
    ): Pair<List<TemplateSource>, List<TemplateSource>> {
        return templateSources.partition { source -> source.generics == null }
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

    override fun resolveModifier(): KModifier? = modifier
}
