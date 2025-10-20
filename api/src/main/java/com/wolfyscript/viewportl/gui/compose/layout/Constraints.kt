package com.wolfyscript.viewportl.gui.compose.layout

class Constraints(
    val minWidth: Size = 0.slots or 0.dp,
    val maxWidth: Size = Int.MAX_VALUE.slots or Int.MAX_VALUE.dp,
    val minHeight: Size = 0.slots or 0.dp,
    val maxHeight: Size = Int.MAX_VALUE.slots or Int.MAX_VALUE.dp,
) {
}