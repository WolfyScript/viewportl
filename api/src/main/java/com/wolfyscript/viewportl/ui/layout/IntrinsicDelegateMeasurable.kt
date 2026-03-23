package com.wolfyscript.viewportl.ui.layout

import com.wolfyscript.viewportl.ui.IntrinsicDimension
import com.wolfyscript.viewportl.ui.IntrinsicSize

/**
 * Wraps an [IntrinsicMeasurable] in a [Measurable] and delegates the [measure] to the intrinsic functions of the wrapped [measurable].
 */
class IntrinsicDelegateMeasurable(
    val measurable: IntrinsicMeasurable,
    val intrinsicSize: IntrinsicSize,
    val intrinsicDimension: IntrinsicDimension,
) : Measurable {

    override val scopeData = measurable.scopeData

    override fun measure(constraints: Constraints): Placeable {
        return if (intrinsicDimension == IntrinsicDimension.Width) {
            val width = if (intrinsicSize == IntrinsicSize.Min) {
                minIntrinsicWidth(constraints.maxHeight)
            } else {
                maxIntrinsicWidth(constraints.maxHeight)
            }
            FixedIntrinsicSizePlaceable(width, constraints.maxHeight)
        } else {
            val height = if (intrinsicSize == IntrinsicSize.Min) {
                minIntrinsicHeight(constraints.maxWidth)
            } else {
                maxIntrinsicHeight(constraints.maxWidth)
            }
            FixedIntrinsicSizePlaceable(constraints.maxWidth, height)
        }
    }

    override fun minIntrinsicWidth(height: Dp): Dp = measurable.minIntrinsicWidth(height)

    override fun maxIntrinsicWidth(height: Dp): Dp = measurable.maxIntrinsicWidth(height)

    override fun minIntrinsicHeight(width: Dp): Dp = measurable.minIntrinsicHeight(width)

    override fun maxIntrinsicHeight(width: Dp): Dp = measurable.maxIntrinsicHeight(width)

}

private class FixedIntrinsicSizePlaceable(override val width: Dp, override val height: Dp) : Placeable {

    override fun placeNoOffset(x: Dp, y: Dp) {}

}