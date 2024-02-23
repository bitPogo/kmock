/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */
import systems.danger.kotlin.danger
import systems.danger.kotlin.fail
import systems.danger.kotlin.onGitHub
import systems.danger.kotlin.warn

danger(args) {
    val allSourceFiles = git.modifiedFiles + git.createdFiles
    val isChangelogUpdated = allSourceFiles.contains("CHANGELOG.adoc")

    onGitHub {
        val branchName = pullRequest.head.label.substringAfter(":")
        val isFeatureBranch =
            "(?:(?::feature\\/)?(?:(?:add|update|change|remove|fix|bump|security)-)[a-zA-Z][a-zA-Z0-9-.]*)"
                .toRegex()
                .matches(branchName)
        val isReleaseBranch =
            "(?:release\\/(?:\\d{1,3}\\.\\d{1,3}(?:\\.\\d{1,3})?)(?:\\/prepare-\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})?)"
                .toRegex()
                .matches(branchName)
        val isDependabotBranch =
            "dependabot/(.*)"
                .toRegex()
                .matches(branchName)
        val isFeatureTitle =
            "(?:(?:\\[[A-Z]{2,8}-\\d{1,6}\\]\\s)?(?:Add|Update|Change|Remove|Fix|Bump|Security)\\s.*)"
                .toRegex()
                .matches(pullRequest.title)
        val isReleaseTitle = "(?:(?:Prepare )?Release \\d{1,3}\\.\\d{1,3}\\.\\d{1,3})"
            .toRegex()
            .matches(pullRequest.title)


        if (!isFeatureBranch && !isReleaseBranch && !isDependabotBranch) {
            fail(
                "Branch name is not following our pattern:\n" +
                    "\nrelease/1.2(.3)(/prepare-1.2.3)\n" +
                    "\nfeature/add|change|remove|fix|bump|security-feature-title\n" +
                    "\n\n" +
                    "\n Current name: $branchName"
            )
        }

        if (isFeatureBranch) {
            if (!isFeatureTitle) {
                fail(
                    "Title is not following our pattern:\n" +
                        "\nAdd|Change|Remove|Fix|Bump|Security {Feature title}"
                )
            }
        }

        if (isReleaseBranch) {
            if (!isReleaseTitle) {
                fail(
                    "Title is not following our pattern: Prepare Release major.minor.patch (1.2.0)"
                )
            }
        }

        // General
        if (pullRequest.assignee == null) {
            warn("Please assign someone to merge this PR")
        }

        if (pullRequest.milestone == null) {
            warn("Set a milestone please")
        }

        when {
            pullRequest.body == null -> warn("Please include a description of your PR changes")
            else -> {/* do nothing*/}
        }

        // Changelog
        if (!isChangelogUpdated) {
            warn("Functional changes should be reflected in the CHANGELOG.adoc")
        }

        // Size
        val changes = (pullRequest.additions ?: 0) - (pullRequest.deletions ?: 0)
        when {
            changes > 2000 -> fail("This Pull-Request is way to big, please slice it into smaller pull-requests.")
            changes > 1000 -> warn("Too Big Pull-Request, keep changes smaller")
            changes > 500 -> warn("Large Pull-Request, try to keep changes smaller if you can")
            else -> {/* do nothing */}
        }
    }
}
