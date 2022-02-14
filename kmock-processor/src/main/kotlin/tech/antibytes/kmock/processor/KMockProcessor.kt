/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import tech.antibytes.kmock.MagicStub

/*
 * Notices -> No deep checking in order to no drain performance
 */

class KMockProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
    private val aggregator: ProcessorContract.Aggregator
) : SymbolProcessor {
    private fun fetchAnnotated(resolver: Resolver): Sequence<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(
            MagicStub::class.qualifiedName!!,
            false
        )
    }

    @OptIn(KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val validAnnotations: MutableList<KSAnnotation> = mutableListOf()
        val annotated = fetchAnnotated(resolver)

        val illAnnotated = aggregator.extractInterfaces(annotated, validAnnotations)

        return illAnnotated
    }
}
