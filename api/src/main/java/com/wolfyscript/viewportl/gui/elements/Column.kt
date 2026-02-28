package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.ui.MeasurePolicy
import com.wolfyscript.viewportl.ui.layout.Alignment
import com.wolfyscript.viewportl.ui.layout.Arrangement
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Placeable
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.layout.LayoutAxisMeasurePolicy
import com.wolfyscript.viewportl.ui.layout.Measurable
import com.wolfyscript.viewportl.ui.layout.MeasureScope
import com.wolfyscript.viewportl.ui.layout.Measurements
import com.wolfyscript.viewportl.ui.modifier.LayoutWeightModifier
import com.wolfyscript.viewportl.ui.modifier.Modifier
import com.wolfyscript.viewportl.ui.modifier.ModifierStackBuilder

@Composable
fun Column(
    modifier: ModifierStackBuilder = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Layout(
        modifier,
        content = {
            ColumnScopeImpl.content()
        },
        measurePolicy = ColumnMeasurePolicy(verticalArrangement, horizontalAlignment)
    )
}

interface ColumnScope {

    /**
     *
     */
    fun ModifierStackBuilder.weight(weight: Float, fill: Boolean = true): ModifierStackBuilder

}

internal object ColumnScopeImpl : ColumnScope {

    override fun ModifierStackBuilder.weight(
        weight: Float,
        fill: Boolean,
    ): ModifierStackBuilder {
        require(weight > 0f) { "invalid weight: $weight: weight must be > 0" }
        return push(LayoutWeightModifier(weight, fill))
    }

}

internal class ColumnMeasurePolicy(
    val verticalArrangement: Arrangement.Vertical,
    val horizontalAlignment: Alignment.Horizontal,
) : MeasurePolicy, LayoutAxisMeasurePolicy {

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): Measurements = measure(
        this,
        measurables,
        constraints.minHeight,
        constraints.minWidth,
        constraints.maxHeight,
        constraints.maxWidth
    )

    override fun Placeable.mainAxisSize(): Dp = height

    override fun Placeable.crossAxisSize(): Dp = width

    override fun createConstraints(
        mainAxisMin: Dp,
        mainAxisMax: Dp,
        crossAxisMin: Dp,
        crossAxisMax: Dp,
    ): Constraints = Constraints(
        crossAxisMin,
        crossAxisMax,
        mainAxisMin,
        mainAxisMax
    )

    override fun arrangeMainAxis(
        measureScope: MeasureScope,
        mainAxisSpace: Dp,
        mainAxisChildSizes: Array<Dp>,
    ): Array<Dp> = verticalArrangement.arrange(
        mainAxisSpace,
        mainAxisChildSizes
    )

    override fun placeItems(
        measureScope: MeasureScope,
        placeables: Array<Placeable?>,
        mainAxisPositions: Array<Dp>,
        mainAxisSpace: Dp,
        crossAxisSpace: Dp,
    ): Measurements = with(measureScope) {
        layout(crossAxisSpace, mainAxisSpace) {
            placeables.forEachIndexed { index, placeable ->
                placeable?.placeAt(
                    horizontalAlignment.align(placeable.width, crossAxisSpace, layoutDirection),
                    mainAxisPositions[index]
                )
            }
        }
    }
}
