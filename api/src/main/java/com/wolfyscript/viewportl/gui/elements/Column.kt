package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Placeable
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.layout.dp
import com.wolfyscript.viewportl.gui.compose.layout.max
import com.wolfyscript.viewportl.gui.compose.layout.or
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder

@Composable
fun Column(
    modifier: ModifierStackBuilder = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) {
    Layout(modifier, content = content) { measurables, constraints ->
        if (measurables.isEmpty()) {
            return@Layout layout(constraints.minWidth, constraints.minHeight) {}
        }

        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val childHeightSizes = Array(measurables.size) { Size.Zero }
        var fixedSpace = Size.Zero
        var width = Size.Zero

        for ((i, measurable) in measurables.withIndex()) {
            val remainingSpace = constraints.maxHeight - fixedSpace

            val placeable = measurable.measure(
                Constraints(
                    minWidth = Size.Zero,
                    maxWidth = constraints.maxWidth,
                    minHeight = Size.Zero,
                    maxHeight = remainingSpace
                )
            )

            childHeightSizes[i] = placeable.height
            fixedSpace += placeable.height
            width = max(width, placeable.width)
            placeables[i] = placeable
        }

        val finalWidth = max(constraints.minWidth, width)
        val finalHeight = max(constraints.minHeight, fixedSpace)

        val mainAxisPositions = verticalArrangement.arrange(finalHeight, childHeightSizes)

        layout(finalWidth, finalHeight) {
            // TODO: horizontal Alignment
            val x = 0.slots or 0.dp

            placeables.forEachIndexed { index, placeable ->
                placeable?.placeAt(x, mainAxisPositions[index])
            }
        }
    }
}