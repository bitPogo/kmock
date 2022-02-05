/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.dependency

object GradlePlugin {
    const val dependency = "tech.antibytes.gradle-plugins:antibytes-dependency:${Version.gradle.antibytes}"
    const val publishing = "tech.antibytes.gradle-plugins:antibytes-publishing:${Version.gradle.antibytes}"
    const val coverage = "tech.antibytes.gradle-plugins:antibytes-coverage:${Version.gradle.antibytes}"
    const val projectConfig = "tech.antibytes.gradle-plugins:antibytes-configuration:${Version.gradle.antibytes}"
    const val spotless = "com.diffplug.spotless:spotless-plugin-gradle:${Version.gradle.spotless}"
    const val ksp = "com.google.devtools.ksp:symbol-processing-api:${Version.gradle.ksp}"
}
