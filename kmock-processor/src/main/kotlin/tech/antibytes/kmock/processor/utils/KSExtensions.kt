/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.isOpen
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import tech.antibytes.kmock.processor.ProcessorContract.GenericDeclaration
import tech.antibytes.kmock.processor.ProcessorContract.TemplateMultiSource
import tech.antibytes.kmock.processor.kotlinpoet.toProxyPairTypeName
import tech.antibytes.kmock.processor.kotlinpoet.toTypeParameterResolver

internal fun KSClassDeclaration.deriveSimpleName(packageName: String): String {
    val qualifiedName = ensureNotNullClassName(this.qualifiedName?.asString())

    return if (!qualifiedName.startsWith(packageName) || qualifiedName == packageName) {
        throw IllegalStateException("Malformed class name!")
    } else {
        qualifiedName.substringAfter("$packageName.")
    }
}

internal fun KSDeclaration.isPublicOpen(): Boolean = this.isPublic() && this.isOpen()

internal fun KSClassDeclaration.isInherited(
    parents: TemplateMultiSource?,
): Boolean = parents != null || this.superTypes.firstOrNull() != null

private fun KSTypeReference.resolveReceiver(
    generics: Map<String, GenericDeclaration>?,
    receiverTypeResolver: TypeParameterResolver,
): Pair<TypeName, TypeName> {
    return this.toProxyPairTypeName(
        generics = generics ?: emptyMap(),
        typeParameterResolver = receiverTypeResolver,
    )
}

internal fun KSPropertyDeclaration.resolveReceiver(
    generics: Map<String, GenericDeclaration>?,
    receiverTypeResolver: TypeParameterResolver,
): Pair<TypeName, TypeName> = this.extensionReceiver!!.resolveReceiver(generics, receiverTypeResolver)

internal fun KSFunctionDeclaration.resolveReceiver(
    generics: Map<String, GenericDeclaration>?,
    receiverTypeResolver: TypeParameterResolver,
): Pair<TypeName, TypeName> = this.extensionReceiver!!.resolveReceiver(generics, receiverTypeResolver)

internal fun KSPropertyDeclaration.isReceiverMethod(): Boolean = this.extensionReceiver != null

internal fun KSPropertyDeclaration.toReceiverTypeParameterResolver(
    parentResolver: TypeParameterResolver,
): TypeParameterResolver {
    val receiver = this.extensionReceiver?.resolve()?.declaration
    return receiver?.parentDeclaration?.typeParameters?.toTypeParameterResolver(parentResolver) ?: parentResolver
}

internal fun KSFunctionDeclaration.isReceiverMethod(): Boolean = this.extensionReceiver != null

internal fun KSFunctionDeclaration.toReceiverTypeParameterResolver(
    parentResolver: TypeParameterResolver,
): TypeParameterResolver {
    val receiver = this.extensionReceiver?.resolve()?.declaration

    return receiver?.parentDeclaration?.typeParameters?.toTypeParameterResolver(parentResolver) ?: parentResolver
}

internal fun KSTypeReference.extractParameter(): List<KSTypeArgument> {
    return element?.typeArguments.orEmpty()
}
