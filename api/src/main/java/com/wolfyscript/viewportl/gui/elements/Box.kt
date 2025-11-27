package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.gui.compose.MeasurePolicy
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.LayoutDirection
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Placeable
import com.wolfyscript.viewportl.gui.compose.layout.max
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder

/**
 * A Box centers its content. If more than one child is present they are rendered above each other.
 */
@Composable
fun Box(
    modifier: ModifierStackBuilder = Modifier,
    contentAlignment: Alignment.HorizontalAndVertical = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    content: @Composable () -> Unit
) {
    Layout(modifier, content = content) { measurables, constraints ->
        if (measurables.isEmpty()) {
            return@Layout layout(constraints.minWidth, constraints.minHeight) {}
        }

        val contentConstraints = if (propagateMinConstraints) {
            constraints
        } else {
            Constraints(maxWidth = constraints.maxWidth, maxHeight = constraints.maxHeight)
        }

        if (measurables.size == 1) {
            val placeable = measurables[0].measure(contentConstraints)
            val width = max(constraints.minWidth, placeable.width)
            val height = max(constraints.minHeight, placeable.height)

            return@Layout layout(width, height) {
                val offset = contentAlignment.align(Offset(placeable.width, placeable.height), Offset(width, height), LayoutDirection.LtR)
                placeable.placeAt(offset.x, offset.y)
            }
        }

        val placeables = arrayOfNulls<Placeable>(measurables.size)
        var width = contentConstraints.minWidth
        var height = contentConstraints.minHeight

        for ((index, measurable) in measurables.withIndex()) {
            val placeable = measurable.measure(contentConstraints)
            placeables[index] = placeable
            width = max(width, placeable.width)
            height = max(height, placeable.height)
        }

        layout(width, height) {
            for (placeable in placeables) {
                placeable?.let {
                    val offset = contentAlignment.align(Offset(it.width, it.height), Offset(width, height), LayoutDirection.LtR)
                    it.placeAt(offset.x, offset.y)
                }
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