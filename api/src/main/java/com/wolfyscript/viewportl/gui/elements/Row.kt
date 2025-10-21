package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable

@Composable
fun Row(content: @Composable () -> Unit) {
    Layout({}, content = content) { measurable, constraints ->

        layout(constraints.maxWidth, constraints.maxHeight) {

        }
    }
}