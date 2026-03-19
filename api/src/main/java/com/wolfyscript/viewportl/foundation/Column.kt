package com.wolfyscript.viewportl.foundation

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.ui.MeasurePolicy
import com.wolfyscript.viewportl.ui.layout.Alignment
import com.wolfyscript.viewportl.ui.layout.Arrangement
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Placeable
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.layout.IntrinsicMeasurable
import com.wolfyscript.viewportl.ui.layout.IntrinsicMeasureScope
import com.wolfyscript.viewportl.ui.layout.IntrinsicMeasurement
import com.wolfyscript.viewportl.ui.layout.LayoutAxisMeasurePolicy
import com.wolfyscript.viewportl.ui.layout.LayoutOrientation
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

    override val orientation: LayoutOrientation = LayoutOrientation.Vertical

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): Measurements = measure(
        this,
        measurables,
        mainAxisMin = constraints.minHeight,
        crossAxisMin = constraints.minWidth,
        mainAxisMax = constraints.maxHeight,
        crossAxisMax = constraints.maxWidth
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
                placeable?.place(
                    horizontalAlignment.align(placeable.width, crossAxisSpace, layoutDirection),
                    mainAxisPositions[index]
                )
            }
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Dp): Dp {
        return IntrinsicMeasurement.intrinsicCrossAxisSize(
            measurables,
            { maxIntrinsicHeight(it) },
            { minIntrinsicWidth(it) },
            height
        )
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Dp): Dp {
        return IntrinsicMeasurement.intrinsicCrossAxisSize(
            measurables,
            { maxIntrinsicHeight(it) },
            { maxIntrinsicWidth(it) },
            height
        )
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Dp): Dp {
        return IntrinsicMeasurement.intrinsicMainAxisSize(
            measurables,
            { minIntrinsicHeight(it) },
            width
        )
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Dp): Dp {
        return IntrinsicMeasurement.intrinsicMainAxisSize(
            measurables,
            { maxIntrinsicHeight(it) },
            width
        )
    }
}
