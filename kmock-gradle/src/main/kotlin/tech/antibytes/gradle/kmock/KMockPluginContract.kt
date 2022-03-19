/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

internal interface KMockPluginContract {
    interface Extension {
        /**
         * The invoking projects root package name
         */
        var rootPackage: String

        /**
         * Mapping to resolve Mock name conflicts
         * The key must be the full qualified name of target which should use a alias instead
         * The value must be an alias name which replaces the short name of the target
         */
        var aliasNameMapping: Map<String, String>

        /**
         * List of qualified name of Generics which are used for recursive declaration.
         * E.g.: Comparable
         * This property can be used to allow them for spying
         */
        var allowedRecursiveTypes: List<String>
    }

    interface CleanUpTask {
        @get:Input
        val indicators: SetProperty<String>

        @get:Input
        val platform: Property<String>

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
            indicator: String,
        ): Copy
    }

    interface KmpSetupConfigurator {
        fun wireSharedSourceTasks(
            project: Project,
            kspMapping: Map<String, String>,
            dependencies: Map<String, Set<String>>
        )
    }

    interface SourceSetConfigurator {
        fun configure(project: Project)
    }

    companion object {
        const val PRECEDENCE_PREFIX = "precedence_"
        const val ALIAS_PREFIX = "alias_"
        const val RECURSIVE_PREFIX = "recursive_"
    }
}
