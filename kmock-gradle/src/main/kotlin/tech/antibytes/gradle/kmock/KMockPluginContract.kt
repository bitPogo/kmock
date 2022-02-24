/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

internal interface KMockPluginContract {
    interface Extension {
        var rootPackage: String
    }

    interface CleanUpTask {
        @get:Input
        val indicator: Property<String>

        @get:Input
        val targetPlatform: Property<String>

        @get:Input
        val target: Property<String>

        @TaskAction
        fun cleanUp()
    }

    interface SharedSourceCopist {
        fun copySharedSource(
            project: Project,
            source: String,
            target: String,
            indicator: String
        ): Copy
    }

    interface SourceSetConfigurator {
        fun configure(project: Project)
    }
}
