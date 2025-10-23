package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.gui.compose.MeasurePolicy
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Measurable
import com.wolfyscript.viewportl.gui.compose.layout.MeasureScope
import com.wolfyscript.viewportl.gui.compose.layout.Measurements
import com.wolfyscript.viewportl.gui.compose.layout.constrainHeight
import com.wolfyscript.viewportl.gui.compose.layout.constrainWidth
import com.wolfyscript.viewportl.gui.compose.layout.dp
import com.wolfyscript.viewportl.gui.compose.layout.or
import com.wolfyscript.viewportl.gui.compose.layout.slots

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
                    placeable.placeAt(0.slots or 0.dp, 0.slots or 0.dp)
                }
            }

            else -> {
                val placeables = measurables.map { it.measure(constraints) }

                var maxWidth = 0.slots or 0.dp
                var maxHeight = 0.slots or 0.dp
                for (placeable in placeables) {
                    maxWidth = maxOf(placeable.width.slot?.value ?: 0, maxWidth.slot?.value ?: 0).slots or maxOf(placeable.width.dp?.value ?: 0, maxWidth.dp?.value ?: 0).dp
                    maxHeight = maxOf(placeable.height.slot?.value ?: 0, maxHeight.slot?.value ?: 0).slots or maxOf(placeable.height.dp?.value ?: 0, maxHeight.dp?.value ?: 0).dp
                }

                layout(constraints.constrainWidth(maxWidth), constraints.constrainHeight(maxHeight)) {
                    for (placeable in placeables) {
                        placeable.placeAt(0.slots or 0.dp, 0.slots or 0.dp)
                    }
                }
            }
        }
    }


}