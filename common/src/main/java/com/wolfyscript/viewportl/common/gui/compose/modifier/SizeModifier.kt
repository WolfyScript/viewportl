package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.modifier.*

class SizeModifier(
    val minWidth: Size = Size.Unspecified,
    val minHeight: Size = Size.Unspecified,
    val maxWidth: Size = Size.Unspecified,
    val maxHeight: Size = Size.Unspecified,
    val enforceIncoming: Boolean,
) : ModifierData<SizeModifierNode> {

    override fun create(): SizeModifierNode {
        return SizeModifierNode(
            minWidth, minHeight, maxWidth, maxHeight, enforceIncoming
        )
    }

    override fun update(node: SizeModifierNode) {

    }

}

class SizeModifierNode(
    val minWidth: Size = Size.Unspecified,
    val minHeight: Size = Size.Unspecified,
    val maxWidth: Size = Size.Unspecified,
    val maxHeight: Size = Size.Unspecified,
    val enforceIncoming: Boolean,
) : LayoutModifierNode, ModifierNode {

    override fun onAttach() {

    }

    override fun onDetach() {

    }

    override fun MeasureModifyScope.modify(constraints: Constraints): LayoutModification {
        // TODO

        return modifyLayout(modifiedConstraints = constraints, offset = Offset.Zero)
    }

}
