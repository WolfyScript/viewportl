package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Dp

/**
 * A [ModifierNode] that modifies the layout of a [com.wolfyscript.viewportl.ui.Node]
 * by modifying the incoming [Constraints] and outgoing [com.wolfyscript.viewportl.ui.layout.Measurements]
 */
interface LayoutModifierNode : ModifierNode {

    /**
     * Modifies the incoming constraints and produces a new [LayoutModification]
     *
     * [constraints] - The incoming constraints (either from a previous modifier or the initial constrain for the node)
     *
     * @return a new [LayoutModification]
     */
    fun LayoutModifyScope.modify(constraints: Constraints): LayoutModification

    fun IntrinsicModifyIncomingScope.modifyMinIntrinsicWidth(height: Dp): Dp {
        return childIntrinsicMinWidth(height)
    }

    fun IntrinsicModifyIncomingScope.modifyMinIntrinsicHeight(width: Dp): Dp {
        return childIntrinsicMinHeight(width)
    }

    fun IntrinsicModifyIncomingScope.modifyMaxIntrinsicWidth(height: Dp): Dp {
        return childIntrinsicMaxWidth(height)
    }

    fun IntrinsicModifyIncomingScope.modifyMaxIntrinsicHeight(width: Dp): Dp {
        return childIntrinsicMaxHeight(width)
    }

}
