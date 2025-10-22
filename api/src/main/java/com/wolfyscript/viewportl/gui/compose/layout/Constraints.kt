package com.wolfyscript.viewportl.gui.compose.layout

class Constraints(
    val minWidth: Size = 0.slots or 0.dp,
    val maxWidth: Size = Int.MAX_VALUE.slots or Int.MAX_VALUE.dp,
    val minHeight: Size = 0.slots or 0.dp,
    val maxHeight: Size = Int.MAX_VALUE.slots or Int.MAX_VALUE.dp,
) {

    companion object {

    }

}

fun Constraints.constrainWidth(width: Size): Size {
    return Size(
        width.slot?.value?.coerceIn(minWidth.slot?.value ?: 0, maxWidth.slot?.value ?: 0)?.slots,
        width.dp?.value?.coerceIn(minWidth.dp?.value ?: 0, maxWidth.dp?.value ?: 0)?.dp
    )
}

fun Constraints.constrainHeight(height: Size): Size {
    return Size(
        height.slot?.value?.coerceIn(minHeight.slot?.value ?: 0, maxHeight.slot?.value ?: 0)?.slots,
        height.dp?.value?.coerceIn(minHeight.dp?.value ?: 0, maxHeight.dp?.value ?: 0)?.dp
    )
}



