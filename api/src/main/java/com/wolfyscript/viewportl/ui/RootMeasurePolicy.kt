package com.wolfyscript.viewportl.ui

import com.wolfyscript.viewportl.ui.MeasurePolicy
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.layout.Measurable
import com.wolfyscript.viewportl.ui.layout.MeasureScope
import com.wolfyscript.viewportl.ui.layout.Measurements
import com.wolfyscript.viewportl.ui.layout.constrainHeight
import com.wolfyscript.viewportl.ui.layout.constrainWidth
import com.wolfyscript.viewportl.ui.layout.dp

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