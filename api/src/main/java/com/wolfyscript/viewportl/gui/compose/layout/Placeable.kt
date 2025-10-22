package com.wolfyscript.viewportl.gui.compose.layout

interface Placeable {

    val width: Size

    val height: Size

    fun placeAt(x: Size, y: Size)

}