package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.gui.compose.layout.MeasureScope
import com.wolfyscript.viewportl.gui.compose.layout.Measurements
import com.wolfyscript.viewportl.gui.compose.layout.PlacementScope
import com.wolfyscript.viewportl.gui.compose.layout.Size

class SimpleMeasureScope : MeasureScope {

    override fun layout(
        width: Size,
        height: Size,
        placement: PlacementScope.() -> Unit,
    ): Measurements {
        return Measurements(width, height, placement)
    }
}