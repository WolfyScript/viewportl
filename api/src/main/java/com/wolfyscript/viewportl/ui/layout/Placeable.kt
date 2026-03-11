package com.wolfyscript.viewportl.ui.layout

interface Placeable {

    val width: Dp

    val height: Dp

    fun placeNoOffset(x: Dp, y: Dp)

}