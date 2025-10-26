package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.ModifierData
import com.wolfyscript.viewportl.gui.compose.ModifierNode
import com.wolfyscript.viewportl.gui.compose.ModifierStackScope
import com.wolfyscript.viewportl.gui.compose.layout.Constraints

fun ModifierStackScope.modifyLayout(modifyFn: MeasureModifyScope.(Constraints) -> LayoutModification) {
    push(LayoutModifierData(modifyFn))
}

class LayoutModifierData(
    val layoutFn: MeasureModifyScope.(Constraints) -> LayoutModification
) : ModifierData {

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