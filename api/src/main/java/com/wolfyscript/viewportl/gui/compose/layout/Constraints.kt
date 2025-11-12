package com.wolfyscript.viewportl.gui.compose.layout

import kotlin.math.max

class Constraints(
    val minWidth: Size = 0.slots or 0.dp,
    val maxWidth: Size = Int.MAX_VALUE.slots or Int.MAX_VALUE.dp,
    val minHeight: Size = 0.slots or 0.dp,
    val maxHeight: Size = Int.MAX_VALUE.slots or Int.MAX_VALUE.dp,
) {

    /**
     * Grows or shrinks the current constraints.
     *
     * Both the [minWidth], [minHeight] and the [maxWidth], [maxHeight] are grown/shrunk according to [horizontal], [vertical].
     *
     */
    fun offset(horizontal: Size, vertical: Size): Constraints {
        return Constraints(
            minWidth = max(minWidth.slot.value + horizontal.slot.value, 0).slots or max(minWidth.dp.value + horizontal.dp.value, 0).dp,
            maxWidth = max(maxWidth.slot.value + horizontal.slot.value, 0).slots or max(maxWidth.dp.value + horizontal.dp.value, 0).dp,
            minHeight = max(minHeight.slot.value + vertical.slot.value, 0).slots or max(minHeight.dp.value + vertical.dp.value, 0).dp,
            maxHeight = max(maxHeight.slot.value + vertical.slot.value, 0).slots or max(maxHeight.dp.value + vertical.dp.value, 0).dp,
        )
    }

}

/**
 * Makes sure that [width] is within this constraints' specification:
 *
 * [ [Constraints.minWidth], [Constraints.maxWidth] ]
 *
 * @return a new width that is within `[minWidth, maxWidth]`
 */
fun Constraints.constrainWidth(width: Size): Size {
    return Size(
        width.slot.value.coerceIn(minWidth.slot.value, maxWidth.slot.value).slots,
        width.dp.value.coerceIn(minWidth.dp.value, maxWidth.dp.value).dp
    )
}

/**
 * Makes sure that [height] is within this constraints' specification:
 *
 * [ [Constraints.minHeight], [Constraints.maxHeight] ]
 *
 * @return a new height that is within `[minHeight, maxHeight]`
 */
fun Constraints.constrainHeight(height: Size): Size {
    return Size(
        height.slot.value.coerceIn(minHeight.slot.value, maxHeight.slot.value).slots,
        height.dp.value.coerceIn(minHeight.dp.value, maxHeight.dp.value).dp
    )
}

fun Constraints.constrain(otherConstraints: Constraints): Constraints {
    // TODO: Rethink the whole shared slot/dp Size...
    return Constraints(
        minWidth = otherConstraints.minWidth.slot.value.coerceIn(minWidth.slot.value, maxWidth.slot.value).slots or otherConstraints.minWidth.dp.value.coerceIn(minWidth.dp.value, maxWidth.dp.value).dp,
        maxWidth = otherConstraints.maxWidth.slot.value.coerceIn(minWidth.slot.value, maxWidth.slot.value).slots or otherConstraints.maxWidth.dp.value.coerceIn(minWidth.dp.value, maxWidth.dp.value).dp,
        minHeight = otherConstraints.minHeight.slot.value.coerceIn(minHeight.slot.value, maxHeight.slot.value).slots or otherConstraints.minHeight.dp.value.coerceIn(minHeight.dp.value, maxHeight.dp.value).dp,
        maxHeight = otherConstraints.maxHeight.slot.value.coerceIn(minHeight.slot.value, minHeight.slot.value).slots or otherConstraints.maxHeight.dp.value.coerceIn(minHeight.slot.value, maxHeight.slot.value).dp,
    )
}



