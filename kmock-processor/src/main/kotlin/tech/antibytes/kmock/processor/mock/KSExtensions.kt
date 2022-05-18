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
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import tech.antibytes.kmock.processor.KMockGenerics
import tech.antibytes.kmock.processor.ProcessorContract

internal fun KSDeclaration.isPublicOpen(): Boolean = this.isPublic() && this.isOpen()

internal fun KSClassDeclaration.isInherited(
    parents: ProcessorContract.TemplateMultiSource?
): Boolean = parents != null || this.superTypes.firstOrNull() != null

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

private fun KSType.resolveReceiver(
    typeResolver: TypeParameterResolver,
    receiverTypeResolver: TypeParameterResolver,
): TypeName {
    val arguments = this.innerArguments

    return if (declaration is KSClassDeclaration) {
        (declaration as KSClassDeclaration).toClassName().safeParameterize(
            parameters = declaration.typeParameters,
            arguments = arguments,
            resolver = typeResolver
        )
    } else {
        val generics = KMockGenerics.extractGenerics(
            template = declaration.parentDeclaration!!,
            resolver = receiverTypeResolver
        )

        KMockGenerics.mapDeclaredGenerics(
            generics = generics!!,
            typeResolver = receiverTypeResolver
        ).first()
    }
}

internal fun KSPropertyDeclaration.resolveReceiver(
    typeResolver: TypeParameterResolver,
    receiverTypeResolver: TypeParameterResolver,
): TypeName = this.extensionReceiver!!.resolve().resolveReceiver(typeResolver, receiverTypeResolver)

internal fun KSFunctionDeclaration.resolveReceiver(
    typeResolver: TypeParameterResolver,
    receiverTypeResolver: TypeParameterResolver,
): TypeName = this.extensionReceiver!!.resolve().resolveReceiver(typeResolver, receiverTypeResolver)

internal fun KSPropertyDeclaration.isReceiverMethod(): Boolean = this.extensionReceiver != null

private fun KSDeclaration.resolveReceiverBoundaries(
    typeParameterResolver: TypeParameterResolver?
): Map<String, List<KSTypeReference>>? {
    return if (this is KSClassDeclaration) {
        null
    } else {
        val resolver = this.parentDeclaration!!.typeParameters.toTypeParameterResolver(typeParameterResolver)
        return KMockGenerics.extractGenerics(
            template = this.parentDeclaration!!,
            resolver = resolver
        )
    }
}

internal fun KSPropertyDeclaration.toReceiverTypeParameterResolver(
    parentResolver: TypeParameterResolver
): TypeParameterResolver {
    val receiver = this.extensionReceiver?.resolve()?.declaration
    return receiver?.parentDeclaration?.typeParameters?.toTypeParameterResolver(parentResolver) ?: parentResolver
}

internal fun KSPropertyDeclaration.resolveReceiverBoundaries(
    typeParameterResolver: TypeParameterResolver?
): Map<String, List<KSTypeReference>>? {
    return if (this.extensionReceiver == null) {
        null
    } else {
        val extension = this.extensionReceiver!!.resolve()

        return extension.declaration.resolveReceiverBoundaries(typeParameterResolver)
    }
}

internal fun KSFunctionDeclaration.isReceiverMethod(): Boolean = this.extensionReceiver != null
