package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.gui.compose.layout.MeasureScope
import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Arranger
import com.wolfyscript.viewportl.gui.compose.layout.Measurable
import com.wolfyscript.viewportl.gui.compose.layout.Measurements
import com.wolfyscript.viewportl.gui.compose.layout.Placeable
import com.wolfyscript.viewportl.gui.compose.layout.PlacementScope
import com.wolfyscript.viewportl.gui.compose.layout.Position
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.layout.dp
import com.wolfyscript.viewportl.gui.compose.layout.slots

class ArrangerImpl(override val node: Node) : Arranger {

    override val width: Size
        get() {
            return measurements?.width ?: Size(0.slots, 0.dp)
        }
    override val height: Size
        get() {
            return measurements?.height ?: Size(0.slots, 0.dp)
        }

    private var position: Position? = null
    private var measurements: Measurements? = null

    private var previousConstraints: Constraints? = null

    override fun measure(constraints: Constraints): Placeable {
        val childMeasurables = buildList<Measurable> {
            node.forEachChild { child -> add(child.arranger) }
        }

        // TODO
        val measureScope = object : MeasureScope {
            override fun layout(
                width: Size,
                height: Size,
                placement: PlacementScope.() -> Unit,
            ): Measurements {
                TODO("Not yet implemented")
            }
        }

        node.measurePolicy?.let { measurePolicy ->
            measurements = with(measurePolicy) {
                measureScope.measure(childMeasurables, constraints)
            }
        }

        return this
    }

    override fun placeAt(
        x: Size,
        y: Size,
    ) {
        this.position = Position(x, y)
        afterPlace()
    }

    private fun afterPlace() {
        measurements?.placeChildren?.let { place ->
            // TODO
            object : PlacementScope {}.place()
        }
    }

    override fun remeasure(constraints: Constraints): Boolean {
        if (previousConstraints == null || previousConstraints != constraints) {
            previousConstraints = constraints
            val previousMeasurements = measurements
            measure(constraints)
            return previousMeasurements != measurements
        }
        return false
    }

}