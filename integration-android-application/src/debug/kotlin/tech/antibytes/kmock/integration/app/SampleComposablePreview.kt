/*
 * Copyright (c) 2024 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Preview
@Composable
fun SampleComposablePreview() {
    val flow = MutableStateFlow("generic text")

    SampleComposable(viewModel = ViewModelStub(flow))
}

private class ViewModelStub(override val flow: StateFlow<String>) : AppContract.SampleViewModel {
    override fun doSomething() {
        TODO("Not yet implemented")
    }
}
