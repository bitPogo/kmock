/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.config.quality

import tech.antibytes.gradle.quality.api.LinterConfiguration
import tech.antibytes.gradle.quality.api.PartialLinterConfiguration

object Linter {
    val spotless = LinterConfiguration().copy(
        code = PartialLinterConfiguration(
            include = setOf("**/*.kt"),
            exclude = setOf(
                "buildSrc/build/",
                "**/buildSrc/build/",
                "**/src/test/resources/**/*.kt",
                "**/build/**/*.kt",
            ),
            rules = mapOf(
                "ij_kotlin_imports_layout" to "*",
                "ij_kotlin_allow_trailing_comma" to "true",
                "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
                "ktlint_standard_property-naming" to "disabled",
                "ktlint_standard_discouraged-comment-location" to "disabled",
                "ktlint_standard_value-argument-comment" to "disabled",
                "ktlint_standard_multiline-expression-wrapping" to "disabled",
                "ktlint_standard_function-naming" to "disabled",
            ),
        ),
    )
}
