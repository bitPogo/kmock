/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.Project

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

        /**
         * Selection of targets which allow Proxies to use Spies.
         * Set of full qualified name of target which should allow proxy to spy on.
         * Note: This will also activate build-in methods.
         */
        var spyOn: Set<String>
    }

    interface SourceSetConfigurator {
        fun configure(project: Project)
    }

    companion object {
        const val KMOCK_PREFIX = "kmock_"
        const val KSP_DIR = "${KMOCK_PREFIX}kspDir"
        const val KMP_FLAG = "${KMOCK_PREFIX}isKmp"
        const val ROOT_PACKAGE = "${KMOCK_PREFIX}rootPackage"
        const val PRECEDENCE_PREFIX = "${KMOCK_PREFIX}precedence_"
        const val ALIAS_PREFIX = "${KMOCK_PREFIX}alias_"
        const val RECURSIVE_PREFIX = "${KMOCK_PREFIX}recursive_"
        const val BUILD_IN_PREFIX = "${KMOCK_PREFIX}buildIn_"
        const val SPY_ON = "${KMOCK_PREFIX}spyOn_"
        const val USELESS_PREFIXES_PREFIX = "${KMOCK_PREFIX}namePrefix_"
    }
}
