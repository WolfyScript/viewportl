package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.gui.compose.MeasurePolicy
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Dp
import com.wolfyscript.viewportl.gui.compose.layout.Measurable
import com.wolfyscript.viewportl.gui.compose.layout.MeasureScope
import com.wolfyscript.viewportl.gui.compose.layout.Measurements
import com.wolfyscript.viewportl.gui.compose.layout.constrainHeight
import com.wolfyscript.viewportl.gui.compose.layout.constrainWidth
import com.wolfyscript.viewportl.gui.compose.layout.dp

object RootMeasurePolicy : MeasurePolicy {

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): Measurements {
        return when {
            measurables.isEmpty() -> {
                layout(constraints.minWidth, constraints.minHeight) {}
            }

            measurables.size == 1 -> {
                val placeable = measurables.first().measure(constraints)

                layout(constraints.constrainWidth(placeable.width), constraints.constrainHeight(placeable.height)) {
                    placeable.placeAt(Dp.Zero, Dp.Zero)
                }
            }

            else -> {
                val placeables = measurables.map { it.measure(constraints) }

                var maxWidth = Dp.Zero
                var maxHeight = Dp.Zero
                for (placeable in placeables) {
                    maxWidth = maxOf(placeable.width.value, maxWidth.value).dp
                    maxHeight = maxOf(placeable.height.value, maxHeight.value).dp
                }

                layout(constraints.constrainWidth(maxWidth), constraints.constrainHeight(maxHeight)) {
                    for (placeable in placeables) {
                        placeable.placeAt(Dp.Zero, Dp.Zero)
                    }
                }
            }
        }
    }


}