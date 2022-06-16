/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.processor.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.processor.ProcessorContract
import tech.antibytes.kmock.processor.ProcessorContract.TemplateSource
import tech.antibytes.util.test.fulfils
import tech.antibytes.util.test.mustBe

class SourceFilterSpec {
    private val fixture = kotlinFixture()

    @Test
    fun `It fulfils SourceFilterSpec`() {
        SourceFilter(emptyMap(), mockk()) fulfils ProcessorContract.SourceFilter::class
    }

    @Test
    fun `Given filter is called with 2 Lists of Sources it filters the first by the 2nd according to the qualified name`() {
        // Given
        val source0_0: KSClassDeclaration = mockk()
        val source1_1: KSClassDeclaration = mockk()

        val packageName0: String = fixture.fixture()
        val interfaceName0: String = fixture.fixture()

        val source1_0: KSClassDeclaration = mockk()
        val source0_1: KSClassDeclaration = mockk()

        val packageName1: String = fixture.fixture()
        val interfaceName1: String = fixture.fixture()

        val sources0 = listOf(
            TemplateSource(
                indicator = "",
                template = source0_0,
                templateName = packageName0,
                packageName = interfaceName0,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "",
                template = source0_1,
                templateName = packageName0,
                packageName = interfaceName1,
                generics = null,
                dependencies = emptyList()
            )
        )

        val sources1 = listOf(
            TemplateSource(
                indicator = "",
                template = source1_0,
                templateName = packageName1,
                packageName = interfaceName0,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "",
                template = source1_1,
                templateName = packageName0,
                packageName = interfaceName1,
                generics = null,
                dependencies = emptyList()
            )
        )

        // When
        val actual = SourceFilter(emptyMap(), mockk()).filter(sources0, sources1)

        // Then
        actual mustBe listOf(
            sources0.first()
        )
    }

    @Test
    fun `Given filterByDependencies is called with aggregated SharedSource it emits an error if the SharedSource was not declared and they are not unique`() {
        // Given
        val logger: KSPLogger = mockk(relaxUnitFun = true)
        val source0: KSClassDeclaration = mockk()
        val source1: KSClassDeclaration = mockk()

        val packageName: String = fixture.fixture()
        val interfaceName: String = fixture.fixture()

        val marker0: String = fixture.fixture()
        val marker1: String = fixture.fixture()

        val sources = listOf(
            TemplateSource(
                indicator = marker0,
                template = source0,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = marker1,
                template = source1,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            )
        )

        // When
        SourceFilter(mapOf("any" to sortedSetOf()), logger).filterByDependencies(sources)

        // Then
        verify(exactly = 1) { logger.error("No SharedSource defined for $marker0.") }
        verify(exactly = 1) { logger.error("No SharedSource defined for $marker1.") }
    }

    @Test
    fun `Given filterByDependencies is called with aggregated SharedSource it filters not independent sources`() {
        // Given
        val source0: KSClassDeclaration = mockk()
        val source1: KSClassDeclaration = mockk()

        val packageName: String = fixture.fixture()
        val interfaceName: String = fixture.fixture()

        val sources = listOf(
            TemplateSource(
                indicator = "iosTest",
                template = source0,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "otherTest",
                template = source1,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
        )

        val dependencies = mapOf(
            "commonTest" to sortedSetOf(),
            "metaTest" to sortedSetOf("commonTest"),
            "concurrentTest" to sortedSetOf("commonTest", "metaTest"),
            "nativeTest" to sortedSetOf("commonTest", "concurrentTest", "metaTest"),
            "otherTest" to sortedSetOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
            "iosTest" to sortedSetOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
        )

        // When
        val actual = SourceFilter(dependencies, mockk()).filterByDependencies(sources)

        // Then
        actual mustBe listOf(sources[0], sources[1])
    }

    @Test
    fun `Given filterByDependencies is called with aggregated SharedSource it filters up to a shared parent`() {
        // Given
        val source0: KSClassDeclaration = mockk()
        val source1: KSClassDeclaration = mockk()
        val source2: KSClassDeclaration = mockk()

        val packageName: String = fixture.fixture()
        val interfaceName: String = fixture.fixture()

        val sources = listOf(
            TemplateSource(
                indicator = "iosTest",
                template = source0,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "otherTest",
                template = source2,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "concurrentTest",
                template = source1,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
        )

        val dependencies = mapOf(
            "commonTest" to sortedSetOf(),
            "metaTest" to sortedSetOf("commonTest"),
            "concurrentTest" to sortedSetOf("commonTest", "metaTest"),
            "nativeTest" to sortedSetOf("commonTest", "concurrentTest", "metaTest"),
            "otherTest" to sortedSetOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
            "iosTest" to sortedSetOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
        )

        // When
        val actual = SourceFilter(dependencies, mockk()).filterByDependencies(sources)

        // Then
        actual mustBe listOf(sources[2])
    }

    @Test
    fun `Given filterByDependencies is called with aggregated SharedSource it ignores sources which are children of a already added one`() {
        // Given
        val source0: KSClassDeclaration = mockk()
        val source1: KSClassDeclaration = mockk()
        val source2: KSClassDeclaration = mockk()

        val packageName: String = fixture.fixture()
        val interfaceName: String = fixture.fixture()

        val sources = listOf(
            TemplateSource(
                indicator = "concurrentTest",
                template = source1,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "iosTest",
                template = source0,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "otherTest",
                template = source2,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
        )

        val dependencies = mapOf(
            "commonTest" to sortedSetOf(),
            "metaTest" to sortedSetOf("commonTest"),
            "concurrentTest" to sortedSetOf("commonTest", "metaTest"),
            "nativeTest" to sortedSetOf("commonTest", "concurrentTest", "metaTest"),
            "otherTest" to sortedSetOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
            "iosTest" to sortedSetOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
        )

        // When
        val actual = SourceFilter(dependencies, mockk()).filterByDependencies(sources)

        // Then
        actual mustBe listOf(sources[0])
    }

    @Test
    fun `Given filterByDependencies is called with aggregated SharedSource it ignores sources which are children of while leaving independent one alone`() {
        // Given
        val source0: KSClassDeclaration = mockk()
        val source1: KSClassDeclaration = mockk()
        val source2: KSClassDeclaration = mockk()
        val source3: KSClassDeclaration = mockk()

        val packageName: String = fixture.fixture()
        val interfaceName: String = fixture.fixture()

        val sources = listOf(
            TemplateSource(
                indicator = "darwinTest",
                template = source1,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "iosTest",
                template = source0,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "otherTest",
                template = source2,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "linuxTest",
                template = source3,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
        )

        val dependencies = mapOf(
            "commonTest" to sortedSetOf(),
            "metaTest" to sortedSetOf("commonTest"),
            "concurrentTest" to sortedSetOf("commonTest", "metaTest"),
            "linuxTest" to sortedSetOf("commonTest", "concurrentTest", "metaTest", "otherTest"),
            "otherTest" to sortedSetOf("commonTest", "concurrentTest", "metaTest"),
            "darwinTest" to sortedSetOf("commonTest", "concurrentTest", "metaTest"),
            "iosTest" to sortedSetOf("commonTest", "concurrentTest", "metaTest", "darwinTest"),
        )

        // When
        val actual = SourceFilter(dependencies, mockk()).filterByDependencies(sources)

        // Then
        actual mustBe listOf(sources[0], sources[2])
    }

    @Test
    fun `Given filterByDependencies is called with aggregated SharedSource it filters up to a shared transitive parent`() {
        // Given
        val source0: KSClassDeclaration = mockk()
        val source1: KSClassDeclaration = mockk()
        val source2: KSClassDeclaration = mockk()
        val source3: KSClassDeclaration = mockk()
        val source4: KSClassDeclaration = mockk()
        val source5: KSClassDeclaration = mockk()

        val packageName: String = fixture.fixture()
        val interfaceName: String = fixture.fixture()

        val sources = listOf(
            TemplateSource(
                indicator = "iosTest",
                template = source0,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "darwinTest",
                template = source1,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "concurrentTest",
                template = source2,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "nativeTest",
                template = source3,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "otherTest",
                template = source4,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
            TemplateSource(
                indicator = "metaTest",
                template = source5,
                templateName = interfaceName,
                packageName = packageName,
                generics = null,
                dependencies = emptyList()
            ),
        )

        val dependencies = mapOf(
            "commonTest" to sortedSetOf(),
            "metaTest" to sortedSetOf("commonTest"),
            "concurrentTest" to sortedSetOf("commonTest", "metaTest"),
            "nativeTest" to sortedSetOf("commonTest", "concurrentTest", "metaTest"),
            "otherTest" to sortedSetOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
            "darwinTest" to sortedSetOf("nativeTest", "commonTest", "concurrentTest", "metaTest"),
            "iosTest" to sortedSetOf("nativeTest", "commonTest", "concurrentTest", "metaTest", "darwinTest"),
        )

        // When
        val actual = SourceFilter(dependencies, mockk()).filterByDependencies(sources)

        // Then
        actual mustBe listOf(sources[5])
    }
}
