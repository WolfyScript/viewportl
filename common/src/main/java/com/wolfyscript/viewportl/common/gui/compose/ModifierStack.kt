package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleLayoutModification
import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleLayoutModifyScope
import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleMeasureModifyScope
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierData
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStack
import com.wolfyscript.viewportl.gui.compose.layout.*
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifyScope
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModification
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder
import kotlin.reflect.KClass
import kotlin.reflect.cast

class ModifierStackImpl(internal val modifiers: ArrayDeque<ModifierNode>) : ModifierStack {

    private var attached: Boolean = false

    // TODO: Invalidate these cached modifications when Modifiers change! (assuming we not just swap out the ModifierStackImpl instance, but update the stack directly)
    private val modificationSnapshots: ArrayDeque<LayoutModification> = ArrayDeque()
    private var performedLayoutModification: Boolean = false

    override fun modifyMeasure(
        nodeConstraints: Constraints,
    ): LayoutModification {
        if (modifiers.isEmpty()) return SimpleLayoutModification(nodeConstraints) { it }
        if (modificationSnapshots.isNotEmpty()) {
            return modificationSnapshots.first()
        }
        if (performedLayoutModification) {
            return SimpleLayoutModification(nodeConstraints) { it }
        }

        val scope: LayoutModifyScope = SimpleLayoutModifyScope()
        for (modifier in modifiers) {
            if (modifier !is LayoutModifierNode) continue

            val latestModification = with(modifier) {
                scope.modify(nodeConstraints)
            }
            modificationSnapshots.addFirst(latestModification)
        }
        performedLayoutModification = true
        return modificationSnapshots.firstOrNull() ?: SimpleLayoutModification(nodeConstraints) { it }
    }

    override fun modifyLayout(initialMeasure: MeasureModification): MeasureModification {
        if (modificationSnapshots.isEmpty()) {
            return initialMeasure
        }

        var currentMeasure: MeasureModification = initialMeasure
        val scope = SimpleMeasureModifyScope()
        for (modification in modificationSnapshots) {
            currentMeasure = with(modification) {
                scope.measure(currentMeasure)
            }
        }
        return currentMeasure
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

class SimpleModifierStackBuilder : ModifierStackBuilder {

    val stack = ArrayDeque<ModifierData<*>>()

    override fun push(modifier: ModifierData<*>): ModifierStackBuilder {
        stack.add(modifier)
        return this
    }

    override fun build(): ModifierStackImpl {
        return ModifierStackImpl(stack.mapTo(ArrayDeque()) { data ->
            data.create()
        })
    }

}

