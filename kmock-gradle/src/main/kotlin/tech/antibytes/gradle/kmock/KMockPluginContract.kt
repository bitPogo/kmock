/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import org.gradle.api.provider.SetProperty

internal interface KMockPluginContract {
    interface Extension {
        val androidVariants: SetProperty<String>
        val androidFlavours: SetProperty<String>
    }
}
