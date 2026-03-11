package com.wolfyscript.viewportl.ui.layout

enum class FillDirection {

    Vertical,
    Horizontal,
    Both;

    val isVertical: Boolean
        get() = this == Vertical || this == Both

    val isHorizontal: Boolean
        get() = this == Horizontal || this == Both

}