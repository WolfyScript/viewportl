package com.wolfyscript.viewportl.ui.layout

import com.wolfyscript.viewportl.ui.modifier.RowColumnScopeData

/**
 * MeasurePolicy for layouts with a Main and Cross Axis.
 *
 * @see com.wolfyscript.viewportl.foundation.Row
 * @see com.wolfyscript.viewportl.foundation.Column
 */
internal interface LayoutAxisMeasurePolicy {

    val orientation: LayoutOrientation

    fun Placeable.mainAxisSize(): Dp

    fun Placeable.crossAxisSize(): Dp

    fun createConstraints(
        mainAxisMin: Dp,
        mainAxisMax: Dp,
        crossAxisMin: Dp,
        crossAxisMax: Dp,
    ): Constraints

    fun arrangeMainAxis(
        measureScope: MeasureScope,
        mainAxisSpace: Dp,
        mainAxisChildSizes: Array<Dp>,
    ): Array<Dp>

    fun placeItems(
        measureScope: MeasureScope,
        placeables: Array<Placeable?>,
        mainAxisPositions: Array<Dp>,
        mainAxisSpace: Dp,
        crossAxisSpace: Dp,
    ): Measurements

    fun measure(
        measureScope: MeasureScope,
        measurables: List<Measurable>,
        mainAxisMin: Dp,
        crossAxisMin: Dp,
        mainAxisMax: Dp,
        crossAxisMax: Dp,
    ): Measurements {
        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val childMainAxisSizes = Array(measurables.size) { Dp.Zero }

        var fixedSpace = Dp.Zero
        var weightedSpace = Dp.Zero
        var crossAxisSpace = Dp.Zero

        var totalWeight = 0f
        var weightedChildren = 0

        for ((i, measurable) in measurables.withIndex()) {
            (measurable.scopeData as? RowColumnScopeData)?.apply {
                if (weight > 0f) {
                    totalWeight += weight
                    weightedChildren++
                    continue
                }
            }

            val remainingSpace = mainAxisMax - fixedSpace
            val placeable = measurable.measure(
                createConstraints(
                    mainAxisMin = Dp.Zero,
                    mainAxisMax = remainingSpace,
                    crossAxisMin = Dp.Zero,
                    crossAxisMax = crossAxisMax
                )
            )

            childMainAxisSizes[i] = placeable.mainAxisSize()
            fixedSpace += placeable.mainAxisSize()
            crossAxisSpace = max(crossAxisSpace, placeable.crossAxisSize())
            placeables[i] = placeable
        }

        if (weightedChildren > 0) {
            val remainingAvailableSpace = mainAxisMax - fixedSpace
            val weightUnitSpace = remainingAvailableSpace / totalWeight

            var remainingSpace = remainingAvailableSpace
            for (measurable in measurables) {
                (measurable.scopeData as? RowColumnScopeData)?.apply {
                    val weightedSpace = weightUnitSpace * weight
                    remainingSpace -= weightedSpace
                }
            }

            for ((i, measurable) in measurables.withIndex()) {
                if (placeables[i] != null) {
                    continue
                }

                val scopeData = measurable.scopeData as? RowColumnScopeData
                val weight = scopeData?.weight ?: 0f
                assert(weight > 0) { "Weighted Items should have a weight >0" }

                val weightedSize = max(0.dp, weightUnitSpace * weight)

                val placeable = measurable.measure(
                    createConstraints(
                        mainAxisMin = if (scopeData?.fill ?: false) weightedSize else Dp.Zero,
                        mainAxisMax = weightedSize,
                        crossAxisMin = Dp.Zero,
                        crossAxisMax = crossAxisMax
                    )
                )

                childMainAxisSizes[i] = placeable.mainAxisSize()
                weightedSpace += placeable.mainAxisSize()
                crossAxisSpace = max(crossAxisSpace, placeable.crossAxisSize())
                placeables[i] = placeable
            }
            weightedSpace = weightedSpace.coerceIn(Dp.Zero, mainAxisMax - fixedSpace)
        }

        val finalMainAxis = max(mainAxisMin, (fixedSpace + weightedSpace).coerceAtLeast(Dp.Zero))
        val finalCrossAxis = max(crossAxisMin, crossAxisSpace)

        val mainAxisPositions = arrangeMainAxis(measureScope, finalMainAxis, childMainAxisSizes)

        return placeItems(
            measureScope,
            placeables,
            mainAxisPositions,
            finalMainAxis,
            finalCrossAxis
        )
    }

}