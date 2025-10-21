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

class ArrangerImpl(override val node: Node) : Arranger {

    private var position: Position? = null
    private var measurements: Measurements? = null

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

}