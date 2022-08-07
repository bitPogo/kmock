/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NULLABLE_ANY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.TYPE_PARAMETER
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.kotlinpoet.mapArgumentType
import tech.antibytes.kmock.processor.kotlinpoet.mapParameterType
import tech.antibytes.kmock.processor.kotlinpoet.resolveGenericDeclaration
import tech.antibytes.kmock.processor.kotlinpoet.toTypeName
import tech.antibytes.kmock.processor.kotlinpoet.toTypeParameterResolver
import tech.antibytes.kmock.processor.kotlinpoet.toTypeVariableName

internal object KMockGenerics : GenericResolver {
    private val nullableAnys = listOf(NULLABLE_ANY)

    private fun KSTypeParameter.resolveBound(): List<KSTypeReference> = bounds.toList()

    private fun extractGenerics(
        typeParameter: List<KSTypeParameter>,
    ): Map<String, List<KSTypeReference>> {
        val generic: MutableMap<String, List<KSTypeReference>> = mutableMapOf()
        typeParameter.forEach { type ->
            generic[type.name.getShortName()] = type.resolveBound()
        }

        return generic
    }

    override fun extractGenerics(template: KSDeclaration): Map<String, List<KSTypeReference>>? {
        return if (template.typeParameters.isEmpty()) {
            null
        } else {
            extractGenerics(template.typeParameters)
        }
    }

    override fun mapDeclaredGenerics(
        generics: Map<String, List<KSTypeReference>>,
        typeParameterResolver: TypeParameterResolver,
    ): List<TypeVariableName> = generics.map { (type, bounds) ->
        TypeVariableName(
            type,
            bounds = bounds.map { ksReference -> ksReference.toTypeName(typeParameterResolver) },
        )
    }

    private fun mapTypes(
        generics: Map<String, List<KSTypeReference>>,
        suffix: Int,
    ): Map<String, String> {
        var counter = 0 + suffix
        return generics.map { (typeName, _) ->
            Pair(typeName, "$TYPE_PARAMETER$counter").also { counter++ }
        }.toMap()
    }

    private fun mapDeclaredGenericsWithSuffix(
        generics: Map<String, List<KSTypeReference>>,
        suffix: Int,
        typeResolver: TypeParameterResolver,
    ): List<TypeVariableName> {
        var counter = 0 + suffix
        val mapping = mapTypes(generics, suffix)
        return generics.map { (_, bounds) ->
            TypeVariableName(
                "$TYPE_PARAMETER$counter",
                bounds = bounds.map { ksReference ->
                    ksReference.mapArgumentType(
                        typeParameterResolver = typeResolver,
                        mapping = mapping,
                    )
                },
            ).also { counter++ }
        }
    }

    private fun resolveTypeParameter(
        typeParameter: Map<String, List<KSTypeReference>>?,
        typeResolver: TypeParameterResolver,
        suffix: Int,
    ): List<TypeVariableName> {
        return if (typeParameter == null) {
            emptyList()
        } else {
            mapDeclaredGenericsWithSuffix(
                generics = typeParameter,
                typeResolver = typeResolver,
                suffix = suffix,
            )
        }
    }

    override fun remapTypes(
        templates: List<KSClassDeclaration>,
        generics: List<Map<String, List<KSTypeReference>>?>,
    ): Pair<List<TypeName>, List<TypeVariableName>> {
        var counter = 0
        val aggregatedTypeParameter: MutableList<TypeVariableName> = mutableListOf()
        val parameterizedParents = templates.mapIndexed { idx, parent ->
            val typeParameter = resolveTypeParameter(
                typeParameter = generics[idx],
                typeResolver = parent.typeParameters.toTypeParameterResolver(),
                suffix = counter,
            )
            val raw = parent.toClassName()
            counter += generics[idx]?.size ?: 0

            if (typeParameter.isNotEmpty()) {
                aggregatedTypeParameter.addAll(typeParameter)
                raw.parameterizedBy(typeParameter)
            } else {
                raw
            }
        }

        return Pair(parameterizedParents, aggregatedTypeParameter)
    }

    private fun resolveNullableAny(): GenericDeclaration {
        return GenericDeclaration(
            types = nullableAnys,
            isRecursive = false,
            isNullable = true,
        )
    }

    private fun KSType.isNullable(): Boolean = nullability == Nullability.NULLABLE || isMarkedNullable

    private fun List<KSTypeReference>.determineRootNullability(): Boolean {
        return all { reference -> reference.resolve().isNullable() }
    }

    private fun KSTypeReference.resolveSingleBoundary(
        visited: Set<String>,
        classScope: Set<String>,
        allGenerics: Set<String>,
        rootNullability: Boolean,
        resolved: Map<String, GenericDeclaration>,
        typeParameterResolver: TypeParameterResolver,
    ): GenericDeclaration? {
        val type = mapParameterType(
            visited = visited,
            classScope = classScope,
            functionScope = allGenerics,
            rootNullability = rootNullability,
            resolved = resolved,
            typeParameterResolver = typeParameterResolver,
        )

        return type.resolveGenericDeclaration()
    }

    private fun List<KSTypeReference>.resolveMultiBoundary(
        visited: Set<String>,
        classScope: Set<String>,
        allGenerics: Set<String>,
        rootNullability: Boolean,
        resolved: Map<String, GenericDeclaration>,
        typeParameterResolver: TypeParameterResolver,
    ): GenericDeclaration? {
        var isNullable = true
        var castOnReturn = false
        var isRecursive = false

        val resolvedTypes = map { rawType ->
            val type = rawType.mapParameterType(
                visited = visited,
                classScope = classScope,
                functionScope = allGenerics,
                rootNullability = rootNullability,
                resolved = resolved,
                typeParameterResolver = typeParameterResolver,
            )

            val genericDeclaration = type.resolveGenericDeclaration()
                ?: return null

            isRecursive = isRecursive || genericDeclaration.isRecursive
            isNullable = isNullable && type.isNullable

            castOnReturn = castOnReturn || genericDeclaration.doCastReturnType

            type
        }

        return GenericDeclaration(
            types = resolvedTypes,
            isRecursive = isRecursive,
            isNullable = isNullable,
            doCastReturnType = castOnReturn,
        )
    }

    private fun determineType(
        visited: Set<String>,
        classScope: Set<String>,
        allGenerics: Set<String>,
        types: List<KSTypeReference>,
        resolved: Map<String, GenericDeclaration>,
        typeParameterResolver: TypeParameterResolver,
    ): GenericDeclaration? {
        val rootNullability = types.determineRootNullability()
        return when (types.size) {
            0 -> resolveNullableAny()
            1 -> types.first().resolveSingleBoundary(
                visited = visited,
                classScope = classScope,
                allGenerics = allGenerics,
                rootNullability = rootNullability,
                resolved = resolved,
                typeParameterResolver = typeParameterResolver,
            )
            else -> types.resolveMultiBoundary(
                visited = visited,
                classScope = classScope,
                allGenerics = allGenerics,
                rootNullability = rootNullability,
                resolved = resolved,
                typeParameterResolver = typeParameterResolver,
            )
        }
    }

    override fun mapProxyGenerics(
        classScope: Map<String, List<TypeName>>?,
        generics: Map<String, List<KSTypeReference>>,
        typeParameterResolver: TypeParameterResolver,
    ): Map<String, GenericDeclaration> {
        val raw = generics.toMutableMap()
        val resolved: MutableMap<String, GenericDeclaration> = mutableMapOf()
        val allGenerics = raw.keys
        val visited: MutableSet<String> = mutableSetOf()
        val classWideGenerics = classScope?.keys ?: emptySet()

        while (resolved.keys != allGenerics) {
            raw.forEach { (root, declaration) ->
                if (root !in resolved) {
                    visited.add(root)

                    val type = determineType(
                        visited = visited,
                        classScope = classWideGenerics,
                        allGenerics = allGenerics,
                        types = declaration,
                        resolved = resolved,
                        typeParameterResolver = typeParameterResolver,
                    )

                    if (type != null) {
                        resolved[root] = type
                    }
                }
            }

            visited.clear()
        }

        return resolved
    }

    override fun resolveMockClassType(
        template: KSClassDeclaration,
        typeParameterResolver: TypeParameterResolver,
    ): TypeName {
        return if (template.typeParameters.isEmpty()) {
            template.toClassName()
        } else {
            template.toClassName()
                .parameterizedBy(
                    template.typeParameters.map { type ->
                        type.toTypeVariableName(typeParameterResolver)
                    },
                )
        }
    }

    override fun resolveKMockFactoryType(
        name: String,
        templateSource: TemplateSource,
    ): TypeVariableName {
        val template = templateSource.template
        val typeResolver = template.typeParameters.toTypeParameterResolver()
        val boundary = resolveMockClassType(template, typeResolver)

        return TypeVariableName(name, bounds = listOf(boundary)).copy(reified = true)
    }

    private fun List<KSTypeReference>.toTypeNames(
        resolver: TypeParameterResolver,
    ): List<TypeName> = this.map { rawType -> rawType.toTypeName(resolver) }

    private fun List<KSTypeReference>.resolveTypeNames(resolver: TypeParameterResolver): List<TypeName> {
        return if (this.isEmpty()) {
            nullableAnys
        } else {
            this.toTypeNames(resolver)
        }
    }

    override fun mapClassScopeGenerics(
        generics: Map<String, List<KSTypeReference>>?,
        typeParameterResolver: TypeParameterResolver,
    ): Map<String, List<TypeName>>? {
        return generics?.map { (key, rawTypes) ->
            key to rawTypes.resolveTypeNames(typeParameterResolver)
        }?.toMap()
    }
}
