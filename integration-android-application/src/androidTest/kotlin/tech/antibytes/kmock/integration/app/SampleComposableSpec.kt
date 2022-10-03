/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tech.antibytes.kfixture.fixture
import tech.antibytes.kfixture.kotlinFixture
import tech.antibytes.kmock.Mock
import tech.antibytes.kmock.integration.kmock

@Mock(AppContract.SampleViewModel::class)
class SampleComposableSpec {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val flow = MutableStateFlow("")
    private val viewModel: SampleViewModelMock = kmock()
    private val fixture = kotlinFixture()

    @Before
    fun setUp() {
        flow.update { "" }
        viewModel._clearMock()
    }

    @Test
    fun It_has_a_default_value() {
        // Given
        val value: String = fixture.fixture()

        flow.update { value }

        viewModel._flow.getValue = flow

        // When
        composeTestRule.setContent {
            SampleComposable(viewModel = viewModel)
        }

        // Then
        composeTestRule
            .onNodeWithText(value)
            .assertIsDisplayed()
    }

    @Test
    fun It_changes_its_value() {
        // Given
        val value: String = fixture.fixture()

        viewModel._flow.getValue = flow
        viewModel._doSomething.sideEffect = {
            flow.tryEmit(value)
            Unit
        }

        // When
        composeTestRule.setContent {
            SampleComposable(viewModel = viewModel)
        }

        composeTestRule
            .onNodeWithText("Click me")
            .performClick()

        // Then
        composeTestRule
            .onNodeWithText(value)
            .assertIsDisplayed()
    }
}
