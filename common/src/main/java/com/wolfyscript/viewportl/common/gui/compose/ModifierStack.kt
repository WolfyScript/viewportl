package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleLayoutModification
import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleLayoutModifyScope
import com.wolfyscript.viewportl.common.gui.compose.modifier.SimpleMeasureModifyScope
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.modifier.*
import kotlin.reflect.KClass
import kotlin.reflect.cast

class ModifierStackImpl() : ModifierStack {

    companion object {
        private const val ReuseNode = 0
        private const val UpdateNode = 1
        private const val ReplaceNode = 2

        fun modifierDataAction(prev: ModifierData<*>, next: ModifierData<*>): Int {
            if (prev == next) {
                return ReuseNode
            }
            if (prev::class.java == next::class.java) {
                return UpdateNode
            }
            return ReuseNode
        }
    }

    internal var data: List<ModifierData<*>> = emptyList()
    internal val modifiers: ArrayDeque<ModifierNode> = ArrayDeque()
    private var attached: Boolean = false

    // TODO: Invalidate these cached modifications when Modifiers change! (assuming we not just swap out the ModifierStackImpl instance, but update the stack directly)
    private val modificationSnapshots: ArrayDeque<LayoutModification> = ArrayDeque()
    private var performedLayoutModification: Boolean = false

    fun update(stackBuilder: ModifierStackBuilder) {
        val previousData = data
        data = stackBuilder.data
        if (previousData.size == data.size) {
            for ((index, prev) in previousData.withIndex()) {
                val next = data[index]
                val modifier = modifiers[index]
                when (modifierDataAction(prev, next)) {
                    ReuseNode -> {
                        // Do nothing
                    }

                    UpdateNode -> {
                        next.updateUnsafe(modifier)
                    }

                    ReplaceNode -> {
                        structuralChange()
                    }
                }
            }
        } else if (previousData.isEmpty()) {
            modifiers.clear() // Modifiers should be empty already! just make sure... maybe an assert would be better
            for (newData in data) {
                val modifier = newData.create()
                modifiers += modifier
                if (attached) {
                    modifier.onAttach()
                }
            }
        } else if (data.isEmpty()) {
            for (node in modifiers) {
                node.onDetach()
            }
            modifiers.clear()
        } else {
            structuralChange()
        }

    }

    private fun structuralChange() {
        // TODO: Structural update, for now just remove all and readd new
        for (node in modifiers) {
            node.onDetach()
        }
        modifiers.clear()
        for (modifierData in data) {
            val modifier = modifierData.create()
            modifiers += modifier
            if (attached) {
                modifier.onAttach()
            }
        }
    }

    fun updateModifier(next: ModifierData<*>, modifier: ModifierNode) {
        next.updateUnsafe(modifier)
    }

    private fun <T : ModifierNode> ModifierData<T>.updateUnsafe(modifier: ModifierNode) {
        update(modifier as T)
    }


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

    fun onMeasureChange() {
        for (node in modifiers) {
            node.onMeasurementsChanged()
        }
    }

}

class SimpleModifierStackBuilder : ModifierStackBuilder {

    val stack = ArrayDeque<ModifierData<*>>()
    override val data: List<ModifierData<*>>
        get() = stack.toList()

    override fun push(modifier: ModifierData<*>): ModifierStackBuilder {
        stack.add(modifier)
        return this
    }

}

