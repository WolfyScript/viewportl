package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleLayoutModification
import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleMeasureModifyScope
import com.wolfyscript.viewportl.gui.compose.Modifier
import com.wolfyscript.viewportl.gui.compose.ModifierNode
import com.wolfyscript.viewportl.gui.compose.ModifierStack
import com.wolfyscript.viewportl.gui.compose.ModifierStackScope
import com.wolfyscript.viewportl.gui.compose.layout.*
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModifyScope

class ModifierStackImpl(internal val modifiers: ArrayDeque<ModifierNode>) : ModifierStack {

    override fun modifyLayout(
        nodeConstraints: Constraints,
    ): LayoutModification {
        if (modifiers.isEmpty()) return SimpleLayoutModification(nodeConstraints)

        val scope: MeasureModifyScope = SimpleMeasureModifyScope()

        var latestModification: LayoutModification? = null
        var totalOffset = Position(Size(), Size())

        for (modifier in modifiers) {
            if (modifier !is LayoutModifierNode) continue

            latestModification = with(modifier) {
                scope.modify(nodeConstraints)
            }
            totalOffset = Position(totalOffset.x + latestModification.offset.x, totalOffset.y + latestModification.offset.y)
        }

        if (latestModification != null) {
            return SimpleLayoutModification(latestModification.constraints, totalOffset)
        }

        return SimpleLayoutModification(nodeConstraints)
    }

}

class ModifierStackScopeImpl : ModifierStackScope {

    val stack = ArrayDeque<ModifierNode>()

    override fun push(modifier: Modifier) {
        stack.addFirst(modifier.create())
    }

    fun create(): ModifierStack {
        return ModifierStackImpl(stack)
    }

}