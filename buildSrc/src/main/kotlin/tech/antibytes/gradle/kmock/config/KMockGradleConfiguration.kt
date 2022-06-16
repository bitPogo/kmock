/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.config

import tech.antibytes.gradle.publishing.api.PackageConfiguration
import tech.antibytes.gradle.publishing.api.PomConfiguration

object KMockGradleConfiguration {
    const val group = "tech.antibytes.kmock"
    const val id = "kmock-gradle"
    const val pluginId = "$group.$id"
    val longDescription = """KMock is generator for Mocks and Spies aiming Kotlin (Multiplatform).
        |It can work non intrusive, if relaxed or while acting as Spy and supports as well stubbing.
        |This plugin apply some necessary configuration and takes care of the communication with the processor.
    """.trimMargin()
    const val version = "0.2.0-rc01"

    val publishing = Publishing

    object Publishing : KMockPublishingConfiguration() {
        val packageConfiguration = PackageConfiguration(
            pom = PomConfiguration(
                name = "KMock Gradle Plugin",
                description = description,
                year = year,
                url = url
            ),
            developers = listOf(developer),
            license = license,
            scm = sourceControl
        )
    }
}
