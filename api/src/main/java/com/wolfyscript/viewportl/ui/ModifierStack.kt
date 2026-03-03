package com.wolfyscript.viewportl.ui

import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.modifier.*
import kotlin.reflect.KClass
import kotlin.reflect.cast

internal class ModifierStackImpl : ModifierStack {

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

    /**
     * The snapshots of modifications after each [LayoutModifierNode].
     *
     * We keep those for future improvements like
     * multiple [com.wolfyscript.viewportl.ui.modifier.InventoryDrawModifierNode]s and other [ModifierNode]s that may use the layout info closest to them.
     * At the moment only the first entry is important, as it reflects the final modification step.
     */
    private val modificationSnapshots: ArrayDeque<LayoutModification> = ArrayDeque()


    /**
     * Whether the layout modification was performed and produced up-to-date [modificationSnapshots]
     */
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
                        modificationSnapshots.clear() // TODO: only remove those that have changed?
                        performedLayoutModification = false
                        next.updateUnsafe(modifier)
                    }

                    ReplaceNode -> {
                        structuralChange()
                    }
                }
            }
        } else if (previousData.isEmpty()) {
            modificationSnapshots.clear()
            performedLayoutModification = false
            modifiers.clear() // Modifiers should be empty already! just make sure... maybe an assert would be better
            for (newData in data) {
                val modifier = newData.create()
                modifiers += modifier
                if (attached) {
                    modifier.onAttach()
                }
            }
        } else if (data.isEmpty()) {
            modificationSnapshots.clear()
            performedLayoutModification = false
            for (node in modifiers) {
                node.onDetach()
            }
            modifiers.clear()
        } else {
            structuralChange()
        }

    }

    private fun structuralChange() {
        modificationSnapshots.clear()
        performedLayoutModification = false
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
        if (performedLayoutModification) {
            return modificationSnapshots.firstOrNull() ?: SimpleLayoutModification(nodeConstraints) { it }
        }

        val scope: LayoutModifyScope = SimpleLayoutModifyScope()
        for (i in 0 until modifiers.size) {
            val modifier = modifiers[i]
            if (modifier !is LayoutModifierNode) continue

            val latestModification = with(modifier) {
                scope.modify(modificationSnapshots.firstOrNull()?.constraints ?: nodeConstraints)
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
        for (modification in modificationSnapshots) {
            val scope = SimpleMeasureModifyScope(modification.constraints)
            currentMeasure = with(modification) {
                scope.measure(currentMeasure)
            }
        }
        return currentMeasure
    }

    override fun modifyIntrinsic(
        intrinsicSize: IntrinsicSize,
        intrinsicDimension: IntrinsicDimension,
        crossAxisSize: Dp,
        nodeIntrinsics: IntrinsicMeasureBlock,
    ): Dp {
        val startIndex = 0
        for (index in startIndex.coerceAtLeast(0) until modifiers.size) {
            val modifier = modifiers[index]
            if (modifier !is LayoutModifierNode) continue

            val scope = DelegateIntrinsicModifyIncomingScope(index, nodeIntrinsics, modifiers)
            return with(modifier) {
                if (intrinsicDimension == IntrinsicDimension.Width) {
                    if (intrinsicSize == IntrinsicSize.Min) {
                        scope.modifyMinIntrinsicWidth(crossAxisSize)
                    } else {
                        scope.modifyMaxIntrinsicWidth(crossAxisSize)
                    }
                } else {
                    if (intrinsicSize == IntrinsicSize.Min) {
                        scope.modifyMinIntrinsicHeight(crossAxisSize)
                    } else {
                        scope.modifyMaxIntrinsicHeight(crossAxisSize)
                    }
                }
            }
        }
        return crossAxisSize
    }

    private class DelegateIntrinsicModifyIncomingScope(
        val startIndex: Int,
        val measureNode: IntrinsicMeasureBlock,
        val modifiers: List<ModifierNode>,
    ) : IntrinsicModifyIncomingScope {

        private fun getNextNode(): Pair<Int, LayoutModifierNode>? {
            for (i in (startIndex + 1) until modifiers.size) {
                val modifier = modifiers[i]
                if (modifier !is LayoutModifierNode) continue
                return i to modifier
            }
            return null
        }

        private fun childIntrinsic(modify: LayoutModifierNode.(scope: IntrinsicModifyIncomingScope) -> Dp): Dp? {
            val nextModifier = getNextNode()
            if (nextModifier != null) {
                val scope = DelegateIntrinsicModifyIncomingScope(nextModifier.first, measureNode, modifiers)
                return with(nextModifier.second) {
                    modify(scope)
                }
            }
            return null
        }

        override fun childIntrinsicMinWidth(height: Dp): Dp {
            return childIntrinsic { scope ->
                scope.modifyMinIntrinsicWidth(height)
            } ?: measureNode(IntrinsicDimension.Width, IntrinsicSize.Min, height)
        }

        override fun childIntrinsicMinHeight(width: Dp): Dp {
            return childIntrinsic { scope ->
                scope.modifyMinIntrinsicHeight(width)
            } ?: measureNode(IntrinsicDimension.Height, IntrinsicSize.Min, width)
        }

        override fun childIntrinsicMaxWidth(height: Dp): Dp {
            return childIntrinsic { scope ->
                scope.modifyMaxIntrinsicWidth(height)
            } ?: measureNode(IntrinsicDimension.Width, IntrinsicSize.Max, height)
        }

        override fun childIntrinsicMaxHeight(width: Dp): Dp {
            return childIntrinsic { scope ->
                scope.modifyMaxIntrinsicHeight(width)
            } ?: measureNode(IntrinsicDimension.Height, IntrinsicSize.Max, width)
        }

    }

    override fun <T : ModifierNode> firstOfType(nodeType: KClass<T>): T? {
        return modifiers.firstOrNull { nodeType.isInstance(it) }?.let { nodeType.cast(it) }
    }

    override fun <T : ModifierNode> forEachOfType(nodeType: KClass<T>, block: (T) -> Unit) {
        for (modifier in modifiers) {
            if (nodeType.isInstance(modifier)) {
                block(nodeType.cast(modifier))
            }
        }
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

    fun onLayoutChange() {
        for (node in modifiers) {
            node.onLayoutChanged()
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

