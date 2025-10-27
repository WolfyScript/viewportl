package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints

interface LayoutModifierNode : ModifierNode {

    fun MeasureModifyScope.modify(constraints: Constraints): LayoutModification

}