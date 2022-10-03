/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

includeBuild("kmock-test-dependency")

include(
    ":kmock",
    ":kmock-processor",
    ":kmock-gradle",
)

buildCache {
    local {
        isEnabled = false
        directory = File(rootDir, "build-cache")
        removeUnusedEntriesAfterDays = 30
    }
}

rootProject.name = "kmock"
