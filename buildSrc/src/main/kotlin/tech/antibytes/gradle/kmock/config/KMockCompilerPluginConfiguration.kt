/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.config

import tech.antibytes.gradle.publishing.api.PackageConfiguration
import tech.antibytes.gradle.publishing.api.PomConfiguration

object KMockCompilerPluginConfiguration {
    const val group = "tech.antibytes.kmock"
    const val id = "kmock-compiler"
    const val pluginId = "$group.$id"

    val publishing = Publishing

    object Publishing : KMockPublishingConfiguration() {
        val packageConfiguration = PackageConfiguration(
            pom = PomConfiguration(
                name = id,
                description = "KMock's Compiler plugin is part of humble mocking library for Kotlin Multiplatform.",
                year = 2022,
                url = "https://$gitHubRepositoryPath"
            ),
            developers = listOf(developer),
            license = license,
            scm = sourceControl
        )
    }
}
