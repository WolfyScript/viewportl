package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Dp
import com.wolfyscript.viewportl.gui.compose.layout.constrainHeight
import com.wolfyscript.viewportl.gui.compose.layout.constrainWidth
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifyScope
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierData

class PaddingModifier(
    val start: Dp = Dp.Zero,
    val top: Dp = Dp.Zero,
    val end: Dp = Dp.Zero,
    val bottom: Dp = Dp.Zero,
) : ModifierData<PaddingModifierNode> {

    override fun create(): PaddingModifierNode {
        return PaddingModifierNode(start, top, end, bottom)
    }

    override fun update(node: PaddingModifierNode) {
    }

}

class PaddingModifierNode(
    val start: Dp = Dp.Zero,
    val top: Dp = Dp.Zero,
    val end: Dp = Dp.Zero,
    val bottom: Dp = Dp.Zero,
) : LayoutModifierNode {

    override fun LayoutModifyScope.modify(
        constraints: Constraints
    ): LayoutModification {
        val horizontal = start + end
        val vertical = top + bottom
        val modifiedConstraints = constraints.offset(-horizontal, -vertical)

        return modifyLayout(modifiedConstraints) { childMeasure ->
            val width = constraints.constrainWidth(childMeasure.measuredWidth + horizontal)
            val height = constraints.constrainHeight(childMeasure.measuredHeight + vertical)
            modifyMeasure(width, height, Offset(start, top))
        }
    }

    override fun onAttach() {

    }

    override fun onDetach() {

    }

}
