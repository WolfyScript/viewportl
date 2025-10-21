package com.wolfyscript.viewportl.gui.compose

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Measurable
import com.wolfyscript.viewportl.gui.compose.layout.MeasureScope
import com.wolfyscript.viewportl.gui.compose.layout.Measurements

fun interface MeasurePolicy {

    fun MeasureScope.measure(
        measurable: List<Measurable>, //TODO
        constraints: Constraints,
    ) : Measurements

}