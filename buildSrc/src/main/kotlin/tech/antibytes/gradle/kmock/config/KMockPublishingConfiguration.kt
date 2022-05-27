/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.config

import tech.antibytes.gradle.publishing.api.DeveloperConfiguration
import tech.antibytes.gradle.publishing.api.GitRepositoryConfiguration
import tech.antibytes.gradle.publishing.api.LicenseConfiguration
import tech.antibytes.gradle.publishing.api.MavenRepositoryConfiguration
import tech.antibytes.gradle.publishing.api.SourceControlConfiguration
import tech.antibytes.gradle.publishing.api.VersioningConfiguration

open class KMockPublishingConfiguration {
    private val username = System.getenv("PACKAGE_REGISTRY_UPLOAD_USERNAME")?.toString() ?: ""
    private val passwordGitHubRepos = System.getenv("PACKAGE_REGISTRY_UPLOAD_TOKEN")?.toString() ?: ""
    private val passwordGitHubPackages = System.getenv("PACKAGE_REGISTRY_REGISTER_TOKEN")?.toString() ?: ""
    private val githubOwner = "bitPogo"
    private val githubRepository = "kmock"
    val description = "KMock - a Mock Generator for Kotlin (Multiplatform)."

    private val host = "github.com"
    private val path = "$githubOwner/$githubRepository"

    private val gitHubRepositoryPath = "$host/$path"
    private val gitHubOwnerPath = "$host/$githubOwner"
    val url = "https://$gitHubRepositoryPath"
    protected val year = 2022

    protected val license = LicenseConfiguration(
        name = "Apache License, Version 2.0",
        url = "https://www.apache.org/licenses/LICENSE-2.0.txt",
        distribution = "repo"
    )

    protected val developer = DeveloperConfiguration(
        id = githubOwner,
        name = githubOwner,
        url = "https://$host/$githubOwner",
        email = "solascriptura001+antibytes@gmail.com"
    )

    protected val sourceControl = SourceControlConfiguration(
        url = "git://$gitHubRepositoryPath.git",
        connection = "scm:git://$gitHubRepositoryPath.git",
        developerConnection = "scm:git://$gitHubRepositoryPath.git",
    )

    val repositories = setOf(
        MavenRepositoryConfiguration(
            name = "GitHubPackageRegistry",
            url = "https://maven.pkg.github.com/$path",
            username = username,
            password = passwordGitHubPackages
        ),
        GitRepositoryConfiguration(
            name = "Development",
            gitWorkDirectory = "dev",
            url = "https://$gitHubOwnerPath/maven-dev",
            username = username,
            password = passwordGitHubRepos
        ),
        GitRepositoryConfiguration(
            name = "Snapshot",
            gitWorkDirectory = "snapshots",
            url = "https://$gitHubOwnerPath/maven-snapshots",
            username = username,
            password = passwordGitHubRepos
        ),
        GitRepositoryConfiguration(
            name = "Release",
            gitWorkDirectory = "releases",
            url = "https://$gitHubOwnerPath/maven-releases",
            username = username,
            password = passwordGitHubRepos
        )
    )

    val versioning = VersioningConfiguration(
        useGitHashSnapshotSuffix = true,
        featurePrefixes = listOf("feature"),
    )

    companion object {
        private val configuration = KMockPublishingConfiguration()

        val repositories = configuration.repositories
        val versioning = configuration.versioning
    }
}
