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
         *
         * The key must be the full qualified name of target which should use a alias instead.
         * The value must be an alias name which replaces the short name of the target.
         */
        var aliasNameMapping: Map<String, String>

        /**
         * Selection of targets which allow Proxies on build-in method (e.g. toString).
         *
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
         *
         * It is opt-in on default.
         */
        @Deprecated("This will be removed with version 1.0.")
        var enableNewOverloadingNames: Boolean

        /**
         * Allows to prefix type names in order to resolve collisions for overloaded methods.
         * Note: enableNewOverloadingNames must be set to true to enable this behaviour.
         *
         * The key must be the full qualified name of target which should use an additional prefix.
         * The value is an arbitrary String, which is used as a prefix.
         */
        var useTypePrefixFor: Map<String, String>

        /**
         * Allows to replace auto resolved method names with a given name. This also affects the ProxyId.
         * Note: enableNewOverloadingNames must be set to true to enable this behaviour.
         *
         * The key must be the original full Id of the Proxy.
         * The value is an arbitrary String, which is used as method name.
         */
        var customMethodNames: Map<String, String>

        /**
         * Selection of targets which allow Proxies to use Spies.
         *
         * Set of full qualified name of target which should allow proxy to spy on.
         * Note: This will also activate build-in methods.
         */
        var spyOn: Set<String>

        /**
         * Switch which will KMock tell generate only `kspy` instead of `kmock`.
         * `kspy` will automatically reference all generated spies an additional invocation of `spyOn` is not needed.
         *
         * Default is false
         */
        var spiesOnly: Boolean

        /**
         * Enables spies for all given Interfaces.
         *
         * Default is false
         */
        var spyAll: Boolean

        /**
         * Enable factory functions to reference Mocks by their interfaces.
         *
         * Default is false
         */
        var allowInterfaces: Boolean

        /**
         * Sets a global default freeze value for kmock and kspy.
         *
         * Default is true
         */
        var freezeOnDefault: Boolean

        /**
         * Disables the generation of kmock and kspy.
         *
         * Default is false.
         */
        var disableFactories: Boolean

        /**
         * Allows to use custom annotation for meta sources with the exception of commonTest.
         *
         * The key must be the original full qualified name of the Annotation.
         * The value is the source set it is referring to (nativeTest or native).
         */
        var customAnnotationsForMeta: Map<String, String>

        /**
         * Feature Flag to de-/activate the alternative proxy access.
         *
         * Default is false
         */
        var allowExperimentalProxyAccess: Boolean

        /**
         * Feature Flag to enable/disable fine grained proxy names.
         *
         * Default is false
         */
        @KMockGradleExperimental
        var enableFineGrainedNames: Boolean
    }

    interface SourceSetConfigurator {
        fun configure(project: Project)
    }

    interface DependencyGraph {
        fun resolveAncestors(
            sourceDependencies: Map<String, Set<String>>,
            metaDependencies: Map<String, Set<String>>,
        ): Map<String, Set<String>>
    }

    companion object {
        const val KMOCK_PREFIX = "kmock_"
        const val KSP_DIR = "${KMOCK_PREFIX}kspDir"
        const val KMP_FLAG = "${KMOCK_PREFIX}isKmp"
        const val DISABLE_FACTORIES = "${KMOCK_PREFIX}disable_factories"
        const val FREEZE = "${KMOCK_PREFIX}freeze"
        const val INTERFACES = "${KMOCK_PREFIX}allowInterfaces"
        const val ROOT_PACKAGE = "${KMOCK_PREFIX}rootPackage"
        const val DEPENDENCIES = "${KMOCK_PREFIX}dependencies_"
        const val ALIASES = "${KMOCK_PREFIX}alias_"
        const val USE_BUILD_IN = "${KMOCK_PREFIX}buildIn_"
        const val SPY_ON = "${KMOCK_PREFIX}spyOn_"
        const val SPY_ALL = "${KMOCK_PREFIX}spyAll"
        const val SPIES_ONLY = "${KMOCK_PREFIX}spiesOnly"
        const val OVERLOAD_NAME_FEATURE_FLAG = "${KMOCK_PREFIX}useNewOverloadedNames"
        const val USELESS_PREFIXES = "${KMOCK_PREFIX}oldNamePrefix_"
        const val TYPE_PREFIXES = "${KMOCK_PREFIX}namePrefix_"
        const val CUSTOM_METHOD_NAME = "${KMOCK_PREFIX}customMethodName_"
        const val CUSTOM_ANNOTATION = "${KMOCK_PREFIX}customAnnotation_"
        const val ALTERNATIVE_PROXY_ACCESS = "${KMOCK_PREFIX}alternativeProxyAccess"
        const val FINE_GRAINED_PROXY_NAMES = "${KMOCK_PREFIX}enableFineGrainedProxyNames"
    }
}
