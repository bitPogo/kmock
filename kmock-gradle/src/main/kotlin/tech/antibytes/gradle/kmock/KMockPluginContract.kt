/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.squareup.kotlinpoet.ClassName
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

internal interface KMockPluginContract {
    interface Extension {
        var rootPackage: String
        val sharedSources: MapProperty<String, String>
    }

    interface CleanUpTask {
        @get:Input
        val indicators: SetProperty<String>

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
            platform: String,
            source: String,
            target: String,
            indicator: String
        ): Copy
    }

    interface SourceSetConfigurator {
        fun configure(project: Project)
    }

    interface FactoryGenerator {
        fun generate(
            targetDir: File,
            rootPackage: String
        )
    }

    companion object {
        const val TARGET = "COMMON SOURCE"
        val COLLECTOR_NAME = ClassName(
            "tech.antibytes.kmock",
            "KMockContract.Collector"
        )

        val KMOCK_CONTRACT = ClassName(
            "tech.antibytes.kmock",
            "KMockContract"
        )
    }
}
