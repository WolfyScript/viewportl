package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.constrainHeight
import com.wolfyscript.viewportl.gui.compose.layout.constrainWidth
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder

@Composable
fun Box(modifier: ModifierStackBuilder = Modifier, content: @Composable () -> Unit) {
    Layout(modifier, content = content) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight

        val placeables = measurables.map {
            val placeable = it.measure(constraints)
            val offset = Offset(
                (width - constraints.constrainWidth(placeable.width)) / 2,
                (height - constraints.constrainHeight(placeable.height)) / 2
            )
            placeable to offset
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            for (placeableOffset in placeables) {
                placeableOffset.first.placeAt(placeableOffset.second.x, placeableOffset.second.y)
            }
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