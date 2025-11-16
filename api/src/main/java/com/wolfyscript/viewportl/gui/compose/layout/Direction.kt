package com.wolfyscript.viewportl.gui.compose.layout

enum class Direction {

    Vertical,
    Horizontal,
    Both;

    val isVertical: Boolean
        get() {
            return when (this) {
                Vertical, Both -> true
                Horizontal -> false
            }
        }

    val isHorizontal: Boolean
        get() {
            return when (this) {
                Vertical -> false
                Horizontal, Both -> true
            }
        }

}