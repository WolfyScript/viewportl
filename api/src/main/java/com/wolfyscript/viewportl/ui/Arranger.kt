package com.wolfyscript.viewportl.ui

import com.wolfyscript.viewportl.ui.layout.*
import com.wolfyscript.viewportl.ui.modifier.*

class NodeArrangerImpl(override val node: LayoutNode) : NodeArranger {

    private val layoutDirection: LayoutDirection = LayoutDirection.LtR
    override var width: Dp = Dp.Zero
        private set
    override var height: Dp = Dp.Zero
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
    override val scopeData: Any?
        get() {
            var scopeData: Any? = null
            node.modifierStack.forEachOfType(ScopeDataModifierNode::class) {
                scopeData = it.modifyScopeData(scopeData)
            }
            return scopeData
        }
    val childMeasurables
        get() = buildList<Measurable> {
            node.forEachChild { child -> add(child.arranger) }
        }


    private var incomingConstraints: Constraints = Constraints()

    private var requiresRemeasure = false
    private var requiresReplacement = false

    override fun measure(constraints: Constraints): Placeable {
        val modification = node.modifierStack.modifyMeasure(constraints)
        return performMeasurement(constraints) {
            val measureScope = SimpleMeasureScope(layoutDirection)
            with(node.measurePolicy) {
                measureScope.measure(childMeasurables, modification.constraints)
            }
        }
    }

    private fun performMeasurement(constraints: Constraints, fn: () -> Measurements): Placeable {
        incomingConstraints = constraints
        measurements = fn()
        return this
    }

    override fun placeAt(
        x: Dp,
        y: Dp,
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

    private val modifyMinIntrinsicWidth: LayoutModifierNode.(IntrinsicModifyIncomingScope, Dp) -> IntrinsicIncomingModification =
        { scope, cAS -> scope.modifyMinIntrinsicWidth(cAS) }
    private val modifyMaxIntrinsicWidth: LayoutModifierNode.(IntrinsicModifyIncomingScope, Dp) -> IntrinsicIncomingModification =
        { scope, cAS -> scope.modifyMaxIntrinsicWidth(cAS) }
    private val modifyMinIntrinsicHeight: LayoutModifierNode.(IntrinsicModifyIncomingScope, Dp) -> IntrinsicIncomingModification =
        { scope, cAS -> scope.modifyMinIntrinsicHeight(cAS) }
    private val modifyMaxIntrinsicHeight: LayoutModifierNode.(IntrinsicModifyIncomingScope, Dp) -> IntrinsicIncomingModification =
        { scope, cAS -> scope.modifyMaxIntrinsicHeight(cAS) }

    override fun minIntrinsicWidth(height: Dp): Dp {
        return node.modifierStack.modifyIntrinsic(
            IntrinsicSize.Min,
            layoutDirection,
            height,
            modifyMinIntrinsicWidth,
            modifyMaxIntrinsicWidth,
        ) { crossAxisSize ->
            with(node.measurePolicy) {
                minIntrinsicWidth(childMeasurables, crossAxisSize)
            }
        }
    }

    override fun maxIntrinsicWidth(height: Dp): Dp {
        return node.modifierStack.modifyIntrinsic(
            IntrinsicSize.Max,
            layoutDirection,
            height,
            modifyMinIntrinsicWidth,
            modifyMaxIntrinsicWidth,
        ) { crossAxisSize ->
            with(node.measurePolicy) {
                maxIntrinsicWidth(childMeasurables, crossAxisSize)
            }
        }
    }

    override fun minIntrinsicHeight(width: Dp): Dp {
        return node.modifierStack.modifyIntrinsic(
            IntrinsicSize.Min,
            layoutDirection,
            width,
            modifyMinIntrinsicHeight,
            modifyMaxIntrinsicHeight,
        ) { crossAxisSize ->
            with(node.measurePolicy) {
                minIntrinsicHeight(childMeasurables, crossAxisSize)
            }
        }
    }

    override fun maxIntrinsicHeight(width: Dp): Dp {
        return node.modifierStack.modifyIntrinsic(
            IntrinsicSize.Max,
            layoutDirection,
            width,
            modifyMinIntrinsicHeight,
            modifyMaxIntrinsicHeight,
        ) { crossAxisSize ->
            with(node.measurePolicy) {
                maxIntrinsicHeight(childMeasurables, crossAxisSize)
            }
        }
    }

}