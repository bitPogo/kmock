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
         * The invoking projects root package name.
         */
        var rootPackage: String

        /**
         * Mapping to resolve Mock name conflicts.
         * The key must be the full qualified name of target which should use a alias instead.
         * The value must be an alias name which replaces the short name of the target.
         */
        var aliasNameMapping: Map<String, String>

        /**
         * Selection of allowed recursive generic types (e.g. Comparable).
         * List of full qualified names of generics types which are used for recursive declaration.
         * This property can be used to allow them for spying.
         */
        var allowedRecursiveTypes: Set<String>

        /**
         * Selection of targets which allow Proxies on build-in method (e.g. toString).
         * Set of full qualified name of target which should proxy build-in methods.
         */
        var useBuildInProxiesOn: Set<String>

        /**
         * Selection of prefixes which are polluting names of Proxies while they referencing overloaded methods.
         * The order must be longest to shortest.
         */
        var uselessPrefixes: Set<String>
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
        const val BUILD_IN_PREFIX = "buildIn_"
        const val USELESS_PREFIXES_PREFIX = "namePrefix_"
        const val INDICATOR_SEPARATOR = "@"
    }
}
