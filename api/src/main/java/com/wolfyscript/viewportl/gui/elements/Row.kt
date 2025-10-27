package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.dp
import com.wolfyscript.viewportl.gui.compose.layout.or
import com.wolfyscript.viewportl.gui.compose.layout.slots

@Composable
fun Row(modifier: ModifierStackBuilder = ModifierStackBuilder {}, content: @Composable () -> Unit) {
    Layout(modifier, content = content) { measurables, constraints ->

        // TODO: Alignment options
        // TODO: adjust height based on children
        val maxWidthPerChild = constraints.maxWidth / measurables.size
        val childConstraints = Constraints(constraints.minWidth, maxWidthPerChild, constraints.minHeight, constraints.maxHeight)

        val placeables = measurables.map { it.measure(childConstraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            var offset = 0.slots or 0.dp
            for (placeable in placeables) {
                placeable.placeAt(offset, 0.slots or 0.dp)
                offset += maxWidthPerChild
            }
        }
    }
}