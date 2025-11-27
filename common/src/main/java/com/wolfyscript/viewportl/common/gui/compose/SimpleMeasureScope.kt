package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.gui.compose.layout.MeasureScope
import com.wolfyscript.viewportl.gui.compose.layout.Measurements
import com.wolfyscript.viewportl.gui.compose.layout.PlacementScope
import com.wolfyscript.viewportl.gui.compose.layout.Dp

class SimpleMeasureScope : MeasureScope {

    override fun layout(
        width: Dp,
        height: Dp,
        placement: PlacementScope.() -> Unit,
    ): Measurements {
        return Measurements(width, height, placement)
    }
}