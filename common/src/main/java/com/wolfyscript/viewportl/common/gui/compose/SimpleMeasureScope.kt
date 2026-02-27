package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.gui.compose.layout.*

class SimpleMeasureScope(
    override val layoutDirection: LayoutDirection
) : MeasureScope {

    override fun layout(
        width: Dp,
        height: Dp,
        placement: PlacementScope.() -> Unit,
    ): Measurements {
        return Measurements(width, height, placement)
    }
}