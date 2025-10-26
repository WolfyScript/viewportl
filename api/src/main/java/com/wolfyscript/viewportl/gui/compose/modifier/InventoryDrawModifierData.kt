package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.compose.ModifierData
import com.wolfyscript.viewportl.gui.compose.ModifierNode
import com.wolfyscript.viewportl.gui.compose.ModifierStackScope
import com.wolfyscript.viewportl.gui.compose.layout.Position
import com.wolfyscript.viewportl.gui.compose.layout.Size

/**
 * Specifies how content is drawn to a slot based interface (Inventory UI)
 */
fun ModifierStackScope.drawToSlots(fn: InventoryDrawScope.() -> Unit) {

    push(TODO())
}

interface InventoryDrawModifierData : ModifierData {}

/**
 * A nested scope that allows drawing into a section of an Inventory.
 *
 * Positioning is taken care by the scope, so draw calls are always relative to the origin of the current section.
 */
interface InventoryDrawScope {

    /**
     * The width in slots of the current section
     */
    val width: Int

    /**
     * The height in slots of the current section
     */
    val height: Int

    /**
     * Draws the specified [stack] into a slot of size 1x1.
     *
     * [offset] offset relative to this sections origin 0,0
     *
     * [stack] the stack to render into the slot
     */
    fun drawStack(offset: Position = Position(Size(), Size()), stack: ItemStackSnapshot)

}

interface InventoryDrawModifierNode : ModifierNode {

    fun InventoryDrawScope.draw()

}