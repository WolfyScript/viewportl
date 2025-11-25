package com.wolfyscript.viewportl.spigotlike.gui.inventoryui.rendering

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrapSpigot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawScope
import org.bukkit.inventory.Inventory

class CacheInventoryDrawScope(
    override val nodeOffset: Offset,
    override val width: Int,
    override val height: Int,
    val inventory: Inventory?,
) : InventoryDrawScope {

    override fun drawStack(
        offset: Offset,
        stack: ItemStackSnapshot,
    ) {
        if (inventory == null) return
        val finalPos = nodeOffset + offset
        val slot = finalPos.x.slot.value + finalPos.y.slot.value * 9
        if (slot < inventory.size) {
            inventory.setItem(slot, stack.unwrapSpigot())
        } else {
            ScafallProvider.get().logger.warn("Skipping out of bounds slot: $slot")
        }
    }

    override fun clear() {
        for (x in 0 until width) {
            for (y in 0 until height) {
                inventory?.clear((nodeOffset.x.slot.value + x) + (nodeOffset.y.slot.value + y) * 9)
            }
        }
    }
}