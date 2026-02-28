package com.wolfyscript.viewportl.ui.layout

interface MeasureScope : IntrinsicMeasureScope {

    fun layout(width: Dp, height: Dp, placement: PlacementScope.() -> Unit) : Measurements

}