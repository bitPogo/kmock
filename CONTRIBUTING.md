# Contributing

When contributing to this project, this document should help you get started.

## Code Of Conduct

This project adheres to the Contributor Covenant [Code Of Conduct](https://bitpogo.github.io/kmock/development/code-of-conduct/).
By participating, you are expected to uphold this code.

## Issues

GitHub issues are the way to track bugs and enhancements.

Issues are hold in high regards in this project, so please feel free to open [issue](https://github.com/bitPogo/kmock/issues) for:

* _**Questions**_ to help to improve the user experience
* _**Ideas**_ which are a great source for contributions
* _**Problems**_ show where this project is lacking or not working as expected.

If you are reporting a problem, please provide as much information as possible, since this will help us to fix it.
This includes, if possible, a description or small sample project how to reproduce the problem.

Also please check out first if an issue had been already opened with your request.

## Contribute Code

### Development Process

#### Prerequisites

* [Android Studio 2022.1.1 or later](https://developer.android.com/studio#downloads)
* [Java 11](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot)
* [Kotlin 1.7.20](https://kotlinlang.org/)
* [Gradle 7.5.1](https://gradle.org/install/)

#### Features

Every change has to branch of from `main` and use this branch naming convention:

* `feature/{type_of_change}-{short_description}`

`main` must be always in releasable state.

##### Type Of Change

- *add* for new features or functionality
- *change* for changes in existing features or functionality
- *deprecated* for features which are at their end of life and will be removed in the future
- *remove* for removed features or functionality
- *fix* for any bug fixes
- *bump* for dependency updates
- *security* in case of vulnerabilities

Examples:

- `feature/add-awesome-hashing-algorithm`
- `feature/remove-not-so-awesome-algorithm`
- `feature/fix-algorithm-corner-case`
- `feature/bump-lib-to-1.3.0`

### Pull Request

[Pull requests](https://github.com/bitPogo/kmock/pulls) are always welcome!

If you (going to) contribute, please make sure you made clear which problem you are attempt solve or what is nature of your improvement.

#### Create Pull Request

Please use our title pattern: `{type of change} {short description}`:

`type of change` can be:

- *Add* for new features or functionality,
- *Change* for changes in existing features or functionality,
- *Deprecated* for features which are at their end of life and will be removed in the future,
- *Remove* for removed features or functionality,
- *Fix* for any bug fixes,
- *Security* in case of vulnerabilities,
- *Bump* for dependency updates,

followed by a `short description` of your change.

Example:

- Add awesome hashing algorithm
- Changed thumbnail generation

Pull requests must fill the provided template. Put N/A when a paragraph cannot be filled.

_Labels_ should be used (enhancement,bugfix, help wanted etc...) to categorise your contribution.

*Important*: Work in progress pull-requests should be created as a draft.

#### Code Review

Your contribution has to meet these criteria:

* [x] Functional and fitting in the project
* [x] Code style and naming conventions followed
* [x] Test written and passing
* [x] Existing Tests still passing
* [x] Continuous Integration build passing
* [x] Cross platform testing done for all supported platforms
* [x] Documentation updated (if necessary)
* [x] Changelog updated (if necessary)

### Dependencies using other licenses

Contributing code and introducing dependencies into the repository from other projects that use one
of the following licenses is allowed.

- [MIT](https://opensource.org/licenses/MIT)
- [ISC](https://opensource.org/licenses/ISC)
- [Apache 2.0](https://opensource.org/licenses/Apache-2.0)

Any other contribution needs to be signed off by the project owners.
