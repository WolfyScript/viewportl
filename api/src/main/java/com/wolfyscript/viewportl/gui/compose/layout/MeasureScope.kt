package com.wolfyscript.viewportl.gui.compose.layout

interface MeasureScope {

    fun layout(width: Size, height: Size, placement: PlacementScope.() -> Unit) : Measurements

}