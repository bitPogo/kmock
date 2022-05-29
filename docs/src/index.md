# KMock
A humble mocking library for Kotlin, KotlinJS and Kotlin Multiplatform using Kotlin Symbol Processing (KSP).

[![Latest release](https://github.com/bitPogo/kmock/tree/main/docs/src/assets/images/badge-release-latest.svg)][releases]
[![License](https://github.com/bitPogo/kmock/tree/main/docs/src/assets/images/badge-license.svg)](LICENSE)
[![Platforms](https://github.com/bitPogo/kmock/tree/main/docs/src/assets/images/badge-platform-support.svg)](LICENSE)
[![CI - Build Snapshot Version](https://github.com/bitPogo/kmock/actions/workflows/ci-build-snapshot-version.yml/badge.svg)](https://github.com/bitfunk/gradle-plugins/actions/workflows/ci-build-snapshot-version.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=kmock&metric=coverage)](https://sonarcloud.io/summary/new_code?id=kmock)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=kmock&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=kmock)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=kmock&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=kmock)

## About The Project
Writing mocks is certainly no witchcraft, but doing them by hand day for day can be dull.
More importantly it takes time which can be better invested directly into writing tests.
However, while JVM projects get indeed supreme support by either [MockK](https://mockk.io/) or [Mockito](https://github.com/mockito/mockito-kotlin), Kotlin Multiplatform still has nothing comparable.
KMock aims to fill that gap and will hopefully advance to there over time.
Similar to other projects it uses [KSP](https://github.com/google/ksp), but it is capable of associating generated Mocks correctly to their belonging shared source sets (like native, ios, etc) without additional setup and with minimal boilerplate done by consumers.
KMock works currently *only* based on *interfaces*.
It supports to some extent features like spying and relaxation of Mocks to make them non intrusive.
So if the project caught your eye check out the [Playground](https://github.com/bitPogo/kmock-playground) or dive into the [Documentation](https://bitpogo.github.io/kmock/).

## Dependencies

KMock has the following dependencies:

* [AndroidGradlePlugin (AGP) 7.1.3](https://developer.android.com/studio/releases/gradle-plugin)
* [Kotlin 1.6.10](https://kotlinlang.org/docs/releases.html)
* [AtomicFu 0.17.1](https://github.com/Kotlin/kotlinx.atomicfu)
* [Touchlab's Stately 1.2.1](https://github.com/touchlab/Stately)
* [Square KotlinPoet 1.11.0](https://square.github.io/kotlinpoet/)
* [Kotlin Symbol Processing (KSP) 1.6.10-1.0.4](https://github.com/google/ksp)
* [Gradle 7.4.2](https://gradle.org/)
  
## Additional Requirements

* Android 6.0 (API 21) to Android 12 (API 31)
* [Java 11](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot)

## Built For

* [Kotlin Multiplatform](https://kotlinlang.org/docs/reference/mpp-intro.html)

## Changelog

See [changelog](changelog.md)

## Versioning

This project uses [Semantic Versioning](http://semver.org/) as a guideline for our versioning.

## Contributing

You want to help or share a proposal? You have a specific problem? Read the following:

* [Code of conduct](development/code-of-conduct.md) for details on our code of conduct.
* [ contributing guide](development/releasing.md) for details about how to report bugs and propose features.

## Releasing

Please take a look [here](development/releasing.md)

## Copyright and License

Copyright (c) 2022 Matthias Geisler / All rights reserved.

Please refer to the [License](license.md) for further details.