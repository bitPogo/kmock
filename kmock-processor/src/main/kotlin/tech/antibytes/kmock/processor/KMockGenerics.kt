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
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.kmock.processor.utils.mapArgumentType

internal object KMockGenerics : GenericResolver {
    private val any = Any::class.asTypeName()
    private val nullableAnys = listOf(any.copy(nullable = true))
    private val nonNullableAnys = listOf(any.copy(nullable = false))
    private const val TYPE_PARAMETER = "KMockTypeParameter"

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
        root: String,
        rootNullability: Boolean,
        typeName: TypeName,
        resolved: Map<String, GenericDeclaration>,
    ): GenericDeclaration? {
        val typeNameStr = typeName.toString()
        val type = resolved[typeNameStr]

        return if (type == null && root == typeNameStr) {
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
                recursive = false,
                castReturnType = true
            )
        }
    }

    private fun determineRecursiveType(
        typeName: ClassName,
        nested: GenericDeclaration,
        nullable: Boolean
    ): List<TypeName> {
        val types = if (nested.recursive) {
            List(nested.types.size) { "Any?" }
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
        typeName: ClassName,
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

    private fun mergeNestedGeneric(
        type: KSType,
        nested: GenericDeclaration?
    ): GenericDeclaration? {
        return if (nested == null) {
            null
        } else {
            val isNullable = isNullable(type)
            val typeName = type.toClassName()

            filterRecursiveTypes(
                typeName = typeName,
                nested = nested,
                nullable = isNullable
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
            val nestedGeneric = determineType(
                root = root,
                rootNullability = rootNullability,
                types = type.arguments.map { ksTypeArgument -> ksTypeArgument.type!! },
                resolved = resolved,
                allGenerics = allGenerics,
                typeResolver = typeResolver
            )

            mergeNestedGeneric(
                type = type,
                nested = nestedGeneric
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
        var doCastOnReturn = false

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
}
