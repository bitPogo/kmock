/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.config.publishing

import org.gradle.api.Project
import tech.antibytes.gradle.configuration.BasePublishingConfiguration

open class KMockPublishingConfiguration(
    project: Project,
) : BasePublishingConfiguration(project, "kmock") {
    val description = "KMock - a Mock Generator for Kotlin (Multiplatform)."
    val url = "https://$gitHubRepositoryPath"
    val year = 2022
}
