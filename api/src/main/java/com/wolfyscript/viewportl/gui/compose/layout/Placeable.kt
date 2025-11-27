package com.wolfyscript.viewportl.gui.compose.layout

interface Placeable {

    val width: Dp

    val height: Dp

    fun placeAt(x: Dp, y: Dp)

}