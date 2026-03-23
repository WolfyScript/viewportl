package com.wolfyscript.viewportl.ui

import com.wolfyscript.viewportl.ui.layout.*
import com.wolfyscript.viewportl.ui.modifier.IntrinsicMeasureBlock
import com.wolfyscript.viewportl.ui.modifier.ScopeDataModifierNode
import com.wolfyscript.viewportl.ui.modifier.SimpleMeasureModification

internal class NodeArrangerImpl(override val node: LayoutNode) : NodeArranger {

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
        return measureSelf(constraints) {
            val measureScope = SimpleMeasureScope(layoutDirection)
            with(node.measurePolicy) {
                measureScope.measure(childMeasurables, modification.constraints)
            }
        }
    }

    private fun measureSelf(constraints: Constraints, fn: () -> Measurements): Placeable {
        incomingConstraints = constraints
        measurements = fn()
        return this
    }

    override fun placeNoOffset(
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

    internal val measureIntrinsics =
        IntrinsicMeasureBlock { dimension, size, crossAxisSize ->
            val scope = SimpleMeasureScope(layoutDirection)
            with(node.measurePolicy) {
                if (dimension == IntrinsicDimension.Width) {
                    if (size == IntrinsicSize.Min) {
                        scope.minIntrinsicWidth(childMeasurables, crossAxisSize)
                    } else {
                        scope.maxIntrinsicWidth(childMeasurables, crossAxisSize)
                    }
                } else {
                    if (size == IntrinsicSize.Min) {
                        scope.minIntrinsicHeight(childMeasurables, crossAxisSize)
                    } else {
                        scope.maxIntrinsicHeight(childMeasurables, crossAxisSize)
                    }
                }
            }
        }

    override fun minIntrinsicWidth(height: Dp): Dp {
        return node.modifierStack.modifyIntrinsic(
            IntrinsicSize.Min,
            IntrinsicDimension.Width,
            height,
            measureIntrinsics
        )
    }

    override fun maxIntrinsicWidth(height: Dp): Dp {
        return node.modifierStack.modifyIntrinsic(
            IntrinsicSize.Max,
            IntrinsicDimension.Width,
            height,
            measureIntrinsics
        )
    }

    override fun minIntrinsicHeight(width: Dp): Dp {
        return node.modifierStack.modifyIntrinsic(
            IntrinsicSize.Min,
            IntrinsicDimension.Height,
            width,
            measureIntrinsics
        )
    }

    override fun maxIntrinsicHeight(width: Dp): Dp {
        return node.modifierStack.modifyIntrinsic(
            IntrinsicSize.Max,
            IntrinsicDimension.Height,
            width,
            measureIntrinsics
        )
    }

}