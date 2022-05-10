/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.isLocal
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import com.squareup.kotlinpoet.tags.TypeAliasTag
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource

internal object KMockGenerics : GenericResolver {
    private val any = Any::class.asTypeName()
    private val nullableAnys = listOf(any.copy(nullable = true))
    private val nonNullableAnys = listOf(any.copy(nullable = false))

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

    private fun KSTypeArgument.mapArgumentType(
        typeParamResolver: TypeParameterResolver,
        mapping: Map<String, String>,
        typeArguments: List<KSTypeArgument>,
    ): TypeName {
        return this.type!!.resolve().toTypeName(
            typeParamResolver = typeParamResolver,
            mapping = mapping,
            typeArguments = typeArguments,
        )
    }

    // see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/utils.kt#L16
    private fun TypeName.rawType(): ClassName {
        return findRawType() ?: throw IllegalArgumentException("Cannot get raw type from $this")
    }

    private fun TypeName.findRawType(): ClassName? {
        return when (this) {
            is ClassName -> this
            is ParameterizedTypeName -> rawType
            is LambdaTypeName -> {
                var count = parameters.size
                if (receiver != null) {
                    count++
                }
                val functionSimpleName = if (count >= 23) {
                    "FunctionN"
                } else {
                    "Function$count"
                }
                ClassName("kotlin.jvm.functions", functionSimpleName)
            }
            else -> null
        }
    }

    private fun ClassName.withTypeArguments(arguments: List<TypeName>): TypeName {
        return if (arguments.isEmpty()) {
            this
        } else {
            this.parameterizedBy(arguments)
        }
    }

    private fun KSDeclaration.toClassNameInternal(): ClassName {
        require(!isLocal()) {
            "Local/anonymous classes are not supported!"
        }
        val pkgName = packageName.asString()
        val typesString = checkNotNull(qualifiedName).asString().removePrefix("$pkgName.")

        val simpleNames = typesString
            .split(".")
        return ClassName(pkgName, simpleNames)
    }

    private fun TypeVariableName.copy(name: String): TypeVariableName {
        return TypeVariableName(
            name,
            bounds = this.bounds,
            variance = this.variance,
        ).copy(
            reified = this.isReified,
            tags = this.tags,
            nullable = this.isNullable,
            annotations = this.annotations,
        )
    }

    // see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt#L60
    private fun KSType.toTypeName(
        typeParamResolver: TypeParameterResolver,
        mapping: Map<String, String>,
        typeArguments: List<KSTypeArgument>,
    ): TypeName {
        require(!isError) {
            "Error type '$this' is not resolvable in the current round of processing."
        }

        val type = when (val declaration = this.declaration) {
            is KSTypeParameter -> {
                val parameterType = typeParamResolver[declaration.name.getShortName()]

                if (parameterType.name in mapping) {
                    parameterType.copy(mapping[parameterType.name]!!)
                } else {
                    parameterType
                }
            }
            is KSClassDeclaration -> {
                declaration.toClassName().withTypeArguments(
                    arguments.map { argument ->
                        argument.mapArgumentType(
                            typeParamResolver = typeParamResolver,
                            mapping = mapping,
                            typeArguments = typeArguments,
                        )
                    }
                )
            }
            is KSTypeAlias -> {
                val extraResolver = if (declaration.typeParameters.isEmpty()) {
                    typeParamResolver
                } else {
                    declaration.typeParameters.toTypeParameterResolver(typeParamResolver)
                }

                val mappedArgs = arguments.map { argument ->
                    argument.mapArgumentType(
                        typeParamResolver = typeParamResolver,
                        mapping = mapping,
                        typeArguments = typeArguments,
                    )
                }

                val abbreviatedType = declaration.type.resolve()
                    .toTypeName(extraResolver)
                    .copy(nullable = isMarkedNullable)
                    .rawType()
                    .withTypeArguments(mappedArgs)

                val aliasArgs = typeArguments.map { argument ->
                    argument.mapArgumentType(
                        typeParamResolver = typeParamResolver,
                        mapping = mapping,
                        typeArguments = typeArguments,
                    )
                }

                declaration.toClassNameInternal()
                    .withTypeArguments(aliasArgs)
                    .copy(tags = mapOf(TypeAliasTag::class to TypeAliasTag(abbreviatedType)))
            }
            else -> error("Unsupported type: $declaration")
        }

        return type.copy(nullable = isMarkedNullable)
    }

    private fun mapTypes(
        generics: Map<String, List<KSTypeReference>>,
        suffix: Int,
    ): Map<String, String> {
        var counter = 0 + suffix
        return generics.map { (typeName, _) ->
            Pair(typeName, "T$counter").also { counter++ }
        }.toMap()
    }

    override fun mapDeclaredGenerics(
        generics: Map<String, List<KSTypeReference>>,
        suffix: Int,
        typeResolver: TypeParameterResolver
    ): List<TypeVariableName> {
        var counter = 0 + suffix
        val mapping = mapTypes(generics, suffix)
        return generics.map { (_, bounds) ->
            TypeVariableName(
                "T$counter",
                bounds = bounds.map { ksReference ->
                    ksReference.resolve().toTypeName(
                        typeParamResolver = typeResolver,
                        mapping = mapping,
                        typeArguments = emptyList(),
                    )
                }
            ).also { counter++ }
        }
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
                    template.typeParameters.map { type -> type.toTypeVariableName(resolver) }
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
