package com.wolfyscript.viewportl.gui.compose

import com.wolfyscript.viewportl.gui.compose.layout.Constraints

fun interface MeasurePolicy {

    fun MeasureScope.measure(
        measurable: List<Node>, //TODO
        constraints: Constraints,
    )

}