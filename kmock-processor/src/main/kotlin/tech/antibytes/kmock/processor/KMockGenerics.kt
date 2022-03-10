/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver

internal object KMockGenerics : GenericResolver {
    private fun resolveBound(type: KSTypeParameter): List<KSTypeReference> = type.bounds.toList()

    override fun extractGenerics(
        template: KSDeclaration,
        resolver: TypeParameterResolver
    ): Map<String, List<KSTypeReference>>? {
        return if (template.typeParameters.isEmpty()) {
            null
        } else {
            val generic: MutableMap<String, List<KSTypeReference>> = mutableMapOf()
            template.typeParameters.forEach { type ->
                generic[type.toTypeVariableName(resolver).toString()] = resolveBound(type)
            }

            generic
        }
    }

    override fun mapDeclaredGenerics(
        generics: Map<String, List<KSTypeReference>>,
        typeResolver: TypeParameterResolver
    ): List<TypeVariableName> = generics.map { (type, bounds) ->
        TypeVariableName(
            type,
            bounds = bounds.map { ksReference -> ksReference.resolve().toTypeName(typeResolver) }
        )
    }

    private fun isNullable(type: KSType): Boolean = type.nullability == Nullability.NULLABLE

    private fun resolveKnownType(
        root: String,
        rootNullability: Boolean,
        typeName: TypeName,
        resolved: Map<String, GenericDeclaration>,
    ): GenericDeclaration? {
        val typeNameStr = typeName.toString()
        val type = resolved[typeNameStr]

        return if (type == null && root == typeNameStr) {
            GenericDeclaration(
                types = listOf(
                    TypeVariableName("Any").copy(nullable = rootNullability)
                ),
                nullable = rootNullability,
                recursive = true
            )
        } else {
            type
        }
    }

    private fun resolveTypeName(
        root: String,
        rootNullability: Boolean,
        type: KSType,
        resolved: Map<String, GenericDeclaration>,
        allGenerics: Set<String>,
        typeResolver: TypeParameterResolver
    ): GenericDeclaration? {
        val typeName = type.toTypeName(typeResolver)

        return if (typeName.toString() in allGenerics) {
            resolveKnownType(
                root,
                rootNullability,
                typeName,
                resolved
            )
        } else {
            GenericDeclaration(
                types = listOf(typeName),
                nullable = typeName.isNullable,
                recursive = false
            )
        }
    }

    private fun mergeNestedGeneric(
        type: KSType,
        nested: GenericDeclaration?
    ): GenericDeclaration? {
        return if (nested == null) {
            null
        } else {
            val isNullable = isNullable(type)
            val typeName = type.toClassName()

            GenericDeclaration(
                types = listOf(
                    TypeVariableName(
                        "$typeName<${nested.types.joinToString(", ")}>"
                    ).copy(nullable = isNullable)
                ),
                nullable = isNullable,
                recursive = nested.recursive
            )
        }
    }

    private fun resolveGeneric(
        root: String,
        rootNullability: Boolean,
        type: KSType,
        resolved: Map<String, GenericDeclaration>,
        allGenerics: Set<String>,
        typeResolver: TypeParameterResolver
    ): GenericDeclaration? {
        return if (type.arguments.isEmpty()) {
            resolveTypeName(
                root = root,
                rootNullability = rootNullability,
                type = type,
                resolved = resolved,
                allGenerics = allGenerics,
                typeResolver = typeResolver
            )
        } else {
            val nestGeneric = determineType(
                root = root,
                rootNullability = rootNullability,
                types = type.arguments.map { ksTypeArgument -> ksTypeArgument.type!! },
                resolved = resolved,
                allGenerics = allGenerics,
                typeResolver = typeResolver
            )

            mergeNestedGeneric(
                type,
                nestGeneric
            )
        }
    }

    private fun resolveNullableAny(): GenericDeclaration {
        return GenericDeclaration(
            types = listOf(TypeVariableName("Any?")),
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

    private fun resolveMultiType(
        root: String,
        nullable: Boolean,
        types: List<KSType>,
        resolved: Map<String, GenericDeclaration>,
        allGenerics: Set<String>,
        typeResolver: TypeParameterResolver,
    ): GenericDeclaration? {
        val accumulatedTypes: MutableList<TypeName> = mutableListOf()
        var isNullable: Boolean? = null
        var isRecursive = false

        types.map { type ->
            val nested = resolveGeneric(
                root = root,
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
                isNullable = if (isNullable == null) {
                    nested.nullable
                } else {
                    nested.nullable && isNullable!!
                }

                accumulatedTypes.addAll(nested.types)
            }
        }

        return GenericDeclaration(
            types = accumulatedTypes,
            nullable = isNullable!!,
            recursive = isRecursive
        )
    }

    private fun determineType(
        root: String,
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
                    root = root,
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
                    root = root,
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

        while (resolved.keys != allGenerics) {
            raw.forEach { (root, declaration) ->
                if (root !in resolved) {
                    val type = determineType(
                        root = root,
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
        }

        return resolved
    }
}
