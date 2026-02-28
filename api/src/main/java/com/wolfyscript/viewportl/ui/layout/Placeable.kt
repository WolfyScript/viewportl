package com.wolfyscript.viewportl.ui.layout

interface Placeable {

    val width: Dp

    val height: Dp

    fun placeAt(x: Dp, y: Dp)

}