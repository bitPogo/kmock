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
 * Annotation to determine which interfaces are about to be stubbed/mocked.
 *
 * @param interfaces variable amount of interfaces
 */
annotation class Mock(vararg val interfaces: KClass<*>)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Annotation to determine which interfaces are about to be stubbed/mocked for commonCode
 *
 * @param interfaces variable amount of interfaces
 */
annotation class MockCommon(vararg val interfaces: KClass<*>)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Annotation to determine which interfaces are about to be stubbed/mocked for a shared source.
 *
 * @param sourceSetName to bind the given interface to a sourceSet (eg. nativeTest).
 * @param interfaces variable amount of interfaces.
 */
annotation class MockShared(val sourceSetName: String, vararg val interfaces: KClass<*>)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
/**
 * Annotation to determine a Relaxer. The Processor will use only the first specified relaxer.
 * Note: The relaxer must match the following form or the Processor fails:
 * ```
 *   fun <T> relax(id: String): T {
 *      ...
 *   }
 * ```
 * or:
 * ```
 *  inline fun <reified T> relax(id: String): T {
 *      ...
 *  }
 * ```
 *
 * The Processor will delegate the id of the Proxy which will invoke the Relaxer.
 * @see KMockContract.Relaxer
 */
annotation class Relaxer
