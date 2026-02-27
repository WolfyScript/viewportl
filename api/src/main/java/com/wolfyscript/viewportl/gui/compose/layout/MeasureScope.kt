package com.wolfyscript.viewportl.gui.compose.layout

interface MeasureScope : IntrinsicMeasureScope {

    fun layout(width: Dp, height: Dp, placement: PlacementScope.() -> Unit) : Measurements

}