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
         * Selection of targets which allow Proxies on build-in method (e.g. toString).
         * Set of full qualified name of target which should proxy build-in methods.
         */
        var useBuildInProxiesOn: Set<String>

        /**
         * Selection of prefixes which are polluting names of Proxies while they referencing overloaded methods.
         * The order must be longest to shortest.
         */
        @Deprecated("This will be removed with version 1.0. You can opt-in/out the new behaviour with enableNewOverloadingNames.")
        var uselessPrefixes: Set<String>

        /**
         * Opt-in/out the new name resolver for overloaded methods.
         * It is opt-in on default.
         */
        @Deprecated("This will be removed with version 1.0.")
        var enableNewOverloadingNames: Boolean

        /**
         * Allows to prefix type names in order to resolve collisions for overloaded methods.
         * The key must be the full qualified name of target which should use an additional prefix.
         * The value is an arbitrary String, which is used as a prefix.
         */
        var useTypePrefixFor: Map<String, String>

        /**
         * Allows to replace auto resolved method names with a given name. This also affects the ProxyId.
         * The key must be the original full Id of the Proxy.
         * The value is an arbitrary String, which is used as method name.
         */
        var customMethodNames: Map<String, String>

        /**
         * Selection of targets which allow Proxies to use Spies.
         * Set of full qualified name of target which should allow proxy to spy on.
         * Note: This will also activate build-in methods.
         */
        var enableSpies: Set<String>

        /**
         * Switch which will KMock tell generate only `kspy` instead of `kmock`.
         * `kspy` will automatically reference all generated spies an additional invocation of `spyOn` is not needed.
         */
        var spiesOnly: Boolean

        /**
         * Enable the kmock factory function to reference Mocks by their interfaces.
         * Default is false
         */
        var allowInterfacesOnKmock: Boolean

        /**
         * Enable the kspy factory function to reference Mocks by their interfaces.
         * Default is false
         */
        var allowInterfacesOnKspy: Boolean

        /**
         * Sets a global default freeze value for kmock and kspy.
         * Default is true
         */
        var freezeOnDefault: Boolean
    }

    interface SourceSetConfigurator {
        fun configure(project: Project)
    }

    companion object {
        const val KMOCK_PREFIX = "kmock_"
        const val KSP_DIR = "${KMOCK_PREFIX}kspDir"
        const val KMP_FLAG = "${KMOCK_PREFIX}isKmp"
        const val FREEZE = "${KMOCK_PREFIX}freeze"
        const val INTERFACES_KMOCK = "${KMOCK_PREFIX}allowInterfacesOnKmock"
        const val INTERFACES_KSPY = "${KMOCK_PREFIX}allowInterfacesOnKspy"
        const val ROOT_PACKAGE = "${KMOCK_PREFIX}rootPackage"
        const val PRECEDENCE = "${KMOCK_PREFIX}precedence_"
        const val ALIASES = "${KMOCK_PREFIX}alias_"
        const val USE_BUILD_IN = "${KMOCK_PREFIX}buildIn_"
        const val SPY_ON = "${KMOCK_PREFIX}spyOn_"
        const val SPIES_ONLY = "${KMOCK_PREFIX}spiesOnly"
        const val OVERLOAD_NAME_FEATURE_FLAG = "${KMOCK_PREFIX}useNewOverloadedNames"
        const val USELESS_PREFIXES = "${KMOCK_PREFIX}oldNamePrefix_"
        const val TYPE_PREFIXES = "${KMOCK_PREFIX}namePrefix_"
        const val CUSTOM_METHOD_NAME = "${KMOCK_PREFIX}customMethodName_"
    }
}
