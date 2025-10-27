package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder

@Composable
fun Box(modifier: ModifierStackBuilder = ModifierStackBuilder {}, content: @Composable () -> Unit) {
    Layout(modifier, content = content) { measurables, constraints ->

        layout(constraints.maxWidth, constraints.maxHeight) {

        }
    }
}

/**
 * A Box without any content
 */
@Composable
fun Box(modifier: ModifierStackBuilder) {
    Box(modifier, {})
}