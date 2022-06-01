/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import tech.antibytes.kmock.processor.ProcessorContract.Companion.TYPE_PARAMETER
import tech.antibytes.kmock.processor.ProcessorContract.Companion.any
import tech.antibytes.kmock.processor.ProcessorContract.Companion.nullableAny
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.utils.mapArgumentType
import tech.antibytes.kmock.processor.utils.rawType

internal object KMockGenerics : GenericResolver {
    private val nullableAnys = listOf(nullableAny)
    private val nonNullableAnys = listOf(any)

    private fun resolveBound(type: KSTypeParameter): List<KSTypeReference> = type.bounds.toList()

    private fun extractGenerics(
        typeParameter: List<KSTypeParameter>,
    ): Map<String, List<KSTypeReference>> {
        val generic: MutableMap<String, List<KSTypeReference>> = mutableMapOf()
        typeParameter.forEach { type ->
            generic[type.name.getShortName()] = resolveBound(type)
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
        typeResolver: TypeParameterResolver
    ): List<TypeVariableName> = generics.map { (type, bounds) ->
        TypeVariableName(
            type,
            bounds = bounds.map { ksReference -> ksReference.toTypeName(typeResolver) }
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
        typeResolver: TypeParameterResolver
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
                }
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

    private fun isNullable(type: KSType): Boolean = type.nullability == Nullability.NULLABLE

    private fun anys(rootNullability: Boolean): List<TypeName> {
        return if (rootNullability) {
            nullableAnys
        } else {
            nonNullableAnys
        }
    }

    private fun resolveKnownType(
        visited: Set<String>,
        rootNullability: Boolean,
        typeName: TypeName,
        resolved: Map<String, GenericDeclaration>,
    ): GenericDeclaration? {
        val typeNameStr = typeName.toString().trimEnd('?')
        val type = resolved[typeNameStr]

        return if (type == null && typeNameStr in visited) {
            GenericDeclaration(
                types = anys(rootNullability),
                nullable = rootNullability,
                recursive = true
            )
        } else {
            type
        }
    }

    private fun resolveTypeName(
        visited: Set<String>,
        rootNullability: Boolean,
        type: KSType,
        resolved: Map<String, GenericDeclaration>,
        allGenerics: Set<String>,
        typeResolver: TypeParameterResolver
    ): GenericDeclaration? {
        val typeName = type.toTypeName(typeResolver)

        return if (typeName.toString().trimEnd('?') in allGenerics) {
            resolveKnownType(
                visited,
                rootNullability,
                typeName,
                resolved
            )
        } else {
            GenericDeclaration(
                types = listOf(typeName),
                nullable = typeName.isNullable,
                recursive = false,
                castReturnType = true
            )
        }
    }

    private fun determineRecursiveType(
        typeName: TypeName,
        nested: GenericDeclaration,
        nullable: Boolean
    ): List<TypeName> {
        val types = if (nested.recursive) {
            List(nested.types.size) { nullableAny }
        } else {
            nested.types
        }

        return listOf(
            TypeVariableName(
                "$typeName<${types.joinToString(", ")}>"
            ).copy(nullable = nullable)
        )
    }

    private fun filterRecursiveTypes(
        typeName: TypeName,
        nested: GenericDeclaration,
        nullable: Boolean
    ): GenericDeclaration {
        val types = determineRecursiveType(
            typeName = typeName,
            nested = nested,
            nullable = nullable
        )

        return GenericDeclaration(
            types = types,
            nullable = nullable,
            recursive = false,
            castReturnType = true
        )
    }

    private fun TypeName.stripAlias(): TypeName {
        return if (this is ParameterizedTypeName) {
            this.rawType()
        } else {
            this
        }
    }

    private fun KSType.createTypeName(
        typeResolver: TypeParameterResolver
    ): TypeName {
        val declaration = this.declaration
        return if (declaration is KSTypeAlias) {
            declaration.type.toTypeName(typeResolver).stripAlias()
        } else {
            this.toClassName()
        }
    }

    private fun mergeNestedGeneric(
        type: KSType,
        nested: GenericDeclaration?,
        typeResolver: TypeParameterResolver
    ): GenericDeclaration? {
        return if (nested == null) {
            null
        } else {
            val isNullable = isNullable(type)
            val typeName = type.createTypeName(typeResolver)

            filterRecursiveTypes(
                typeName = typeName,
                nested = nested,
                nullable = isNullable
            )
        }
    }

    private fun resolveGeneric(
        visited: Set<String>,
        rootNullability: Boolean,
        type: KSType,
        resolved: Map<String, GenericDeclaration>,
        allGenerics: Set<String>,
        typeResolver: TypeParameterResolver
    ): GenericDeclaration? {
        return if (type.arguments.isEmpty()) {
            resolveTypeName(
                visited = visited,
                rootNullability = rootNullability,
                type = type,
                resolved = resolved,
                allGenerics = allGenerics,
                typeResolver = typeResolver
            )
        } else {
            val nestedGeneric = determineType(
                visited = visited,
                rootNullability = rootNullability,
                types = type.arguments.map { ksTypeArgument -> ksTypeArgument.type!! },
                resolved = resolved,
                allGenerics = allGenerics,
                typeResolver = typeResolver
            )

            mergeNestedGeneric(
                type = type,
                nested = nestedGeneric,
                typeResolver = typeResolver,
            )
        }
    }

    private fun resolveNullableAny(): GenericDeclaration {
        return GenericDeclaration(
            types = nullableAnys,
            recursive = false,
            nullable = true
        )
    }

    private fun determineRootNullability(
        types: List<KSType>
    ): Boolean {
        var nullable = false

        types.forEach { type ->
            nullable = nullable || isNullable(type)
        }

        return nullable
    }

    private fun isNullableGeneric(
        nested: GenericDeclaration,
        isNullable: Boolean?
    ): Boolean {
        return if (isNullable == null) {
            nested.nullable
        } else {
            nested.nullable && isNullable
        }
    }

    private fun resolveMultiType(
        visited: Set<String>,
        nullable: Boolean,
        types: List<KSType>,
        resolved: Map<String, GenericDeclaration>,
        allGenerics: Set<String>,
        typeResolver: TypeParameterResolver,
    ): GenericDeclaration? {
        val accumulatedTypes: MutableList<TypeName> = mutableListOf()
        var isNullable: Boolean? = null
        var isRecursive = false
        var doCastOnReturn = false

        types.map { type ->
            val nested = resolveGeneric(
                visited = visited,
                rootNullability = nullable,
                type = type,
                resolved = resolved,
                allGenerics = allGenerics,
                typeResolver = typeResolver
            )

            if (nested == null) {
                return null
            } else {
                isRecursive = isRecursive || nested.recursive
                isNullable = isNullableGeneric(nested, isNullable)
                doCastOnReturn = doCastOnReturn || nested.recursive

                accumulatedTypes.addAll(nested.types)
            }
        }

        return GenericDeclaration(
            types = accumulatedTypes,
            nullable = isNullable!!,
            recursive = isRecursive,
            castReturnType = doCastOnReturn
        )
    }

    private fun determineType(
        visited: Set<String>,
        types: List<KSTypeReference>,
        resolved: Map<String, GenericDeclaration>,
        allGenerics: Set<String>,
        typeResolver: TypeParameterResolver,
        rootNullability: Boolean? = null,
    ): GenericDeclaration? {
        return when {
            types.isEmpty() -> resolveNullableAny()
            types.size == 1 -> {
                val type = types.first().resolve()

                val nullable = rootNullability ?: determineRootNullability(listOf(type))

                resolveGeneric(
                    visited = visited,
                    rootNullability = nullable,
                    type = type,
                    resolved = resolved,
                    allGenerics = allGenerics,
                    typeResolver = typeResolver
                )
            }
            else -> {
                val ksTypes = types.map { type -> type.resolve() }

                val nullable = rootNullability ?: determineRootNullability(ksTypes)

                resolveMultiType(
                    visited = visited,
                    nullable = nullable,
                    types = ksTypes,
                    resolved = resolved,
                    allGenerics = allGenerics,
                    typeResolver = typeResolver
                )
            }
        }
    }

    override fun mapProxyGenerics(
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): Map<String, GenericDeclaration> {
        val raw = generics.toMutableMap()
        val resolved: MutableMap<String, GenericDeclaration> = mutableMapOf()
        val allGenerics = raw.keys
        val visited: MutableSet<String> = mutableSetOf()

        while (resolved.keys != allGenerics) {
            raw.forEach { (root, declaration) ->
                if (root !in resolved) {
                    visited.add(root)

                    val type = determineType(
                        visited = visited,
                        types = declaration,
                        resolved = resolved,
                        allGenerics = allGenerics,
                        typeResolver = typeResolver,
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
        resolver: TypeParameterResolver
    ): TypeName {
        return if (template.typeParameters.isEmpty()) {
            template.toClassName()
        } else {
            template.toClassName()
                .parameterizedBy(
                    template.typeParameters.map { type ->
                        type.toTypeVariableName(resolver)
                    }
                )
        }
    }

    override fun resolveKMockFactoryType(
        name: String,
        templateSource: TemplateSource
    ): TypeVariableName {
        val template = templateSource.template
        val typeResolver = template.typeParameters.toTypeParameterResolver()
        val boundary = resolveMockClassType(template, typeResolver)

        return TypeVariableName(name, bounds = listOf(boundary)).copy(reified = true)
    }

    private fun List<KSTypeReference>.toTypeNames(
        resolver: TypeParameterResolver
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
        resolver: TypeParameterResolver,
    ): Map<String, List<TypeName>>? {
        return generics?.map { (key, rawTypes) -> key to rawTypes.resolveTypeNames(resolver) }?.toMap()
    }
}
