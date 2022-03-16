/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.compiler

import org.jetbrains.kotlin.extensions.DeclarationAttributeAltererExtension
import org.junit.jupiter.api.Test
import tech.antibytes.util.test.fulfils

class KMockOpenClassesForTestExtensionSpec {
    @Test
    fun `It fulfils DeclarationAttributeAltererExtension`() {
        KMockOpenClassesForTestExtension() fulfils DeclarationAttributeAltererExtension::class
    }
}
