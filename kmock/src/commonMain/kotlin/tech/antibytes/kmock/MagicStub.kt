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
annotation class MagicStub(vararg val interfaces: KClass<*>)
