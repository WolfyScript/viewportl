package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifyScope
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierData

class LayoutModifierData(
    val layoutFn: LayoutModifyScope.(Constraints) -> LayoutModification,
) : ModifierData<LayoutModifierNode> {

    override fun create(): LayoutModifierNode {
        return CustomLayoutModifierNode(layoutFn)
    }

    override fun update(node: LayoutModifierNode) {

    }

}

class CustomLayoutModifierNode(val layoutFn: LayoutModifyScope.(Constraints) -> LayoutModification) :
    LayoutModifierNode {

    override fun LayoutModifyScope.modify(
        constraints: Constraints,
    ): LayoutModification {
        return layoutFn(constraints)
    }

    override fun onAttach() {

    }

    override fun onDetach() {

    }

}
