/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Annotation to determine which interfaces are about to be stubbed
 *
 * @param interfaces variable amount of interfaces
 */
annotation class Mock(vararg val interfaces: KClass<*>)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Annotation to determine which interfaces are about to be stubbed for commonCode
 *
 * @param interfaces variable amount of interfaces
 */
annotation class MockCommon(vararg val interfaces: KClass<*>)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Annotation to determine which interfaces are about to be stubbed for a shared source
 *
 * @param marker to identify the corresponding source set
 * @param interfaces variable amount of interfaces
 */
annotation class MockShared(val marker: String, vararg val interfaces: KClass<*>)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
/**
 * Annotation to determine a relaxer. The Processor will use only the first specified relaxer.
 * Note: The relaxer must match the following form or the Processor fails:
 * ```
 *   inline fun <reified T> relax(id: String, mock: Any): T {
 *      ...
 *   }
 * ```
 *
 * The Processor will delegate the id of the mocked/stubbed function or property.
 */
annotation class Relaxer
