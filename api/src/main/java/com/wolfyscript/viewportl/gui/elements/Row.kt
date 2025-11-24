package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.LayoutDirection
import com.wolfyscript.viewportl.gui.compose.layout.Placeable
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.layout.dp
import com.wolfyscript.viewportl.gui.compose.layout.max
import com.wolfyscript.viewportl.gui.compose.layout.or
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier

@Composable
fun Row(
    modifier: ModifierStackBuilder = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable () -> Unit
) {
    Layout(modifier, content = content) { measurables, constraints ->
        if (measurables.isEmpty()) {
            return@Layout layout(constraints.minWidth, constraints.minHeight) {}
        }

        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val childWidthSizes = Array(measurables.size) { Size.Zero }
        var fixedSpace = Size.Zero
        var height = Size.Zero

        for ((i, measurable) in measurables.withIndex()) {
            val remainingSpace = constraints.maxWidth - fixedSpace

            val placeable = measurable.measure(
                Constraints(
                    minWidth = Size.Zero,
                    maxWidth = remainingSpace,
                    minHeight = Size.Zero,
                    maxHeight = constraints.maxHeight
                )
            )

            childWidthSizes[i] = placeable.width
            fixedSpace += placeable.width
            height = max(height, placeable.height)
            placeables[i] = placeable
        }

        val finalWidth = max(constraints.minWidth, fixedSpace)
        val finalHeight = max(constraints.minHeight, height)

        val mainAxisPositions = horizontalArrangement.arrange(finalWidth, LayoutDirection.LtR, childWidthSizes)

        layout(finalWidth, finalHeight) {
            // TODO: vertical Alignment
            val y = 0.slots or 0.dp

            placeables.forEachIndexed { index, placeable ->
                placeable?.placeAt(mainAxisPositions[index], y)
            }
        }
    }
}