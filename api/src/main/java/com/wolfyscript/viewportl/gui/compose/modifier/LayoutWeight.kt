package com.wolfyscript.viewportl.gui.compose.modifier

internal class LayoutWeightModifier(
    val weight: Float,
    val fill: Boolean,
) : ModifierData<LayoutWeightNode> {

    override fun create(): LayoutWeightNode {
        return LayoutWeightNode(weight, fill)
    }

    override fun update(node: LayoutWeightNode) {
        node.weight = weight
        node.fill = fill
    }

}

internal class LayoutWeightNode(
    var weight: Float,
    var fill: Boolean,
) : ModifierNode, ScopeDataModifierNode {

    override fun onAttach() {}

    override fun onDetach() {}

    override fun modifyScopeData(data: Any?): RowColumnScopeData =
        (data as? RowColumnScopeData ?: RowColumnScopeData()).also {
            it.weight = weight
            it.fill = fill
        }

}