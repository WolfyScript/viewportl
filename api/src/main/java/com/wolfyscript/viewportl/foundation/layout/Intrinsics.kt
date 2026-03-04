package com.wolfyscript.viewportl.foundation.layout

import com.wolfyscript.viewportl.ui.IntrinsicSize
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.layout.Offset
import com.wolfyscript.viewportl.ui.layout.constrain
import com.wolfyscript.viewportl.ui.modifier.*

fun ModifierStackBuilder.width(intrinsicSize: IntrinsicSize) =
    push(IntrinsicWidthModifier(intrinsicSize, true))

fun ModifierStackBuilder.height(intrinsicSize: IntrinsicSize) =
    push(IntrinsicHeightModifier(intrinsicSize, true))

fun ModifierStackBuilder.requiredWidth(intrinsicSize: IntrinsicSize) =
    push(IntrinsicWidthModifier(intrinsicSize, false))

fun ModifierStackBuilder.requiredHeight(intrinsicSize: IntrinsicSize) =
    push(IntrinsicHeightModifier(intrinsicSize, false))

private class IntrinsicWidthModifier(
    val width: IntrinsicSize,
    val enforceIncoming: Boolean,
) : ModifierData<IntrinsicsWidthModifierNode> {

    override fun create(): IntrinsicsWidthModifierNode = IntrinsicsWidthModifierNode(width, enforceIncoming)

    override fun update(node: IntrinsicsWidthModifierNode) {
        node.width = this.width
        node.enforceIncoming = this.enforceIncoming
    }

}

private class IntrinsicsWidthModifierNode(
    var width: IntrinsicSize,
    var enforceIncoming: Boolean,
) : LayoutModifierNode, ModifierNode {

    override fun onAttach() {}

    override fun onDetach() {}

    override fun LayoutModifyScope.modify(constraints: Constraints): LayoutModification {
        val intrinsicWidth = if (width == IntrinsicSize.Min) {
            minIntrinsicWidth(constraints.maxHeight)
        } else {
            maxIntrinsicWidth(constraints.maxHeight)
        }.coerceAtLeast(Dp.Zero)

        val intrinsicConstraints = Constraints(minWidth = intrinsicWidth, maxWidth = intrinsicWidth)

        return modifyLayout(
            if (enforceIncoming) {
                constraints.constrain(intrinsicConstraints)
            } else {
                intrinsicConstraints
            }
        ) {
            modifyMeasure(it.measuredWidth, it.measuredHeight, Offset.Zero)
        }
    }

    override fun IntrinsicModifyIncomingScope.modifyMinIntrinsicWidth(height: Dp): Dp =
        if (width == IntrinsicSize.Min) {
            childIntrinsicMinWidth(height)
        } else {
            childIntrinsicMaxWidth(height)
        }

    override fun IntrinsicModifyIncomingScope.modifyMaxIntrinsicWidth(height: Dp): Dp =
        if (width == IntrinsicSize.Min) {
            childIntrinsicMinWidth(height)
        } else {
            childIntrinsicMaxWidth(height)
        }

}

private class IntrinsicHeightModifier(
    val height: IntrinsicSize,
    val enforceIncoming: Boolean,
) : ModifierData<IntrinsicsHeightModifierNode> {

    override fun create(): IntrinsicsHeightModifierNode = IntrinsicsHeightModifierNode(height, enforceIncoming)

    override fun update(node: IntrinsicsHeightModifierNode) {
        node.height = this.height
        node.enforceIncoming = this.enforceIncoming
    }

}

private class IntrinsicsHeightModifierNode(
    var height: IntrinsicSize,
    var enforceIncoming: Boolean,
) : LayoutModifierNode, ModifierNode {

    override fun onAttach() {}

    override fun onDetach() {}

    override fun LayoutModifyScope.modify(constraints: Constraints): LayoutModification {
        val intrinsicHeight = if (height == IntrinsicSize.Min) {
            minIntrinsicHeight(constraints.maxWidth)
        } else {
            maxIntrinsicHeight(constraints.maxWidth)
        }.coerceAtLeast(Dp.Zero)

        val intrinsicConstraints = Constraints(minHeight = intrinsicHeight, maxHeight = intrinsicHeight)

        return modifyLayout(
            if (enforceIncoming) {
                constraints.constrain(intrinsicConstraints)
            } else {
                intrinsicConstraints
            }
        ) {
            modifyMeasure(it.measuredWidth, it.measuredHeight, Offset.Zero)
        }
    }

    override fun IntrinsicModifyIncomingScope.modifyMinIntrinsicHeight(width: Dp): Dp =
        if (this@IntrinsicsHeightModifierNode.height == IntrinsicSize.Min) {
            childIntrinsicMinHeight(width)
        } else {
            childIntrinsicMaxHeight(width)
        }

    override fun IntrinsicModifyIncomingScope.modifyMaxIntrinsicHeight(width: Dp): Dp =
        if (this@IntrinsicsHeightModifierNode.height == IntrinsicSize.Min) {
            childIntrinsicMinHeight(width)
        } else {
            childIntrinsicMaxHeight(width)
        }

}
