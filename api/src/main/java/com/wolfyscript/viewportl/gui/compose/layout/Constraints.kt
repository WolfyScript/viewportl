package com.wolfyscript.viewportl.gui.compose.layout

class Constraints(
    val minWidth: Size = Size(Slot(0)),
    val maxWidth: Size = Size(Slot(Int.MIN_VALUE)),
    val minHeight: Size = Size(Slot(0)),
    val maxHeight: Size = Size(Slot(Int.MIN_VALUE)),
) {
}