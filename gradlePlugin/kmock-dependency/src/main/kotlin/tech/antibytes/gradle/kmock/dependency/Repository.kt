/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */
package tech.antibytes.gradle.kmock.dependency

import org.gradle.api.artifacts.dsl.RepositoryHandler

data class Credentials(
    val username: String,
    val password: String
)

data class CustomRepository(
    val url: String,
    val groupIds: List<String>,
    val credentials: Credentials? = null
)

val githubGroups = listOf(
    "tech.antibytes.gradle-plugins",
    "tech.antibytes.test-utils-kmp"
)

val repositories = listOf(
    CustomRepository(
        "https://raw.github.com/bitPogo/maven-dev/main/dev",
        githubGroups
    ),
    CustomRepository(
        "https://raw.github.com/bitPogo/maven-snapshots/main/snapshots",
        githubGroups
    ),
    CustomRepository(
        "https://raw.github.com/bitPogo/maven-releases/main/releases",
        githubGroups
    )
)

fun RepositoryHandler.addCustomRepositories() {
    repositories.forEach { (url, groups, credentials) ->
        maven {
            setUrl(url)
            if (credentials is Credentials) {
                credentials {
                    username = credentials.username
                    password = credentials.password
                }
            }
            content {
                groups.forEach { group -> includeGroup(group) }
            }
        }
    }
}
