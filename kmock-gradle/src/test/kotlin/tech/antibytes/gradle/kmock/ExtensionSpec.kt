/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock

import com.google.devtools.ksp.gradle.KspExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.junit.jupiter.api.Test
import tech.antibytes.gradle.test.createExtension
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class ExtensionSpec {
    @Test
    fun `It fulfils Extension`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)
        extension fulfils KMockPluginContract.Extension::class
    }

    @Test
    fun `Its default rootPackage is an empty string`() {
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk()

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        val extension = createExtension<KMockExtension>(project)

        extension.rootPackage mustBe ""
    }

    @Test
    fun `Its propagates rootPackage changes to Ksp`() {
        // Given
        val project: Project = mockk(relaxed = true)
        val kspExtension: KspExtension = mockk(relaxed = true)
        val expected = "test"

        every { project.extensions.getByType(KspExtension::class.java) } returns kspExtension

        // When
        val extension = createExtension<KMockExtension>(project)
        extension.rootPackage = expected

        extension.rootPackage mustBe expected
        verify(exactly = 1) { kspExtension.arg("rootPackage", expected) }
    }
}
