/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.aggregation

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ksp.toClassName
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver

internal abstract class BaseSourceAggregator(
    private val logger: KSPLogger,
    private val customAnnotations: Map<String, String>,
    private val generics: GenericResolver,
) {
    protected fun resolveAnnotationName(
        annotation: KSAnnotation
    ): String = annotation.annotationType.resolve().declaration.qualifiedName!!.asString()

    protected fun findKMockAnnotation(
        annotations: Sequence<KSAnnotation>,
        condition: (String, KSAnnotation) -> Boolean
    ): KSAnnotation? {
        val annotation = annotations.firstOrNull { annotation ->
            condition(resolveAnnotationName(annotation), annotation)
        }

        return annotation
    }

    protected fun resolveGenerics(template: KSDeclaration): Map<String, List<KSTypeReference>>? {
        return generics.extractGenerics(template)
    }

    protected fun fetchCustomShared(
        resolver: Resolver
    ): Array<Sequence<KSAnnotated>> {
        return customAnnotations.keys.map { annotation ->
            resolver.getSymbolsWithAnnotation(
                annotation,
                false
            )
        }.toTypedArray()
    }

    protected fun String?.ensureTestSourceSet(): String? {
        return when {
            this == null -> null
            !this.endsWith("Test") -> "${this}Test"
            else -> this
        }
    }

    protected fun safeCastInterface(interfaze: KSDeclaration): KSClassDeclaration {
        return when {
            interfaze !is KSClassDeclaration -> {
                logger.error("Cannot stub non interfaces.")
                throw IllegalArgumentException("Cannot stub non interfaces.")
            }
            interfaze.classKind != ClassKind.INTERFACE -> {
                logger.error("Cannot stub non interface ${interfaze.toClassName()}.")
                throw IllegalArgumentException("Cannot stub non interface ${interfaze.toClassName()}.")
            }
            else -> interfaze
        }
    }
}
