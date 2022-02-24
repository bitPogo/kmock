/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

abstract class KMockExtension : KMockPluginContract.Extension {
    override var rootPackage: String = ""
}
