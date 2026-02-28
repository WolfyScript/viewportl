package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Offset
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.layout.constrainHeight
import com.wolfyscript.viewportl.ui.layout.constrainWidth
import com.wolfyscript.viewportl.ui.modifier.LayoutModification
import com.wolfyscript.viewportl.ui.modifier.LayoutModifierNode
import com.wolfyscript.viewportl.ui.modifier.LayoutModifyScope
import com.wolfyscript.viewportl.ui.modifier.ModifierData
import com.wolfyscript.viewportl.viewportl

fun ModifierStackBuilder.padding(
    start: Dp = Dp.Zero,
    top: Dp = Dp.Zero,
    end: Dp = Dp.Zero,
    bottom: Dp = Dp.Zero,
) = push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createPaddingModifier(start, top, end, bottom))

fun ModifierStackBuilder.padding(
    horizontal: Dp = Dp.Zero,
    vertical: Dp = Dp.Zero,
) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createPaddingModifier(
        horizontal,
        vertical,
        horizontal,
        vertical
    )
)


fun ModifierStackBuilder.padding(
    all: Dp = Dp.Zero,
) = push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createPaddingModifier(all, all, all, all))



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
