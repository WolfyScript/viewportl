package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleMeasureModification
import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.gui.compose.layout.*

class NodeArrangerImpl(override val node: Node) : NodeArranger {

    override val width: Size
        get() {
            return measurements?.width ?: Size.Zero
        }
    override val height: Size
        get() {
            return measurements?.height ?: Size.Zero
        }

    override var position: Offset = Offset.Zero
    internal var measurements: Measurements? = null

    private var previousConstraints: Constraints? = null

    private var requiresRemeasure = false
    private var requiresReplacement = false

    override fun measure(constraints: Constraints): Placeable {
        val modification = node.modifierStack.modifyMeasure(constraints)

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
        val modification = node.modifierStack.modifyLayout(SimpleMeasureModification(width, height, Offset.Zero))

        this.position = Offset(x, y) + modification.offset
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
        if (measurements != null) {
            val scope = SimplePlacementScope()
            measurements?.placeChildren(scope)
        }
    }

}