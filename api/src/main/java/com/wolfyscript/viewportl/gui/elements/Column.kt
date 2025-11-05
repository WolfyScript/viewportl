package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.dp
import com.wolfyscript.viewportl.gui.compose.layout.or
import com.wolfyscript.viewportl.gui.compose.layout.slots

@Composable
fun Column(content: @Composable () -> Unit) {
    Layout({}, content = content) { measurables, constraints ->

        // TODO: Alignment options
        // TODO: adjust width based on children
        val maxHeightPerChild = constraints.maxHeight / measurables.size
        val childConstraints = Constraints(constraints.minWidth, constraints.maxWidth, constraints.minHeight, maxHeightPerChild)

        val placeables = measurables.map { it.measure(childConstraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            var offset = 0.slots or 0.dp
            for (placeable in placeables) {
                placeable.placeAt(0.slots or 0.dp, offset)
                offset += maxHeightPerChild
            }
        }
    }
}