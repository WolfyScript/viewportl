package com.wolfyscript.viewportl.foundation

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.ui.MeasurePolicy
import com.wolfyscript.viewportl.ui.layout.*
import com.wolfyscript.viewportl.ui.modifier.LayoutWeightModifier
import com.wolfyscript.viewportl.ui.modifier.Modifier
import com.wolfyscript.viewportl.ui.modifier.ModifierStackBuilder

@Composable
fun Row(
    modifier: ModifierStackBuilder = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit,
) {
    Layout(
        modifier,
        content = { RowScopeImpl.content() },
        measurePolicy = RowMeasurePolicy(horizontalArrangement, verticalAlignment)
    )
}

interface RowScope {

    /**
     *
     */
    fun ModifierStackBuilder.weight(weight: Float, fill: Boolean = true): ModifierStackBuilder

}

internal object RowScopeImpl : RowScope {

    override fun ModifierStackBuilder.weight(
        weight: Float,
        fill: Boolean,
    ): ModifierStackBuilder {
        require(weight > 0f) { "invalid weight: $weight: weight must be > 0" }
        return push(LayoutWeightModifier(weight, fill))
    }

}

internal class RowMeasurePolicy(
    val horizontalArrangement: Arrangement.Horizontal,
    val verticalAlignment: Alignment.Vertical,
) : MeasurePolicy, LayoutAxisMeasurePolicy {

    override val orientation: LayoutOrientation = LayoutOrientation.Horizontal

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): Measurements = measure(
        this,
        measurables,
        mainAxisMin = constraints.minWidth,
        crossAxisMin = constraints.minHeight,
        mainAxisMax = constraints.maxWidth,
        crossAxisMax = constraints.maxHeight
    )

    override fun Placeable.mainAxisSize(): Dp = width

    override fun Placeable.crossAxisSize(): Dp = height

    override fun createConstraints(
        mainAxisMin: Dp,
        mainAxisMax: Dp,
        crossAxisMin: Dp,
        crossAxisMax: Dp,
    ): Constraints = Constraints(
        mainAxisMin,
        mainAxisMax,
        crossAxisMin,
        crossAxisMax
    )

    override fun arrangeMainAxis(
        measureScope: MeasureScope,
        mainAxisSpace: Dp,
        mainAxisChildSizes: Array<Dp>,
    ): Array<Dp> = horizontalArrangement.arrange(
        mainAxisSpace,
        measureScope.layoutDirection,
        mainAxisChildSizes
    )

    override fun placeItems(
        measureScope: MeasureScope,
        placeables: Array<Placeable?>,
        mainAxisPositions: Array<Dp>,
        mainAxisSpace: Dp,
        crossAxisSpace: Dp,
    ): Measurements = with(measureScope) {
        layout(mainAxisSpace, crossAxisSpace) {
            placeables.forEachIndexed { index, placeable ->
                placeable?.place(
                    mainAxisPositions[index],
                    verticalAlignment.align(placeable.height, crossAxisSpace)
                )
            }
        }
    }

}
