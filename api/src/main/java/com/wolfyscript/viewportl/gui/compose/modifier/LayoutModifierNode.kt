package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints

/**
 * A [ModifierNode] that modifies the layout of a [com.wolfyscript.viewportl.gui.compose.Node]
 * by modifying the incoming [Constraints] and outgoing [com.wolfyscript.viewportl.gui.compose.layout.Measurements]
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

}