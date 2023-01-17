/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.util.test.config.publishing

import org.gradle.api.Project
import tech.antibytes.gradle.publishing.api.PackageConfiguration
import tech.antibytes.gradle.publishing.api.PomConfiguration

class KMockGradleConfiguration(project: Project) : PublishingBase() {
    val id = "kmock-gradle"
    val pluginId = "$group.$id"
    val longDescription = """KMock is generator for Mocks and Spies aiming Kotlin (Multiplatform).
        |It can work non intrusive, if relaxed or while acting as Spy and supports as well stubbing.
        |This plugin apply some necessary configuration and takes care of the communication with the processor.
    """.trimMargin()
    val version = "0.3.0-rc04"

    val publishing = Publishing(project)

    class Publishing(project: Project) : KMockPublishingConfiguration(project) {
        val packageConfiguration = PackageConfiguration(
            pom = PomConfiguration(
                name = "KMock Gradle Plugin",
                description = description,
                year = year,
                url = url,
            ),
            developers = listOf(developer),
            license = license,
            scm = sourceControl,
        )
    }
}
