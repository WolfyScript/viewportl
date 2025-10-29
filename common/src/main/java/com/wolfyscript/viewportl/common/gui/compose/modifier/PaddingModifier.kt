package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.layout.dp
import com.wolfyscript.viewportl.gui.compose.layout.or
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModifyScope
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierData

class PaddingModifier(
    val start: Size = Size.Zero,
    val top: Size = Size.Zero,
    val end: Size = Size.Zero,
    val bottom: Size = Size.Zero,
) : ModifierData<PaddingModifierNode> {

    override fun create(): PaddingModifierNode {
        return PaddingModifierNode(start, top, end, bottom)
    }

    override fun update(node: PaddingModifierNode) {
    }

}

class PaddingModifierNode(
    val start: Size = 0.slots or 0.dp,
    val top: Size = 0.slots or 0.dp,
    val end: Size = 0.slots or 0.dp,
    val bottom: Size = 0.slots or 0.dp,
) : LayoutModifierNode {

    override fun MeasureModifyScope.modify(
        constraints: Constraints
    ): LayoutModification {
        val horizontal = start + end
        val vertical = top + bottom
        val modifiedConstraints = constraints.offset(-horizontal, -vertical)

        return modifyLayout(modifiedConstraints, Offset(start, top))
    }

    override fun onAttach() {
        TODO("Not yet implemented")
    }

    override fun onDetach() {
        TODO("Not yet implemented")
    }

}
