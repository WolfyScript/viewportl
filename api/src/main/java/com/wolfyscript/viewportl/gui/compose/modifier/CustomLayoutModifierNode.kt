package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.Modifier
import com.wolfyscript.viewportl.gui.compose.ModifierNode
import com.wolfyscript.viewportl.gui.compose.ModifierStackScope
import com.wolfyscript.viewportl.gui.compose.layout.Constraints

fun ModifierStackScope.modifyLayout(modifyFn: MeasureModifyScope.(Constraints) -> LayoutModification) {
    push(LayoutModifier(modifyFn))
}

class LayoutModifier(
    val layoutFn: MeasureModifyScope.(Constraints) -> LayoutModification
) : Modifier {

    override fun create(): ModifierNode {
        return CustomLayoutModifierNode(layoutFn)
    }

}

class CustomLayoutModifierNode(val layoutFn: MeasureModifyScope.(Constraints) -> LayoutModification) : LayoutModifierNode {

    override fun MeasureModifyScope.modify(
        constraints: Constraints
    ): LayoutModification {
        return layoutFn(constraints)
    }

    override fun onAttach() {

    }

    override fun onDetach() {

    }

}