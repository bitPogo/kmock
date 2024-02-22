/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.aggregation

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ksp.toClassName
import java.io.File
import tech.antibytes.kmock.processor.ProcessorContract.AnnotationContainer
import tech.antibytes.kmock.processor.ProcessorContract.Companion.supportedPlatforms
import tech.antibytes.kmock.processor.ProcessorContract.GenericResolver

internal abstract class BaseSourceAggregator(
    private val logger: KSPLogger,
    private val customAnnotations: Map<String, String>,
    private val generics: GenericResolver,
) {
    private fun resolveIndicator(path: String?): String? {
        return path?.substringAfter("src${File.separator}")?.substringBefore(File.separator)
    }

    protected fun resolveKmockAnnotation(annotations: Sequence<KSAnnotated>): AnnotationContainer {
        val common: MutableList<KSAnnotated> = mutableListOf()
        val shared: MutableMap<String, MutableList<KSAnnotated>> = mutableMapOf()
        val platform: MutableList<KSAnnotated> = mutableListOf()

        annotations.forEach { ksAnnotated ->
            val indicator = resolveIndicator(ksAnnotated.containingFile?.filePath)

            when {
                indicator != null && indicator in supportedPlatforms -> platform.add(ksAnnotated)
                indicator == "commonTest" -> common.add(ksAnnotated)
                indicator?.endsWith("Test") == true -> {
                    val sources = shared.getOrElse(indicator) { mutableListOf() }
                    sources.add(ksAnnotated)

                    shared[indicator] = sources
                }
                else -> logger.warn("Unprocessable source.", ksAnnotated)
            }
        }

        return AnnotationContainer(
            common = common,
            shared = shared,
            platform = platform,
        )
    }

    protected fun resolveAnnotationName(
        annotation: KSAnnotation,
    ): String = annotation.annotationType.resolve().declaration.qualifiedName!!.asString()

    protected fun findKMockAnnotation(
        annotations: Sequence<KSAnnotation>,
        condition: (String, KSAnnotation) -> Boolean,
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
        resolver: Resolver,
    ): Array<Sequence<KSAnnotated>> {
        return customAnnotations.keys.map { annotation ->
            resolver.getSymbolsWithAnnotation(
                annotation,
                false,
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
                logger.warn("Cannot stub non interfaces.")
                throw IllegalArgumentException("Cannot stub non interfaces.")
            }
            interfaze.classKind != ClassKind.INTERFACE -> {
                logger.warn("Cannot stub non interface ${interfaze.toClassName()}.")
                throw IllegalArgumentException("Cannot stub non interface ${interfaze.toClassName()}.")
            }
            else -> interfaze
        }
    }
}
