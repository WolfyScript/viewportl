package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable

@Composable
fun Column(content: @Composable () -> Unit) {
    Layout(content = content) { measurables, constraints ->

    }
}