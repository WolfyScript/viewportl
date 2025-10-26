package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.gui.compose.layout.*

class NodeArrangerImpl(override val node: Node) : NodeArranger {

    override val width: Size
        get() {
            return measurements?.width ?: Size(0.slots, 0.dp)
        }
    override val height: Size
        get() {
            return measurements?.height ?: Size(0.slots, 0.dp)
        }

    internal var offset: Position? = null
    internal var position: Position? = null
    internal var measurements: Measurements? = null

    private var previousConstraints: Constraints? = null

    private var requiresRemeasure = false
    private var requiresReplacement = false

    override fun measure(constraints: Constraints): Placeable {
        val modification = node.modifierStack.modifyLayout(constraints)
        this@NodeArrangerImpl.offset = modification.offset

        val childMeasurables = buildList<Measurable> {
            node.forEachChild { child -> add(child.arranger) }
        }

        return performMeasurement(constraints) {
            val measureScope = SimpleMeasureScope()
            node.measurePolicy?.let { measurePolicy ->
                with(measurePolicy) {
                    measureScope.measure(childMeasurables, modification.constraints)
                }
            } ?: Measurements(0.slotsSize, 0.slotsSize)
        }
    }

    private fun performMeasurement(constraints: Constraints, fn: () -> Measurements): Placeable {
        previousConstraints = constraints
        measurements = fn()
        return this
    }

    override fun placeAt(
        x: Size,
        y: Size,
    ) {
        if (offset != null) {
            this.position = Position(offset!!.x + x, offset!!.y + y)
        } else {
            this.position = Position(x, y)
        }
        afterPlace()
    }

    private fun afterPlace() {
        measurements?.placeChildren?.let { place ->
            SimplePlacementScope().place()
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

    override fun layout() {
        if (measurements == null) {
            val scope = SimplePlacementScope()
            measurements?.placeChildren(scope)
        }
    }

}