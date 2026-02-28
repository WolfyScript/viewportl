package com.wolfyscript.viewportl.ui

import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.layout.IntrinsicMeasurable
import com.wolfyscript.viewportl.ui.layout.IntrinsicMeasureScope
import com.wolfyscript.viewportl.ui.layout.Measurable
import com.wolfyscript.viewportl.ui.layout.MeasureScope
import com.wolfyscript.viewportl.ui.layout.Measurements

fun interface MeasurePolicy {

    fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): Measurements

    fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Dp,
    ): Dp {
        return Dp.Zero
    }

    fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Dp,
    ): Dp {
        return Dp.Zero
    }

    fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Dp,
    ): Dp {
        return Dp.Zero
    }

    fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Dp,
    ): Dp {
        return Dp.Zero
    }

}