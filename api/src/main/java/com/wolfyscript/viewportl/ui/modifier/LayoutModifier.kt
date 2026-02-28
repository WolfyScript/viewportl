package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.modifier.LayoutModification
import com.wolfyscript.viewportl.ui.modifier.LayoutModifierNode
import com.wolfyscript.viewportl.ui.modifier.LayoutModifyScope
import com.wolfyscript.viewportl.ui.modifier.ModifierData

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
