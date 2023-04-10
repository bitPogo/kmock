/*
 * Copyright (c) 2023 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.config.publishing

import org.gradle.api.Project
import tech.antibytes.gradle.publishing.PublishingApiContract.Type
import tech.antibytes.gradle.publishing.api.PackageConfiguration
import tech.antibytes.gradle.publishing.api.PomConfiguration

class KMockProcessorConfiguration(project: Project) : PublishingGroup() {
    val publishing = Publishing(project)

    class Publishing(project: Project) : KMockPublishingConfiguration(project) {
        val packageConfiguration = PackageConfiguration(
            type = Type.PURE_JAVA,
            pom = PomConfiguration(
                name = "KMock Processor",
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
