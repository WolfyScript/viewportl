package com.wolfyscript.viewportl.gui.compose.layout

enum class Direction {

    Vertical,
    Horizontal,
    Both;

    val isVertical: Boolean
        get() = this == Vertical || this == Both

    val isHorizontal: Boolean
        get() = this == Horizontal || this == Both

}