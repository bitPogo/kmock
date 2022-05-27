# Releasing

Releases are automatically created from added tags using GitHub Actions.

A tag needs to be in the form of `v{major}.{minor}.{patch}`.

## Release preparation

1. Create a release branch of from `main` branch with this pattern:

- `release/{major}.{minor}/prepare-{major}.{minor}.{patch}`

2. Update [changelog.md](../changelog.md) by creating a new Unreleased section and change current unreleased to release version
3. Update the latest release [badge](badges.md)

## Release

For a release, this project uses GitHub releases:

1. Create a [new release](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository)
2. Set the tag
3. Set the title
4. Add a description in form of a changelog
5. Publish when ready
