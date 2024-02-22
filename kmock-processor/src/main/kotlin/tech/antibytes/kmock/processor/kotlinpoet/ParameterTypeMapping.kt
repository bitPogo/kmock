/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

@file:Suppress("ktlint:standard:filename")

package tech.antibytes.kmock.processor.kotlinpoet

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Variance
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass
import tech.antibytes.kmock.processor.ProcessorContract.Companion.ANY
import tech.antibytes.kmock.processor.ProcessorContract.Companion.NULLABLE_ANY
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration

// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt#L1
// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/utils.kt#L16
private data class ArgumentMappingDecorator(
    val arguments: List<TypeName>,
    val recursive: Boolean,
    val castReturnType: Boolean,
)

private fun TypeName.amendTag(
    amend: Function1<MutableMap<KClass<*>, Any>, Unit>,
): TypeName {
    val tags = tags.toMutableMap()
    amend(tags)

    return copy(tags = tags)
}

private fun TypeName.tagTypeFromDecorator(
    markedAsNullable: Boolean,
    decorator: ArgumentMappingDecorator?,
): TypeName {
    return if (decorator == null) {
        this
    } else {
        amendTag { tags ->
            tags[GenericDeclaration::class] = GenericDeclaration(
                types = listOf(this),
                isNullable = isNullable || markedAsNullable,
                isRecursive = decorator.recursive,
                doCastReturnType = decorator.castReturnType,
            )
        }
    }
}

private fun TypeName.transferGenericDeclaration(
    parent: TypeName,
): TypeName {
    val declaration = parent.resolveGenericDeclaration()

    return if (declaration == null) {
        this
    } else {
        this.amendTag { tags ->
            tags[GenericDeclaration::class] = GenericDeclaration(
                types = listOf(this),
                isRecursive = declaration.isRecursive,
                doCastReturnType = declaration.doCastReturnType,
                isNullable = this.isNullable,
            )
        }
    }
}

private fun KSTypeArgument.resolveVariance(
    type: KSTypeReference,
    visited: Set<String>,
    classScope: Set<String>,
    functionScope: Set<String>,
    rootNullability: Boolean,
    resolved: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
): TypeName {
    return when (variance) {
        Variance.COVARIANT -> {
            val typeName = type.mapParameterType(
                visited = visited,
                classScope = classScope,
                functionScope = functionScope,
                rootNullability = rootNullability,
                resolved = resolved,
                typeParameterResolver = typeParameterResolver,
            )
            WildcardTypeName.producerOf(typeName).transferGenericDeclaration(typeName)
        }
        Variance.CONTRAVARIANT -> {
            val typeName = type.mapParameterType(
                visited = visited,
                classScope = classScope,
                functionScope = functionScope,
                rootNullability = rootNullability,
                resolved = resolved,
                typeParameterResolver = typeParameterResolver,
            )
            WildcardTypeName.consumerOf(typeName).transferGenericDeclaration(typeName)
        }
        Variance.STAR -> STAR_WITH_DECLARATION
        Variance.INVARIANT -> {
            type.mapParameterType(
                visited = visited,
                classScope = classScope,
                functionScope = functionScope,
                rootNullability = rootNullability,
                resolved = resolved,
                typeParameterResolver = typeParameterResolver,
            )
        }
    }
}

private fun KSTypeArgument.mapParameterType(
    visited: Set<String>,
    classScope: Set<String>,
    functionScope: Set<String>,
    rootNullability: Boolean,
    resolved: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
): TypeName {
    return if (type == null) {
        STAR_WITH_DECLARATION
    } else {
        resolveVariance(
            type = type!!,
            visited = visited,
            classScope = classScope,
            functionScope = functionScope,
            rootNullability = rootNullability,
            resolved = resolved,
            typeParameterResolver = typeParameterResolver,
        )
    }
}

internal fun TypeName.resolveGenericDeclaration(): GenericDeclaration? {
    return tag(GenericDeclaration::class)?.run {
        copy(
            types = this.types.map { type ->
                if (type != this@resolveGenericDeclaration) {
                    type.copy(nullable = this@resolveGenericDeclaration.isNullable)
                } else {
                    type
                }
            },
        )
    }
}

private fun List<KSTypeArgument>.mapParameterType(
    visited: Set<String>,
    classScope: Set<String>,
    functionScope: Set<String>,
    rootNullability: Boolean,
    resolved: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
): ArgumentMappingDecorator? {
    var isRecursive = false
    var doCastOnReturn = false

    val mappedArguments = map { argument ->
        val resolvedArgument = argument.mapParameterType(
            typeParameterResolver = typeParameterResolver,
            visited = visited,
            classScope = classScope,
            functionScope = functionScope,
            rootNullability = rootNullability,
            resolved = resolved,
        )

        val genericDeclaration = resolvedArgument.resolveGenericDeclaration()
            ?: return null

        isRecursive = isRecursive || genericDeclaration.isRecursive
        doCastOnReturn = doCastOnReturn || genericDeclaration.doCastReturnType

        resolvedArgument
    }

    return ArgumentMappingDecorator(
        arguments = mappedArguments,
        recursive = isRecursive,
        castReturnType = doCastOnReturn,
    )
}

private fun List<KSTypeArgument>.resolveUntaggedTypes(
    visited: Set<String>,
    classScope: Set<String>,
    functionScope: Set<String>,
    rootNullability: Boolean,
    resolved: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
): List<TypeName> {
    return map { argument ->
        argument.mapParameterType(
            typeParameterResolver = typeParameterResolver,
            visited = visited,
            classScope = classScope,
            functionScope = functionScope,
            rootNullability = rootNullability,
            resolved = resolved,
        )
    }
}

private fun ArgumentMappingDecorator?.resolveArguments(
    arguments: List<KSTypeArgument>,
    classScope: Set<String>,
    functionScope: Set<String>,
    rootNullability: Boolean,
    visited: Set<String>,
    resolved: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
): List<TypeName> {
    return this?.arguments
        ?: arguments.resolveUntaggedTypes(
            typeParameterResolver = typeParameterResolver,
            visited = visited,
            classScope = classScope,
            functionScope = functionScope,
            rootNullability = rootNullability,
            resolved = resolved,
        )
}

private fun KSType.abbreviateType(
    visited: Set<String>,
    classScope: Set<String>,
    functionScope: Set<String>,
    resolved: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
    rootNullability: Boolean,
    markedAsNullable: Boolean,
    typeArguments: List<KSTypeArgument>,
): TypeName {
    val argumentsDecorator = typeArguments.mapParameterType(
        visited = visited,
        classScope = classScope,
        functionScope = functionScope,
        rootNullability = rootNullability,
        resolved = resolved,
        typeParameterResolver = typeParameterResolver,
    )

    val type = mapParameterType(
        visited = visited,
        classScope = classScope,
        functionScope = functionScope,
        resolved = resolved,
        rootNullability = rootNullability,
        typeArguments = emptyList(),
        typeParameterResolver = typeParameterResolver,
    ).rawType()
        .withTypeArguments(
            argumentsDecorator.resolveArguments(
                arguments = typeArguments,
                visited = visited,
                classScope = classScope,
                functionScope = functionScope,
                rootNullability = rootNullability,
                resolved = resolved,
                typeParameterResolver = typeParameterResolver,
            ),
        ).copy(nullable = markedAsNullable)

    return type.tagTypeFromDecorator(markedAsNullable, argumentsDecorator)
}

internal fun KSTypeReference.mapParameterType(
    visited: Set<String>,
    classScope: Set<String>,
    functionScope: Set<String>,
    resolved: Map<String, GenericDeclaration>,
    rootNullability: Boolean,
    typeParameterResolver: TypeParameterResolver,
): TypeName {
    return resolve().mapParameterType(
        visited = visited,
        classScope = classScope,
        functionScope = functionScope,
        resolved = resolved,
        rootNullability = rootNullability,
        typeParameterResolver = typeParameterResolver,
        typeArguments = element?.typeArguments.orEmpty(),
    )
}

private fun TypeName.tagWithGenericDeclaration(
    recursive: Boolean,
    nullable: Boolean,
    castReturnType: Boolean,
): TypeName {
    return amendTag { tags ->
        tags[GenericDeclaration::class] = GenericDeclaration(
            types = listOf(this),
            isRecursive = recursive,
            isNullable = nullable,
            doCastReturnType = castReturnType,
        )
    }
}

private fun GenericDeclaration?.resolveEventuallyKnownType(
    markedAsNullable: Boolean,
    typeReference: TypeVariableName,
): TypeName {
    return when {
        this == null -> typeReference
        types.size > 1 -> ANY.copy(
            nullable = isNullable || markedAsNullable,
            tags = mapOf(
                GenericDeclaration::class to GenericDeclaration(
                    types = types,
                    isRecursive = isRecursive,
                    isNullable = isNullable || markedAsNullable,
                    doCastReturnType = true,
                ),
            ),
        )
        else -> types.first().amendTag { tags ->
            tags[GenericDeclaration::class] = GenericDeclaration(
                types = types,
                isRecursive = isRecursive,
                isNullable = isNullable || markedAsNullable,
                doCastReturnType = doCastReturnType,
            )
        }
    }
}

private fun TypeVariableName.resolveTypeVariable(
    visited: Set<String>,
    classScope: Set<String>,
    functionScope: Set<String>,
    rootNullability: Boolean,
    nullable: Boolean,
    resolved: Map<String, GenericDeclaration>,
): TypeName {
    val declaration = resolved[name]

    return when {
        declaration == null && name in visited -> {
            ANY.tagWithGenericDeclaration(
                recursive = true,
                nullable = rootNullability,
                castReturnType = true,
            )
        }
        name in classScope && name !in functionScope -> {
            this.tagWithGenericDeclaration(
                recursive = false,
                nullable = nullable,
                castReturnType = true,
            )
        }
        name !in classScope && name !in functionScope -> { // Generic Types of Aliases
            ANY.tagWithGenericDeclaration(
                recursive = false,
                nullable = true,
                castReturnType = true,
            )
        }
        else -> declaration.resolveEventuallyKnownType(
            typeReference = this,
            markedAsNullable = nullable,
        )
    }
}

private fun ClassName.markAsNonGeneric(
    nullable: Boolean,
): ClassName {
    return amendTag { tags ->
        tags[GenericDeclaration::class] = NON_GENERIC.copy(
            types = listOf(this),
            isNullable = nullable,
        )
    } as ClassName
}

private fun ClassName.resolveClassType(
    arguments: List<KSTypeArgument>,
    argumentsDecorator: ArgumentMappingDecorator?,
    rootNullability: Boolean,
    markedAsNullable: Boolean,
    visited: Set<String>,
    classScope: Set<String>,
    functionScope: Set<String>,
    resolved: Map<String, GenericDeclaration>,
    typeParameterResolver: TypeParameterResolver,
): TypeName {
    return if (arguments.isEmpty()) {
        markAsNonGeneric(markedAsNullable)
    } else {
        withTypeArguments(
            argumentsDecorator.resolveArguments(
                arguments = arguments,
                visited = visited,
                classScope = classScope,
                functionScope = functionScope,
                rootNullability = rootNullability,
                resolved = resolved,
                typeParameterResolver = typeParameterResolver,
            ),
        ).tagTypeFromDecorator(markedAsNullable, argumentsDecorator)
    }
}

private fun TypeName.resolveNullability(
    rootNullability: Boolean,
    isMarkedNullable: Boolean,
): TypeName {
    val genericTag = tag(GenericDeclaration::class)

    return if (this == ANY && genericTag?.isRecursive == true) {
        copy(nullable = isMarkedNullable || rootNullability)
    } else {
        copy(nullable = isMarkedNullable || isNullable)
    }
}

// see: https://github.com/square/kotlinpoet/blob/9af3f67bb4338f6f35fcd29cb9228227981ae1ce/interop/ksp/src/main/kotlin/com/squareup/kotlinpoet/ksp/ksTypes.kt#L60
private fun KSType.mapParameterType(
    visited: Set<String>,
    classScope: Set<String>,
    functionScope: Set<String>,
    resolved: Map<String, GenericDeclaration>,
    typeArguments: List<KSTypeArgument>,
    rootNullability: Boolean,
    typeParameterResolver: TypeParameterResolver,
): TypeName {
    require(!isError) {
        "Error type '$this' is not resolvable in the current round of processing."
    }

    val type = when (val declaration = this.declaration) {
        is KSTypeParameter -> {
            typeParameterResolver[declaration.name.getShortName()]
                .resolveTypeVariable(
                    visited = visited,
                    classScope = classScope,
                    functionScope = functionScope,
                    resolved = resolved,
                    rootNullability = rootNullability,
                    nullable = isMarkedNullable,
                )
        }
        is KSClassDeclaration -> {
            val argumentsDecorator = typeArguments.mapParameterType(
                visited = visited,
                classScope = classScope,
                functionScope = functionScope,
                resolved = resolved,
                rootNullability = rootNullability,
                typeParameterResolver = typeParameterResolver,
            )

            declaration.toClassName().resolveClassType(
                arguments = typeArguments,
                argumentsDecorator = argumentsDecorator,
                markedAsNullable = isMarkedNullable,
                rootNullability = rootNullability,
                visited = visited,
                classScope = classScope,
                functionScope = functionScope,
                resolved = resolved,
                typeParameterResolver = typeParameterResolver,
            )
        }
        is KSTypeAlias -> {
            val (resolvedType, mappedArgs, extraResolver) = declaration.resolveAlias(
                arguments = typeArguments,
                typeParameterResolver = typeParameterResolver,
            )

            val abbreviatedType = resolvedType.abbreviateType(
                visited = visited,
                classScope = classScope,
                functionScope = functionScope,
                resolved = resolved,
                typeParameterResolver = extraResolver,
                markedAsNullable = isMarkedNullable,
                rootNullability = rootNullability,
                typeArguments = mappedArgs,
            )

            val aliasArgsDecorator = arguments.mapParameterType(
                visited = visited,
                classScope = classScope,
                functionScope = functionScope,
                rootNullability = rootNullability,
                resolved = resolved,
                typeParameterResolver = extraResolver,
            )

            declaration.parameterizedBy(
                abbreviateType = abbreviatedType,
                typeArguments = aliasArgsDecorator.resolveArguments(
                    arguments = typeArguments,
                    visited = visited,
                    classScope = classScope,
                    functionScope = functionScope,
                    rootNullability = rootNullability,
                    resolved = resolved,
                    typeParameterResolver = extraResolver,
                ),
            ).tagTypeFromDecorator(isMarkedNullable, aliasArgsDecorator)
        }
        else -> error("Unsupported type: $declaration")
    }

    return type.resolveNullability(
        isMarkedNullable = isMarkedNullable,
        rootNullability = rootNullability,
    )
}

private val STAR_WITH_DECLARATION = STAR.copy(
    tags = mapOf(
        GenericDeclaration::class to GenericDeclaration(
            types = listOf(NULLABLE_ANY),
            isRecursive = false,
            isNullable = true,
            doCastReturnType = false,
        ),
    ),
)

private val NON_GENERIC = GenericDeclaration(
    types = emptyList(),
    isRecursive = false,
    isNullable = false,
    doCastReturnType = false,
)
