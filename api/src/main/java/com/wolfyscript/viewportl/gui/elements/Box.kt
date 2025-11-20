package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.gui.compose.MeasurePolicy
import com.wolfyscript.viewportl.gui.compose.layout.Placeable
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.layout.max
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder

/**
 * A Box centers its content. If more than one child is present they are rendered above each other.
 */
@Composable
fun Box(modifier: ModifierStackBuilder = Modifier, content: @Composable () -> Unit) {
    Layout(modifier, content = content) { measurables, constraints ->
        if (measurables.isEmpty()) {
            return@Layout layout(constraints.minWidth, constraints.minHeight) {}
        }

        if (measurables.size == 1) {
            val placeable = measurables[0].measure(constraints)
            val width = max(constraints.minWidth, placeable.width)
            val height = max(constraints.minHeight, placeable.height)

            return@Layout layout(width, height) {
                placeable.placeAt(Size.Zero, Size.Zero)
            }
        }

        val placeables = arrayOfNulls<Placeable>(measurables.size)
        var width = constraints.minWidth
        var height = constraints.minHeight

        for ((index, measurable) in measurables.withIndex()) {
            val placeable = measurable.measure(constraints)
            placeables[index] = placeable
            width = max(width, placeable.width)
            height = max(height, placeable.height)
        }

        layout(width, height) {
            for (placeable in placeables) {
                placeable?.placeAt(Size.Zero, Size.Zero)
            }
        }
    }
}

/**
 * A Box without any content
 */
@Composable
fun Box(modifier: ModifierStackBuilder) {
    Layout(modifier, content = {}, EmptyBoxMeasurePolicy)
}

private val EmptyBoxMeasurePolicy = MeasurePolicy { measurables, constraints -> layout(constraints.minWidth, constraints.minHeight) {} }