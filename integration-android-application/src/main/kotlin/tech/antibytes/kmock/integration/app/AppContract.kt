/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration.app

import kotlinx.coroutines.flow.StateFlow

interface AppContract {
    interface SampleViewModel {
        val flow: StateFlow<String>

        fun doSomething()
    }
}
