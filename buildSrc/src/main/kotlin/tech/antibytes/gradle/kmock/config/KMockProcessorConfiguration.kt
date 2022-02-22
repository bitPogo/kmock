/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.config

import tech.antibytes.gradle.publishing.api.PackageConfiguration
import tech.antibytes.gradle.publishing.api.PomConfiguration

object KMockProcessorConfiguration {
    const val group = "tech.antibytes.kmock"

    val publishing = Publishing

    object Publishing : KMockPublishingConfiguration() {
        val packageConfiguration = PackageConfiguration(
            isJavaLibrary = true,
            pom = PomConfiguration(
                name = "kmock-processor",
                description = "Mocking Framework for Kotlin Multiplatform.",
                year = 2022,
                url = "https://$gitHubRepositoryPath"
            ),
            developers = listOf(developer),
            license = license,
            scm = sourceControl
        )
    }
}
