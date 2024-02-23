/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
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
public annotation class Mock(vararg val interfaces: KClass<*>)

@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Determines which interfaces should be stubbed/mocked as a union of them.
 *
 * @param name name which used for the mock.
 * @param interfaces variable amount of interfaces.
 * @property interfaces which will be propagated to the (KSP) processor.
 * @property name name which will be propagated to the (KSP) processor.
 * @author Matthias Geisler
 */
public annotation class MultiMock(
    val name: String,
    vararg val interfaces: KClass<*>,
)

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
public annotation class MockCommon(vararg val interfaces: KClass<*>)

@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Determines which interfaces should be stubbed/mocked as a union of them for CommonCode.
 *
 * @param name name which used for the mock.
 * @param interfaces variable amount of interfaces.
 * @property name name which will be propagated to the (KSP) processor.
 * @property interfaces which will be propagated to the (KSP) processor.
 * @author Matthias Geisler
 */
public annotation class MultiMockCommon(
    val name: String,
    vararg val interfaces: KClass<*>,
)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Determines which interfaces should be stubbed/mocked for a shared source.
 *
 * @param sourceSetName to bind the given interface to a sourceSet (e.g. nativeTest or native).
 * @param interfaces variable amount of interfaces.
 * @property sourceSetName which will be propagated to the (KSP) processor.
 * @property interfaces which will be propagated to the (KSP) processor.
 * @author Matthias Geisler
 */
public annotation class MockShared(val sourceSetName: String, vararg val interfaces: KClass<*>)

@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Determines which interfaces should be stubbed/mocked as a union of them a shared source.
 *
 * @param sourceSetName to bind the given interface to a sourceSet (e.g. nativeTest or native).
 * @param name name which used for the mock.
 * @param interfaces variable amount of interfaces.
 * @property sourceSetName which will be propagated to the (KSP) processor.
 * @property name name which will be propagated to the (KSP) processor.
 * @property interfaces which will be propagated to the (KSP) processor.
 * @author Matthias Geisler
 */
public annotation class MultiMockShared(
    val sourceSetName: String,
    val name: String,
    vararg val interfaces: KClass<*>,
)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Determines which interfaces should be stubbed/mocked for a arbitrary source set.
 *
 * @param interfaces variable amount of interfaces.
 * @property interfaces which will be propagated to the (KSP) processor.
 * @author Matthias Geisler
 */
@KMockExperimental
public annotation class KMock(
    vararg val interfaces: KClass<*>,
)

@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
/**
 * Determines which interfaces should be stubbed/mocked as a union of them for a arbitrary source set.
 *
 * @param name name which used for the mock.
 * @param interfaces variable amount of interfaces.
 * @property name name which will be propagated to the (KSP) processor.
 * @property interfaces which will be propagated to the (KSP) processor.
 * @author Matthias Geisler
 */
@KMockExperimental
public annotation class KMockMulti(
    val name: String,
    vararg val interfaces: KClass<*>,
)

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
public annotation class Relaxer

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This API is experimental. It may be removed or changed in future releases.",
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
)
public annotation class KMockExperimental

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
)
/**
 * Marks a thing which is intended only for internal usage.
 */
public annotation class KMockInternal
