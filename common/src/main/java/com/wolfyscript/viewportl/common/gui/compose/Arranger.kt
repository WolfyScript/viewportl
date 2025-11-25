package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleMeasureModification
import com.wolfyscript.viewportl.gui.compose.layout.*

class NodeArrangerImpl(override val node: LayoutNode) : NodeArranger {

    override var width: Size = Size.Zero
        private set
    override var height: Size = Size.Zero
        private set
    override var position: Offset = Offset.Zero
    private var sizeOffset: Offset = Offset.Zero
    internal var measurements: Measurements = Measurements()
        set(value) {
            val previous = field
            field = value

            if (previous != value) {
                onMeasureChange()
            }
        }

    private var incomingConstraints: Constraints = Constraints()

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
        incomingConstraints = constraints
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
        measurements.placeChildren.let { place ->
            SimplePlacementScope().place()
        }
        onLayoutChange()
    }

    override fun remeasure(constraints: Constraints): Boolean {
        if (incomingConstraints != constraints) {
            incomingConstraints = constraints
            val previousMeasurements = measurements
            measure(constraints)
            return previousMeasurements != measurements
        }
        return false
    }

    override fun layout() {
        val scope = SimplePlacementScope()
        measurements.placeChildren(scope)
    }

    fun onLayoutChange() {
        node.modifierStack.onLayoutChange()
    }

    fun onMeasureChange() {
        // TODO: move to a fun together with the same logic for LayoutModifiers
        width = measurements.width.coerceIn(incomingConstraints.minWidth, incomingConstraints.maxWidth)
        height = measurements.height.coerceIn(incomingConstraints.minHeight, incomingConstraints.maxHeight)
        sizeOffset = Offset((width - measurements.width) / 2, (height - measurements.height) / 2)

        node.modifierStack.onMeasureChange()
    }

}