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
 * Determines which interfaces should be stubbed/mocked
 *
 * @param interfaces variable amount of interfaces.
 * @property interfaces which will be propagated to the (KSP) processor.
 * @author Matthias Geisler
 */
annotation class Mock(vararg val interfaces: KClass<*>)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Determines which interfaces should be stubbed/mocked for CommonCode.
 *
 * @param interfaces variable amount of interfaces.
 * @property interfaces which will be propagated to the (KSP) processor.
 * @author Matthias Geisler
 */
annotation class MockCommon(vararg val interfaces: KClass<*>)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Determines which interfaces should be stubbed/mocked for a shared source.
 *
 * @param sourceSetName to bind the given interface to a sourceSet (e.g. nativeTest).
 * @param interfaces variable amount of interfaces.
 * @property sourceSetName which will be propagated to the (KSP) processor.
 * @property interfaces which will be propagated to the (KSP) processor.
 * @author Matthias Geisler
 */
annotation class MockShared(val sourceSetName: String, vararg val interfaces: KClass<*>)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
/**
 * Determines a Relaxer (optional). The Processor will use only the first specified relaxer.
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
 * @author Matthias Geisler
 */
annotation class Relaxer
