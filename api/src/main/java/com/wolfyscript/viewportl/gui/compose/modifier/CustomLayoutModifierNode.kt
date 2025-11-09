package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints

fun ModifierStackBuilder.modifyLayout(modifyFn: MeasureModifyScope.(Constraints) -> LayoutModification): ModifierStackBuilder =
    push(LayoutModifierData(modifyFn))

class LayoutModifierData(
    val layoutFn: MeasureModifyScope.(Constraints) -> LayoutModification,
) : ModifierData<LayoutModifierNode> {

    override fun create(): LayoutModifierNode {
        return CustomLayoutModifierNode(layoutFn)
    }

    override fun update(node: LayoutModifierNode) {

    }

}

class CustomLayoutModifierNode(val layoutFn: MeasureModifyScope.(Constraints) -> LayoutModification) :
    LayoutModifierNode {

    override fun MeasureModifyScope.modify(
        constraints: Constraints,
    ): LayoutModification {
        return layoutFn(constraints)
    }

    override fun onAttach() {

    }

    override fun onDetach() {

    }

}