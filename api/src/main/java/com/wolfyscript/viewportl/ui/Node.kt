package com.wolfyscript.viewportl.ui

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.ui.layout.NodeArranger
import com.wolfyscript.viewportl.ui.modifier.ModifierStack
import com.wolfyscript.viewportl.ui.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.viewportl

/**
 * A Node in the Compose Tree Graph.
 *
 * ### Gap Buffer
 * Compose uses a type of gap-buffer to store the tree of Nodes.
 *
 * The buffers operations to insert, move, and remove nodes are handled by
 * - [insertChildAt],
 * - [removeChildrenAt],
 * - [moveChildren],
 * - and [clearChildren]
 *
 */
interface Node {

    companion object {
        val Constructor: () -> Node = { LayoutNode() }

        val SetModifier: Node.(ModifierStackBuilder) -> Unit = { this.modifier = it }
        val SetMeasurePolicy: Node.(MeasurePolicy) -> Unit = { this.measurePolicy = it }
    }

    var parent: Node?
    var modifier: ModifierStackBuilder
    var measurePolicy: MeasurePolicy

    val arranger: NodeArranger
    val modifierStack: ModifierStack

    fun insertChildAt(index: Int, child: Node)

    /**
     * Removes the section of child [Nodes][Node] from this Node.
     */
    fun removeChildrenAt(index: Int, count: Int)

    fun moveChildren(from: Int, to: Int, count: Int)

    fun clearChildren()

    fun forEachChild(action: (Node) -> Unit)

}