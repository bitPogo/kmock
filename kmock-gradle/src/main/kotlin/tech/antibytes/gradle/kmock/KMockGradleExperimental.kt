/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */
package tech.antibytes.gradle.kmock

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
public annotation class KMockGradleExperimental
