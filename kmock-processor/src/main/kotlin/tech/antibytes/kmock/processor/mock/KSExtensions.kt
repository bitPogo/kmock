/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.mock

import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.isOpen
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import tech.antibytes.kmock.processor.KMockGenerics

internal fun KSDeclaration.isPublicOpen(): Boolean = this.isPublic() && this.isOpen()

private fun ClassName.safeParameterize(
    parameters: List<KSTypeParameter>,
    arguments: List<KSTypeArgument>,
    resolver: TypeParameterResolver
): TypeName {
    return if (parameters.isEmpty()) {
        this
    } else {
        this.parameterizedBy(
            arguments.map { argument -> argument.toTypeName(resolver) }
        )
    }
}

private fun resolveReceiver(
    declaration: KSDeclaration,
    arguments: List<KSTypeArgument>,
    parentResolver: TypeParameterResolver
): TypeName {
    return if (declaration is KSClassDeclaration) {
        declaration
            .toClassName()
            .safeParameterize(
                parameters = declaration.typeParameters,
                arguments = arguments,
                resolver = parentResolver
            )
    } else {
        val resolver = declaration.parentDeclaration!!.typeParameters.toTypeParameterResolver(
            declaration.typeParameters.toTypeParameterResolver()
        )
        val generics = KMockGenerics.extractGenerics(
            template = declaration.parentDeclaration!!,
            resolver = resolver
        )

        KMockGenerics.mapDeclaredGenerics(
            generics = generics!!,
            typeResolver = resolver
        ).first()
    }
}

internal fun KSPropertyDeclaration.determineScope(): TypeName? {
    return if (this.extensionReceiver == null) {
        null
    } else {
        val extension = this.extensionReceiver!!.resolve()
        val arguments = extension.innerArguments
        val resolver = this.typeParameters.toTypeParameterResolver()

        return resolveReceiver(
            declaration = extension.declaration,
            arguments = arguments,
            parentResolver = resolver
        )
    }
}

internal fun KSFunctionDeclaration.determineScope(): TypeName? {
    return if (this.extensionReceiver == null) {
        null
    } else {
        val extension = this.extensionReceiver!!.resolve()
        val arguments = extension.innerArguments
        val resolver = this.typeParameters.toTypeParameterResolver()

        return resolveReceiver(
            declaration = extension.declaration,
            arguments = arguments,
            parentResolver = resolver
        )
    }
}
