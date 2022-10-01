/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.kmock.integration.app

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

@Composable
fun SampleComposable(viewModel: AppContract.SampleViewModel) {
    val genericText = viewModel.flow.collectAsState()

    Text(genericText.value)

    TextButton(onClick = { viewModel.doSomething() }) {
        Text("Click me")
    }
}
