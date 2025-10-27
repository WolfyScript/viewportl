package com.wolfyscript.viewportl.gui.compose.layout

import kotlin.math.max

class Constraints(
    val minWidth: Size = 0.slots or 0.dp,
    val maxWidth: Size = Int.MAX_VALUE.slots or Int.MAX_VALUE.dp,
    val minHeight: Size = 0.slots or 0.dp,
    val maxHeight: Size = Int.MAX_VALUE.slots or Int.MAX_VALUE.dp,
) {

    companion object {

    }

    fun offset(horizontal: Size, vertical: Size): Constraints {
        return Constraints(
            minWidth = max(minWidth.slot.value + horizontal.slot.value, 0).slots or max(minWidth.dp.value + horizontal.dp.value, 0).dp,
            maxWidth = max(maxWidth.slot.value + horizontal.slot.value, 0).slots or max(maxWidth.dp.value + horizontal.dp.value, 0).dp,
            minHeight = max(minHeight.slot.value + vertical.slot.value, 0).slots or max(minHeight.dp.value + vertical.dp.value, 0).dp,
            maxHeight = max(maxHeight.slot.value + vertical.slot.value, 0).slots or max(maxHeight.dp.value + vertical.dp.value, 0).dp,
        )
    }

}

fun Constraints.constrainWidth(width: Size): Size {
    return Size(
        width.slot.value.coerceIn(minWidth.slot.value, maxWidth.slot.value).slots,
        width.dp.value.coerceIn(minWidth.dp.value, maxWidth.dp.value).dp
    )
}

fun Constraints.constrainHeight(height: Size): Size {
    return Size(
        height.slot.value.coerceIn(minHeight.slot.value, maxHeight.slot.value).slots,
        height.dp.value.coerceIn(minHeight.dp.value, maxHeight.dp.value).dp
    )
}



