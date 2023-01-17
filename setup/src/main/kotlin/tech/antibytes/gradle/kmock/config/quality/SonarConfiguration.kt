/*
 * Copyright (c) 2023 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.config.quality

import org.gradle.api.Project
import tech.antibytes.gradle.quality.api.QualityGateConfiguration

class SonarConfiguration(project: Project) {
    val configuration = QualityGateConfiguration(
        project = project,
        projectKey = "kmock",
        exclude = setOf(
            "playground",
            "integration-kmp",
            "integration-android-application",
            "docs",
        )
    )
}
