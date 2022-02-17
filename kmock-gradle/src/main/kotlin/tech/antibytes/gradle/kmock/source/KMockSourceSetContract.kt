/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

internal interface KMockSourceSetContract {
    interface CleanUpTask {
        @get:Input
        val source: Property<String>

        @TaskAction
        fun cleanUp()
    }
}
