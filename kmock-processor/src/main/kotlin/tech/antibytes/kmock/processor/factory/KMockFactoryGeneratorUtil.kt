/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.factory

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.Companion.COLLECTOR_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.FREEZE_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KMOCK_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.KSPY_FACTORY_TYPE_NAME
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NOOP_COLLECTOR
import tech.antibytes.kmock.processor.ProcessorContract.Companion.RELAXER_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SHARED_MOCK_FACTORY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.SPY_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.TEMPLATE_TYPE_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.Companion.UNIT_RELAXER_ARGUMENT
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.Source
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import kotlin.reflect.KClass

internal class KMockFactoryGeneratorUtil(
    freezeOnDefault: Boolean,
    isKmp: Boolean,
    private val genericResolver: GenericResolver
) : ProcessorContract.MockFactoryGeneratorUtil {
    private val modifier: KModifier? = if (isKmp) {
        KModifier.ACTUAL
    } else {
        null
    }

    private val spyOn = ParameterSpec.builder(
        SPY_ARGUMENT,
        TypeVariableName(KSPY_FACTORY_TYPE_NAME).copy(nullable = false)
    ).build()
    private val spyOnNullable = ParameterSpec.builder(
        SPY_ARGUMENT,
        TypeVariableName(KSPY_FACTORY_TYPE_NAME).copy(nullable = true)
    ).build()

    private val relaxed = ParameterSpec.builder(RELAXER_ARGUMENT, Boolean::class).build()
    private val relaxedWithDefault = ParameterSpec.builder(RELAXER_ARGUMENT, Boolean::class)
        .defaultValue("false")
        .build()

    private val relaxedUnit = ParameterSpec.builder(UNIT_RELAXER_ARGUMENT, Boolean::class).build()
    private val relaxedUnitWithDefault = ParameterSpec.builder(UNIT_RELAXER_ARGUMENT, Boolean::class)
        .defaultValue("false")
        .build()

    private val verifier = ParameterSpec.builder(COLLECTOR_ARGUMENT, ProcessorContract.COLLECTOR_NAME).build()
    private val verifierWithDefault = ParameterSpec.builder(COLLECTOR_ARGUMENT, ProcessorContract.COLLECTOR_NAME)
        .defaultValue(NOOP_COLLECTOR.simpleName)
        .build()

    private val freeze = ParameterSpec.builder(FREEZE_ARGUMENT, Boolean::class).build()
    private val freezeWithDefault = ParameterSpec.builder(FREEZE_ARGUMENT, Boolean::class)
        .defaultValue(freezeOnDefault.toString())
        .build()

    private fun TypeName.toParameterizedByStar(): TypeName {
        return if (this is ParameterizedTypeName) {
            val parameter = List(this.typeArguments.size) { STAR }

            this.rawType.parameterizedBy(parameter)
        } else {
            this
        }
    }

    private fun buildGenericFactoryArgument(
        identifier: TypeName,
    ): TypeName {
        return kClass.parameterizedBy(
            identifier.toParameterizedByStar()
        )
    }

    private fun FunSpec.Builder.amendGenericValues(
        identifier: TypeName,
        generics: List<TypeVariableName>
    ): FunSpec.Builder {
        this.addTypeVariables(generics)

        if (generics.isNotEmpty()) {
            this.addParameter(
                ParameterSpec.builder(
                    name = TEMPLATE_TYPE_ARGUMENT,
                    type = buildGenericFactoryArgument(identifier)
                ).build()
            )
        }

        return this
    }

    private fun FunSpec.Builder.amendMultiBounded(
        boundaries: List<TypeName>,
    ): FunSpec.Builder {
        boundaries.forEachIndexed { idx, boundary ->
            this.addParameter(
                ParameterSpec.builder(
                    name = "$TEMPLATE_TYPE_ARGUMENT$idx",
                    type = buildGenericFactoryArgument(boundary)
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
        boundaries: List<TypeName>,
        generics: List<TypeVariableName>,
        hasDefault: Boolean,
        modifier: KModifier?
    ): FunSpec.Builder {
        val kmock = FunSpec.builder(KMOCK_FACTORY)

        kmock.addModifiers(KModifier.INTERNAL, KModifier.INLINE)
            .addParameter(buildVerifierParameter(hasDefault))
            .addParameter(buildRelaxedParameter(hasDefault))
            .addParameter(buildUnitRelaxedParameter(hasDefault))
            .addParameter(buildFreezeParameter(hasDefault))
            .returns(type)
            .addTypeVariable(type.copy(reified = true))

        if (modifier != null) {
            kmock.addModifiers(modifier)
        }

        return if (boundaries.isNotEmpty()) {
            kmock.amendMultiBounded(boundaries)
        } else {
            kmock.amendGenericValues(type.bounds.first(), generics)
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
        boundaries: List<TypeName>,
        generics: List<TypeVariableName>,
        hasDefault: Boolean,
        modifier: KModifier?
    ): FunSpec.Builder {
        val kspy = FunSpec.builder(KSPY_FACTORY)

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

        return if (boundaries.isNotEmpty()) {
            kspy.amendMultiBounded(spyType.bounds)
        } else {
            kspy.amendGenericValues(spyType.bounds.first(), generics)
        }
    }

    override fun generateSharedMockFactorySignature(
        mockType: TypeVariableName,
        spyType: TypeVariableName,
        boundaries: List<TypeName>,
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

        return if (boundaries.isNotEmpty()) {
            mockFactory.amendMultiBounded(boundaries)
        } else {
            mockFactory.amendGenericValues(spyType.bounds.first(), generics)
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
            typeParameterResolver = typeResolver
        )
    }

    private fun String?.isNullOrNotEmpty(): Boolean = this?.isNotEmpty() ?: true

    override fun <T : Source> resolveModifier(templateSource: T?): KModifier? {
        return if (templateSource?.indicator.isNullOrNotEmpty()) {
            modifier
        } else {
            null
        }
    }

    private fun resolveParameter(rawParameter: List<TypeName>): List<TypeName> {
        val parameter: MutableList<TypeName> = MutableList(rawParameter.size) { idx ->
            TypeVariableName("${ProcessorContract.TYPE_PARAMETER}$idx")
        }

        parameter.add(STAR)

        return parameter
    }

    override fun resolveMockType(
        templateSource: TemplateMultiSource,
        parameter: List<TypeName>,
    ): TypeName {
        return ClassName(
            packageName = templateSource.packageName,
            "${templateSource.templateName}Mock",
        ).parameterizedBy(
            resolveParameter(parameter)
        )
    }

    companion object {
        private val kClass = KClass::class.asClassName()
    }
}
