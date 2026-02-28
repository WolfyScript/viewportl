package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.viewportl.ui.IntrinsicSize
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

    fun IntrinsicModifyIncomingScope.modifyMinIntrinsicWidth(height: Dp): IntrinsicIncomingModification {
        return passthrough(height, IntrinsicSize.Min)
    }

    fun IntrinsicModifyIncomingScope.modifyMinIntrinsicHeight(width: Dp): IntrinsicIncomingModification {
        return passthrough(width, IntrinsicSize.Min)
    }

    fun IntrinsicModifyIncomingScope.modifyMaxIntrinsicWidth(height: Dp): IntrinsicIncomingModification {
        return passthrough(height, IntrinsicSize.Max)
    }

    fun IntrinsicModifyIncomingScope.modifyMaxIntrinsicHeight(width: Dp): IntrinsicIncomingModification {
        return passthrough(width, IntrinsicSize.Max)
    }

}
