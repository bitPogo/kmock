# Changelog

All important changes of this project must be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased](https://github.com/bitPogo/kmock/compare/v0.3.0-rc02...main)

### Added

### Changed

* pseudo build-in methods are now resolved independently

### Deprecated

### Removed

### Fixed

* Aliases as type arguments are not correctly resolved, if the Aliases have type arguments on their own
* Generics cause AccessMethods to collide in their definition
* Stackoverflow when determine TypeVariance
* Transitive Aliases are not correctly resolved for AccessMethods if they nested ParameterTypes

### Security

### Bumped

* Gradle 7.4.2 -> 7.5
* Kotlin 1.6.21 -> 1.7.0
* KotlinPoet 1.11.0 -> 1.12.0
* AtomicFu 0.17.3 -> 0.18.2
* Stately 1.2.1 -> 1.2.3
* Android Target SDK 31 -> 32
* CompilerTest 1.4.8 -> 1.4.9

## [0.3.0-rc02](https://github.com/bitPogo/kmock/compare/v0.3.0-rc01...v0.3.0-rc02)

### Changed

* sources for `release` and `debug` for non KMP are now added via the Android extension while custom build-variant need manual setup

### Fixed

* Build Cache enabled, Mocks are not created
* Relaxer not picked correctly up on multiple test runs
* Multi Interface Cleanup causes compilation to fail

### Bumped

* Android Target SDK 31 -> 32

## [0.3.0-rc01](https://github.com/bitPogo/kmock/compare/v0.2.2...v0.3.0-rc01)

### Added

* `preventResolvingOfAliases` as workaround for AccessMethods with expect/actual Aliases which cause incremental builds to fail

### Bumped

* Kotlin 1.6.10 -> 1.6.21
* AtomicFu 0.17.1 -> 0.17.3
* KSP 1.6.10-1.0.4 -> 1.6.21-1.0.6

## [0.2.2](https://github.com/bitPogo/kmock/compare/v0.2.1...v0.2.2)

### Fixed

* `run`/`runs` does not trigger SideEffect invocation

## [0.2.1](https://github.com/bitPogo/kmock/compare/v0.2.0...v0.2.1)

### Fixed

* Plugin triggered the usage of the legacy JS compiler for KotlinJs


## [0.2.0](https://github.com/bitPogo/kmock/compare/v0.2.0-rc01...v0.2.0)

### Added

* `and` ArgumentConstraint
* `getValue` which preserves the old behaviour of get (PropertyProxy)
* `getValues` to replace getMany (PropertyProxy)
* `error` to replace throws (FunProxy)
* `errors` to replace throwsMany (FunProxy)
* `returns` which acts as setter for `getValue`/`returnValue` for Proxies
* `returnsMany` which acts as setter for `getValues`/`returnValues` for Proxies
* `throws` (infix method) which acts as setter for `error` for FunProxies
* `throwsMany` (infix method) which acts as setter for `errors` for FunProxies
* `runOnGet` which acts as setter for `getSideEffect` for PropertyProxies
* `runOnSet` which acts as setter for `setSideEffect` for PropertyProxies
* `hasNoFurtherInvocations` to ease `assertProxy`

### Changed

* The plugin can now applied directly while configuration must be done after the `kotlin` setup
* AccessMethods need always a hint if the Template has type parameters
* ProxyNameResolver is capable of utilizing the actual types of generic parameters when overloaded
* `run`/`runs` are now infix methods
* Proxies have now an operator for get which means they can be accessed like Arrays
* _*BREAKING*_ `get` is now responsible for the SideEffect of PropertyProxies

### Deprecated

* `getMany` (PropertyProxy)
* `throws` (FunProxy)
* `throwsMany` (FunProxy)

### Removed

* _*BREAKING*_ `getSideEffect` (PropertyProxy)

### Fixed

* `vararg` eats specialised Array Types (e.g. IntArray) and covariant types when inherited
* Factories for Multi-Interface-Mocks
* Multi-Boundary Parameter are not right resolved when mixed multi with regular parameter in nested types
* Collisions of type aliases with regular method signatures

## [0.2.0-rc01](https://github.com/bitPogo/kmock/compare/v0.1.1...v0.2.0-rc01)

### Added

* `or` ArgumentConstraint
* `not` ArgumentConstraint
* `spiesOnly` in the Gradle extension, in order to tell the processor to only create `kspy`. Also `spyOn` is not needed in this configuration
* `spyAll` in the Gradle extension, in order to tell the processor to create for al given interfaces also a spy entryPoint
* `vararg` is now supported by Mocks
* `freezeOnDefault` in the Gradle extension, which sets a default freeze value for `kspy` and `kmock`
* `useTypePrefixFor` for overloaded names in order to ease them and avoid collisions
* `customMethodNames` in order to allow complete custom names for methods
* `allowInterfaces`, which combines `allowInterfacesOnKmock` and `allowInterfacesOnKspy`
* `disableFactories` in order to disable the generation of `kmock` and `kspy` if needed
* `customAnnotationsForMeta` to provide a hook for the usage of customized annotation for meta/shared sources
* `assertOrder` in order to make the names more consistent and preserves the old behaviour of `verifyStrictOrder`
* `assertProxy` as alternative to `verify`
* iosSimulatorArm64 support
* Support for instrumented Android tests (aka androidAndroidTest) on KMP
* Multi-Interface Mocks support
* Interface receiver members are full supported
* `run` for FunProxies to mitigate the strict assignment policy in terms of a single SideEffect
* `runs` for FunProxies to mitigate the strict assignment policy in terms of SideEffects
* `throwsMany` for FunProxies to align the the FunProxy API
* `enableFineGrainedNames` in order to allow fine grained typing (experimental/not this will feature helps you only in a non JVM context)
* `KMock` experimental annotation, which is agnostic in terms of source sets
* `KMockMulti` experimental annotation, which is agnostic in terms of source sets

### Changed

* Generated mocks don't contain runtime logic any longer
* Mutable properties of Proxies now separate froze/unfrozen state is cleaner to improve Runtime
* `kmock` and `kspy` are using now a shared function to improve compile time
* Non intrusive behaviour (spy & relaxation) is now resolved by proxy invocation rather then by proxy initialisation in order to cover edge cases
* Assertion-/VerificationChain is not coupled any longer directly to proxies and provide improved error messages
* Expectation Methods do not bleed into the global context any longer
* `verifyStrictOrder` is now used for total order of certain Proxies but allows partial order between different Proxies
* Android MinSDK 23 -> 21
* `spyOn` is now capable of picking up KMock defined Aliases
* `kspy` in terms of generics not longer exposed if not declared via `spyOn` or `spiesOnly`
* Relaxation method gets now the return type boundaries delegated, if generic in order to resolve type conflicts
* Generic methods names get prefixed by the generic type name, if it is overloaded to avoid name collisions
* Generic methods names get prefixed by an indicator, if it is overloaded and nullable to avoid name collisions
* Meta/Shared Source Annotation are now supporting platform references (e.g. instead of metaTest you can write meta)
* Custom Source Annotation are now supporting platform references (e.g. instead of sharedTest you can write shared)
* Proxy-Access-Methods and their corresponding verification/assertion counter parts
* `verifer` argument is now called `collector` in `kmock` and `kspy`

### Removed

* `allowedRecursiveTypes`, since it is no longer needed due to the new spy invocation
* `allowInterfacesOnKmock`, use allowInterfaces instead
* `allowInterfacesOnKspy`, use allowInterfaces instead
* Old experimental ProxyAssertion family, use `assertProxy` or `verify` instead
* `uselessPrefixes` in the Gradle Extension

### Fixed

* FunProxy names with nullable or multi-bounded types defined as generic parameter
* Annotation was not picked up when more then one Annotation was used
* Factories for Common were not created when no Template was specified
* Parallel declared shared source were eaten up by each other
* Nested Generic Types were not resolved for Proxies
* Multi-Bounded Generics caused invalid Proxy name if overloaded and a leading boundary was nullable

### Bumped

* KotlinPoet 1.10.2 -> 1.11.0
* Gradle 7.4.1 -> 7.4.2
* Android Gradle Plugin 7.1.2 -> 7.2.1

## [0.1.1](https://github.com/bitPogo/kmock/compare/v0.1.0...v0.1.1)

### Fixed

* Warnings for unused expression and unused parameter in MockFactory

### Bumped

* Gradle 7.2 -> 7.4.1

## [0.1.0](https://github.com/bitPogo/kmock/compare/releases/tag/v0.1.0)

Initial release.
