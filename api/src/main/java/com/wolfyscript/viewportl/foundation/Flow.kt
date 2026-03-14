package com.wolfyscript.viewportl.foundation

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.ui.IntrinsicSize
import com.wolfyscript.viewportl.ui.MeasurePolicy
import com.wolfyscript.viewportl.ui.layout.*
import com.wolfyscript.viewportl.ui.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.ui.modifier.RowColumnScopeData

/**
 * [FlowRow] is a variation of the [Row], that when it runs out of space for items in the row-line,
 * moves to the next row-line below the current, until the items run out.
 *
 * The fill direction is according to the current layout direction, either left-to-right ([LayoutDirection.LtR])
 * or right-to-left ([LayoutDirection.RtL]). The cross axis fill direction is always top to bottom.
 *
 * @param modifier The modifier applied to the FlowRow
 * @param horizontalLineArrangement The horizontal arrangement of items in each line
 * @param verticalLineAlignment The vertical cross axis alignment of items in each line
 * @param verticalArrangement The vertical arrangement of the flows row-lines
 *
 */
@Composable
fun FlowRow(
    modifier: ModifierStackBuilder,
    horizontalLineArrangement: Arrangement.Horizontal,
    verticalLineAlignment: Alignment.Vertical,
    verticalArrangement: Arrangement.Vertical,
    maxRowLines: Int = Int.MAX_VALUE,
    maxItemsInEachRow: Int = Int.MIN_VALUE,
    content: @Composable FlowRowScope.() -> Unit,
) {
    Layout(
        modifier,
        content = { FlowRowScopeImpl.content() },
        measurePolicy = FlowMeasurePolicy(
            RowMeasurePolicy(horizontalLineArrangement, verticalLineAlignment),
            verticalArrangement,
            Arrangement.Start,
            maxRowLines,
            maxItemsInEachRow,
        )
    )
}

/**
 * [FlowColumn] is a variation of the [Column], that when it runs out of space for items in the column-line,
 * moves to the next column-line to the right of the current, until the items run out.
 *
 * The fill direction of items in a column is top to bottom.
 *
 * @param modifier The modifier applied to the FlowRow
 * @param verticalLineArrangement The vertical arrangement of items in each column-line
 * @param horizontalLineAlignment The horizontal cross axis alignment of items in each line
 * @param horizontalArrangement The vertical arrangement of the flows column-lines
 *
 */
@Composable
fun FlowColumn(
    modifier: ModifierStackBuilder,
    verticalLineArrangement: Arrangement.Vertical,
    horizontalLineAlignment: Alignment.Horizontal,
    horizontalArrangement: Arrangement.Horizontal,
    maxColumns: Int = Int.MAX_VALUE,
    maxItemsPerColumn: Int = Int.MIN_VALUE,
    content: @Composable FlowColumnScope.() -> Unit,
) {
    Layout(
        modifier,
        content = { FlowColumnScopeImpl.content() },
        measurePolicy = FlowMeasurePolicy(
            ColumnMeasurePolicy(verticalLineArrangement, horizontalLineAlignment),
            Arrangement.Top,
            horizontalArrangement,
            maxColumns,
            maxItemsPerColumn,
        )
    )
}

interface FlowRowScope: FlowScope, RowScope

interface FlowColumnScope: FlowScope, ColumnScope

interface FlowScope {

    /**
     *
     */
    fun ModifierStackBuilder.fillCrossAxis(weight: Float, fill: Boolean = true): ModifierStackBuilder

}

internal object FlowScopeImpl : FlowScope {

    override fun ModifierStackBuilder.fillCrossAxis(
        weight: Float,
        fill: Boolean,
    ): ModifierStackBuilder {
        TODO("Not yet implemented")
    }

}

internal object FlowRowScopeImpl : FlowRowScope, FlowScope by FlowScopeImpl, RowScope by RowScopeImpl

internal object FlowColumnScopeImpl : FlowColumnScope, FlowScope by FlowScopeImpl, ColumnScope by ColumnScopeImpl

internal class FlowMeasurePolicy(
    val lineMeasurePolicy: LayoutAxisMeasurePolicy,
    val verticalArrangement: Arrangement.Vertical,
    val horizontalArrangement: Arrangement.Horizontal,
    val maxLines: Int,
    val maxMainAxisItems: Int,
) : MeasurePolicy {

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): Measurements {
        if (maxLines == 0 || maxMainAxisItems == 0 || measurables.isEmpty()) {
            return layout(Dp.Zero, Dp.Zero) {}
        }

        return splitItems(
            measurables,
            UndirectedConstraints(
                constraints,
                lineMeasurePolicy.orientation,
            )
        )

    }

    fun MeasureScope.splitItems(
        measurables: List<Measurable>,
        constraints: UndirectedConstraints,
    ): Measurements {
        val remainingSpace = Dp.Zero
        val remainingSpaceCrossAxis = Dp.Zero

        val accumulator = FlowLineAccumulator(
            remainingSpace,
            remainingSpaceCrossAxis
        )

        val mainAxisMin = constraints.mainAxisMin
        val mainAxisMax = constraints.mainAxisMax
        val crossAxisMax = constraints.crossAxisMax

        val subsetConstraints = UndirectedConstraints(Dp.Zero, mainAxisMax, Dp.Zero, crossAxisMax)
        val measureConstraints = subsetConstraints.resolve(lineMeasurePolicy.orientation)

        for (measurable in measurables) {
            var cachedPlaceable: Placeable? = null
            val (itemMainAxisSize, itemCrossAxisSize) = measurable.let {
                val scopeData = measurable.scopeData as? RowColumnScopeData

                if (scopeData == null || scopeData.weight == 0f) {
                    cachedPlaceable = it.measure(measureConstraints)

                    with(lineMeasurePolicy) {
                        cachedPlaceable.mainAxisSize() to cachedPlaceable.crossAxisSize()
                    }
                } else {
                    val mainAxis =
                        it.mainAxisIntrinsicSize(lineMeasurePolicy.orientation, IntrinsicSize.Min, Dp.Infinity)
                    val crossAxis =
                        it.crossAxisIntrinsicSize(lineMeasurePolicy.orientation, IntrinsicSize.Min, mainAxis)

                    mainAxis to crossAxis
                }
            }

            if (!accumulator.tryAppend(measurable, cachedPlaceable, itemMainAxisSize, itemCrossAxisSize)) {
                break
            }
        }

        val cachedPlaceables: Array<Placeable?> = Array(accumulator.measurables.size) { accumulator.cachedPlaceables[it] }
        val crossAxisFinalSizes: Array<Dp> = Array(accumulator.lineBreakIndices.size) { Dp.Zero }

        val items = mutableListOf<Measurements>()
        var totalMainAxisSize = Dp.Zero
        var totalCrossAxisSize = Dp.Zero
        var startIndex = 0
        accumulator.lineBreakIndices.forEachIndexed { lineIndex, breakIndex ->
            var crossAxisSize = accumulator.crossAxisSizes[lineIndex]

            val measurements = lineMeasurePolicy.measure(
                this,
                accumulator.measurables.subList(startIndex, breakIndex),
                placeables = cachedPlaceables.copyOfRange(startIndex, breakIndex),
                mainAxisMin = accumulator.mainAxisSize,
                crossAxisMin = subsetConstraints.crossAxisMin,
                mainAxisMax = subsetConstraints.crossAxisMax,
                crossAxisMax = crossAxisSize,
            )

            val mainAxisSize: Dp
            if (lineMeasurePolicy.orientation == LayoutOrientation.Horizontal) {
                mainAxisSize = measurements.width
                crossAxisSize = measurements.height
            } else {
                mainAxisSize = measurements.height
                crossAxisSize = measurements.width
            }

            totalMainAxisSize = max(totalMainAxisSize, mainAxisSize)
            totalCrossAxisSize += crossAxisSize
            crossAxisFinalSizes[lineIndex] = crossAxisSize
            items.add(measurements)
            startIndex = breakIndex
        }

        val finalMainAxisSize: Dp = totalMainAxisSize.coerceIn(constraints.mainAxisMin, constraints.mainAxisMax)
        val finalCrossAxisSize: Dp = totalCrossAxisSize.coerceIn(constraints.crossAxisMin, constraints.crossAxisMax)

        val crossAxisPositions = if (lineMeasurePolicy.orientation == LayoutOrientation.Horizontal) {
            horizontalArrangement.arrange(totalCrossAxisSize, layoutDirection, crossAxisFinalSizes)
        } else {
            verticalArrangement.arrange(totalCrossAxisSize, crossAxisFinalSizes)
        }

        val layoutWidth: Dp
        val layoutHeight: Dp
        if (lineMeasurePolicy.orientation == LayoutOrientation.Horizontal) {
            layoutWidth = finalMainAxisSize
            layoutHeight = finalCrossAxisSize
        } else {
            layoutHeight = finalMainAxisSize
            layoutWidth = finalCrossAxisSize
        }

        return layout(layoutWidth, layoutHeight) {
            for ((i, measurements) in items.withIndex()) {
                offset = if (lineMeasurePolicy.orientation == LayoutOrientation.Horizontal) {
                    Offset(Dp.Zero, crossAxisPositions[i])
                } else {
                    Offset(crossAxisPositions[i], Dp.Zero)
                }
                measurements.placeChildren(this)
            }
        }

    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Dp): Dp {
        return if (lineMeasurePolicy.orientation == LayoutOrientation.Horizontal) {
            intrinsicMainAxisSizeMin(measurables, height)
        } else {
            intrinsicCrossAxisSize(measurables, height)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Dp): Dp {
        return if (lineMeasurePolicy.orientation == LayoutOrientation.Horizontal) {
            intrinsicCrossAxisSize(measurables, width)
        } else {
            intrinsicMainAxisSizeMin(measurables, width)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Dp): Dp {
        return if (lineMeasurePolicy.orientation == LayoutOrientation.Horizontal) {
            intrinsicMainAxisSizeMax(measurables, height)
        } else {
            intrinsicCrossAxisSize(measurables, height)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Dp): Dp {
        return if (lineMeasurePolicy.orientation == LayoutOrientation.Horizontal) {
            intrinsicCrossAxisSize(measurables, width)
        } else {
            intrinsicMainAxisSizeMax(measurables, width)
        }
    }

    private fun intrinsicMainAxisSizeMin(
        items: List<IntrinsicMeasurable>,
        availableCrossAxisSize: Dp,
    ): Dp = intrinsicMainAxisSizeMin(
        items,
        { _, size -> mainAxisIntrinsicSize(lineMeasurePolicy.orientation, IntrinsicSize.Min, size) },
        { _, size -> crossAxisIntrinsicSize(lineMeasurePolicy.orientation, IntrinsicSize.Min, size) },
        availableCrossAxisSize,
    )

    /**
     *
     */
    private fun intrinsicMainAxisSizeMin(
        items: List<IntrinsicMeasurable>,
        itemMainAxisSize: IntrinsicMeasurable.(index: Int, crossAxisSize: Dp) -> Dp,
        itemCrossAxisSize: IntrinsicMeasurable.(index: Int, mainAxisSize: Dp) -> Dp,
        availableCrossAxisSize: Dp,
    ): Dp {
        if (items.isEmpty()) {
            return Dp.Zero
        }

        val mainAxisSizes = Array(items.size) { Dp.Zero }
        val crossAxisSizes = Array(items.size) { Dp.Zero }

        for (i in items.indices) {
            val item = items[i]
            val mainAxisSize = item.itemMainAxisSize(i, availableCrossAxisSize)
            mainAxisSizes[i] = mainAxisSize
            crossAxisSizes[i] = item.itemCrossAxisSize(i, mainAxisSize)
        }

        // Main Axis size range that can fit all items
        val maxMainAxisSize = mainAxisSizes.reduce { acc, dp -> acc + dp }
        val minMainAxisSize = mainAxisSizes.maxOf { it }

        var currentMainAxisSize = maxMainAxisSize
        var currentCrossAxisSize = crossAxisSizes.maxOf { it }

        var low = minMainAxisSize
        var high = maxMainAxisSize
        while (low <= high) {
            if (currentCrossAxisSize == availableCrossAxisSize) {
                return currentMainAxisSize
            }
            val mid = (low + high) / 2
            currentMainAxisSize = mid

            val updatedCrossAxis = intrinsicCrossAxisSize(
                items,
                { index, _ -> mainAxisSizes[index] },
                { index, _ -> crossAxisSizes[index] },
                currentMainAxisSize,
            )

            currentCrossAxisSize = updatedCrossAxis.first
            val itemsShown = updatedCrossAxis.second

            if (currentCrossAxisSize > availableCrossAxisSize || itemsShown < maxMainAxisItems) {
                low = mid + 1.dp
                if (low > high) {
                    return low
                }
            } else if (currentCrossAxisSize < availableCrossAxisSize) {
                high = mid - 1.dp
            } else {
                return currentMainAxisSize
            }
        }
        return currentMainAxisSize
    }

    private fun intrinsicMainAxisSizeMax(
        items: List<IntrinsicMeasurable>,
        availableCrossAxisSize: Dp,
    ): Dp = intrinsicMainAxisSizeMax(
        items,
        {_, size -> mainAxisIntrinsicSize(lineMeasurePolicy.orientation, IntrinsicSize.Max, size) },
        availableCrossAxisSize
    )

    private fun intrinsicMainAxisSizeMax(
        items: List<IntrinsicMeasurable>,
        itemMainAxisSize: IntrinsicMeasurable.(index: Int, crossAxisSize: Dp) -> Dp,
        availableCrossAxisSize: Dp,
    ): Dp {
        var maxMainAxisSize = Dp.Zero
        var lineMainAxisSize = Dp.Zero
        var lastLineBreak = 0
        for ((index, item) in items.withIndex()) {
            lineMainAxisSize += item.itemMainAxisSize(index, availableCrossAxisSize)
            if (index + 1 - lastLineBreak >= maxMainAxisItems || index + 1 >= items.size) {
                lastLineBreak = index
                maxMainAxisSize = max(maxMainAxisSize, lineMainAxisSize)
            }
        }
        return maxMainAxisSize
    }

    private fun intrinsicCrossAxisSize(
        items: List<IntrinsicMeasurable>,
        availableMainAxisSize: Dp,
    ): Dp = intrinsicCrossAxisSize(
            items,
            { _, size -> crossAxisIntrinsicSize(lineMeasurePolicy.orientation, IntrinsicSize.Min, size) },
            { _, size -> crossAxisIntrinsicSize(lineMeasurePolicy.orientation, IntrinsicSize.Min, size) },
            availableMainAxisSize,
        ).first

    private fun intrinsicCrossAxisSize(
        items: List<IntrinsicMeasurable>,
        itemMainAxisSize: IntrinsicMeasurable.(index: Int, crossAxisSize: Dp) -> Dp,
        itemCrossAxisSize: IntrinsicMeasurable.(index: Int, mainAxisSize: Dp) -> Dp,
        availableMainAxisSize: Dp,
    ) : Pair<Dp, Int> {
        if (items.isEmpty()) {
            return Dp.Zero to 0
        }

        val accumulator = FlowLineAccumulator(
            availableMainAxisSize,
            Dp.Infinity,
            maxLines,
            maxMainAxisItems
        )

        var shownItems = 0
        for (i in items.indices) {
            val item = items[i]
            val itemCrossAxisSize = item.itemCrossAxisSize(i, availableMainAxisSize)
            val itemMainAxisSize = item.itemMainAxisSize(i, itemCrossAxisSize)

            if (!accumulator.tryAppend(itemMainAxisSize, itemCrossAxisSize)) {
                break
            }

            ++shownItems
        }

        return accumulator.totalCrossAxisSize to shownItems
    }

}

internal class FlowLineAccumulator(
    private val availableMainAxisSpace: Dp,
    private val availableCrossAxisSpace: Dp,
    val maxLines: Int = Int.MAX_VALUE,
    val maxMainAxisItems: Int = Int.MAX_VALUE,
) {
    val measurables: List<Measurable> field = mutableListOf()
    val cachedPlaceables: Map<Int, Placeable?> field = mutableMapOf()
    val lineBreakIndices: List<Int> field = mutableListOf()

    val crossAxisSizes: List<Dp> field = mutableListOf<Dp>()
    var totalCrossAxisSize = Dp.Zero
        private set

    var mainAxisSize = Dp.Zero
        private set

    var itemIndex = 0
    var lineIndex = 0
    var currentLine: FlowLine = FlowLine(Dp.Zero, Dp.Zero)
        private set
    val lines: List<FlowLine> field = mutableListOf(currentLine)

    fun breakLine(): Boolean {
        if (lineIndex >= maxLines) {
            // TODO: end accumulator
            return false
        }
        lineBreakIndices.add(lineIndex++)
        totalCrossAxisSize += currentLine.crossAxisSize
        crossAxisSizes.add(currentLine.crossAxisSize)
        mainAxisSize += max(currentLine.mainAxisSize, mainAxisSize)
        currentLine = FlowLine(Dp.Zero, Dp.Zero)
        lines.add(currentLine)
        return true
    }

    fun tryAppend(
        itemMainAxisSize: Dp,
        itemCrossAxisSize: Dp,
    ): Boolean {
        val overflowCrossAxis = availableCrossAxisSpace - totalCrossAxisSize - itemCrossAxisSize <= Dp.Zero
        if (overflowCrossAxis) {
            // TODO: end accumulation
            return false
        }

        val overflowMainAxis = when {
            availableMainAxisSpace - currentLine.mainAxisSize - itemMainAxisSize <= Dp.Zero -> true
            currentLine.items + 1 > maxMainAxisItems -> true
            else -> false
        }
        if (overflowMainAxis) {
            if (!breakLine()) {
                return false
            }
            return tryAppend(itemMainAxisSize, itemCrossAxisSize)
        }

        ++itemIndex
        currentLine.append(itemMainAxisSize, itemCrossAxisSize)
        return true
    }

    fun tryAppend(
        measurable: Measurable,
        placeable: Placeable?,
        itemMainAxisSize: Dp,
        itemCrossAxisSize: Dp,
    ): Boolean {
        if (tryAppend(itemMainAxisSize, itemCrossAxisSize)) {
            cachedPlaceables[itemIndex] = placeable
            measurables.add(measurable)
            return true
        }
        return false
    }

    class FlowLine(
        mainAxisSize: Dp,
        crossAxisSize: Dp,
    ) {
        var mainAxisSize: Dp = mainAxisSize
            private set
        var crossAxisSize: Dp = crossAxisSize
            private set
        var items: Int = 0
            private set

        internal fun append(
            itemMainAxisSize: Dp,
            itemCrossAxisSize: Dp,
        ) {
            ++items
            mainAxisSize += itemMainAxisSize
            crossAxisSize = max(crossAxisSize, itemCrossAxisSize)
        }

    }
}
