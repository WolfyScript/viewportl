package com.wolfyscript.viewportl.ui

import com.wolfyscript.viewportl.ui.layout.*

fun interface MeasurePolicy {

    fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): Measurements

    /**
     * The minimum width this layout can take, given a specific height, such that the content of the layout will be rendered correctly.
     *
     * The default implementation uses [measure] as an approximation.
     */
    fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Dp,
    ): Dp {
        val wrapped = measurables.map { IntrinsicDelegateMeasurable(it, IntrinsicSize.Min, IntrinsicDimension.Width) }
        val constraints = Constraints(maxHeight = height)
        val scope = LayoutCapableIntrinsicMeasureScope(this, layoutDirection)
        val measurements = scope.measure(wrapped, constraints)
        return measurements.width
    }

    /**
     * The minimum height this layout can take, given a specific width, such that the content of the layout will be rendered correctly.
     *
     * The default implementation uses [measure] as an approximation.
     */
    fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Dp,
    ): Dp {
        val wrapped = measurables.map { IntrinsicDelegateMeasurable(it, IntrinsicSize.Min, IntrinsicDimension.Height) }
        val constraints = Constraints(maxWidth = width)
        val scope = LayoutCapableIntrinsicMeasureScope(this, layoutDirection)
        val measurements = scope.measure(wrapped, constraints)
        return measurements.height
    }

    /**
     * The minimum width, that when increased further will not decrease the minimum intrinsic height.
     *
     * The default implementation uses [measure] as an approximation.
     */
    fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Dp,
    ): Dp {
        val wrapped = measurables.map { IntrinsicDelegateMeasurable(it, IntrinsicSize.Max, IntrinsicDimension.Width) }
        val constraints = Constraints(maxHeight = height)
        val scope = LayoutCapableIntrinsicMeasureScope(this, layoutDirection)
        val measurements = scope.measure(wrapped, constraints)
        return measurements.width
    }

    /**
     * The minimum height, that when increased further will not decrease the minimum intrinsic width.
     *
     * The default implementation uses [measure] as an approximation.
     */
    fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Dp,
    ): Dp {
        val wrapped = measurables.map { IntrinsicDelegateMeasurable(it, IntrinsicSize.Max, IntrinsicDimension.Height) }
        val constraints = Constraints(maxWidth = width)
        val scope = LayoutCapableIntrinsicMeasureScope(this, layoutDirection)
        val measurements = scope.measure(wrapped, constraints)
        return measurements.height
    }

}