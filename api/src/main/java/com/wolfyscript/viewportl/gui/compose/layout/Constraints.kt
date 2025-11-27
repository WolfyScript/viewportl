package com.wolfyscript.viewportl.gui.compose.layout

import kotlin.math.max

class Constraints(
    val minWidth: Dp = 0.dp,
    val maxWidth: Dp = Dp.Infinity,
    val minHeight: Dp = 0.dp,
    val maxHeight: Dp = Dp.Infinity,
) {

    /**
     * Grows or shrinks the current constraints.
     *
     * Both the [minWidth], [minHeight] and the [maxWidth], [maxHeight] are grown/shrunk according to [horizontal], [vertical].
     *
     */
    fun offset(horizontal: Dp, vertical: Dp): Constraints {
        return Constraints(
            minWidth = Dp(max(minWidth.value + horizontal.value, 0f)),
            maxWidth = Dp(max(maxWidth.value + horizontal.value, 0f)),
            minHeight = Dp(max(minHeight.value + vertical.value, 0f)),
            maxHeight = Dp(max(maxHeight.value + vertical.value, 0f)),
        )
    }

    override fun toString(): String {
        return "Constraints(w=$minWidth h=$minHeight > x < w=$maxWidth h=$maxHeight)"
    }

}

/**
 * Makes sure that [width] is within this constraints' specification:
 *
 * [ [Constraints.minWidth], [Constraints.maxWidth] ]
 *
 * @return a new width that is within `[minWidth, maxWidth]`
 */
fun Constraints.constrainWidth(width: Dp): Dp {
    return Dp(
        width.value.coerceIn(minWidth.value, maxWidth.value)
    )
}

/**
 * Makes sure that [height] is within this constraints' specification:
 *
 * [ [Constraints.minHeight], [Constraints.maxHeight] ]
 *
 * @return a new height that is within `[minHeight, maxHeight]`
 */
fun Constraints.constrainHeight(height: Dp): Dp {
    return Dp(
        height.value.coerceIn(minHeight.value, maxHeight.value)
    )
}

fun Constraints.constrain(otherConstraints: Constraints): Constraints {
    return Constraints(
        minWidth = otherConstraints.minWidth.value.coerceIn(minWidth.value, maxWidth.value).dp,
        maxWidth = otherConstraints.maxWidth.value.coerceIn(minWidth.value, maxWidth.value).dp,
        minHeight = otherConstraints.minHeight.value.coerceIn(minHeight.value, maxHeight.value).dp,
        maxHeight = otherConstraints.maxHeight.value.coerceIn(minHeight.value, maxHeight.value).dp,
    )
}



