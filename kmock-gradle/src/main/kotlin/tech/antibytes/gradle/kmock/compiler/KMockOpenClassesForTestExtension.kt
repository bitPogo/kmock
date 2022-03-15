/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.compiler

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.extensions.DeclarationAttributeAltererExtension
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierListOwner

internal class KMockOpenClassesForTestExtension : DeclarationAttributeAltererExtension {
    private fun crackClassOpen(
        modifierListOwner: KtModifierListOwner,
        declaration: DeclarationDescriptor?,
        isImplicitModality: Boolean
    ): Modality? {
        val descriptor = declaration as? ClassDescriptor

        println("----")

        println(descriptor?.containingDeclaration?.name)

        println("----")

        return when {
            descriptor != null -> null
            !isImplicitModality && modifierListOwner.hasModifier(KtTokens.FINAL_KEYWORD) -> Modality.FINAL
            else -> Modality.OPEN
        }
    }

    override fun refineDeclarationModality(
        modifierListOwner: KtModifierListOwner,
        declaration: DeclarationDescriptor?,
        containingDeclaration: DeclarationDescriptor?,
        currentModality: Modality,
        isImplicitModality: Boolean
    ): Modality? {
        return if (currentModality == Modality.FINAL) {
            crackClassOpen(modifierListOwner, declaration, isImplicitModality)
        } else {
            null
        }
    }
}
