package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable

@Composable
fun Box(content: @Composable () -> Unit) {
    Layout(content = content) { measurables, constraints ->

    }
}