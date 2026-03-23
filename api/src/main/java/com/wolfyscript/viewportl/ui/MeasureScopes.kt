package com.wolfyscript.viewportl.ui

import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.layout.IntrinsicMeasureScope
import com.wolfyscript.viewportl.ui.layout.LayoutDirection
import com.wolfyscript.viewportl.ui.layout.MeasureScope
import com.wolfyscript.viewportl.ui.layout.Measurements
import com.wolfyscript.viewportl.ui.layout.PlacementScope

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

class LayoutCapableIntrinsicMeasureScope(
    wrapped: IntrinsicMeasureScope,
    override val layoutDirection: LayoutDirection
) : MeasureScope, IntrinsicMeasureScope by wrapped {

    override fun layout(
        width: Dp,
        height: Dp,
        placement: PlacementScope.() -> Unit,
    ): Measurements {
        return Measurements(width, height)
    }

}