package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleLayoutModification
import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleMeasureModifyScope
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierData
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStack
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackScope
import com.wolfyscript.viewportl.gui.compose.layout.*
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModifyScope
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder
import net.minecraft.world.level.storage.loot.functions.SetAttributesFunction.modifier
import kotlin.reflect.KClass
import kotlin.reflect.cast

class ModifierStackImpl(internal val modifiers: ArrayDeque<ModifierNode>) : ModifierStack {

    private var attached: Boolean = false

    override fun modifyLayout(
        nodeConstraints: Constraints,
    ): LayoutModification {
        if (modifiers.isEmpty()) return SimpleLayoutModification(nodeConstraints)

        val scope: MeasureModifyScope = SimpleMeasureModifyScope()

        var latestModification: LayoutModification? = null
        var totalOffset = Offset.Zero

        for (modifier in modifiers) {
            if (modifier !is LayoutModifierNode) continue

            latestModification = with(modifier) {
                scope.modify(nodeConstraints)
            }
            totalOffset = Offset(totalOffset.x + latestModification.offset.x, totalOffset.y + latestModification.offset.y)
        }

        if (latestModification != null) {
            return SimpleLayoutModification(latestModification.constraints, totalOffset)
        }

        return SimpleLayoutModification(nodeConstraints)
    }

    override fun <T : ModifierNode> firstOfType(nodeType: KClass<T>): T? {
        return modifiers.firstOrNull { nodeType.isInstance(it) }?.let { nodeType.cast(it) }
    }

    /**
     * Called when a node that owns this modifier stack is detached from the tree
     */
    fun onNodeDetach() {
        attached = false

        for (modifier in modifiers) {
            modifier.onDetach()
        }
    }

    /**
     * Called when a node that owns this modifier stack is attached to the tree
     */
    fun onNodeAttach() {
        for (modifier in modifiers) {
            modifier.onAttach()
        }

        attached = true
    }

}

class ModifierStackScopeImpl : ModifierStackScope {

    val stack = ArrayDeque<ModifierNode>()

    override fun push(modifier: ModifierData<*>) {
        stack.addFirst(modifier.create())
    }

    fun create(): ModifierStackImpl {
        return ModifierStackImpl(stack)
    }

}