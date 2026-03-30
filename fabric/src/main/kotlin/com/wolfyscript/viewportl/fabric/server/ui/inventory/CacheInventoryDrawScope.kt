package com.wolfyscript.viewportl.fabric.server.ui.inventory

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.wrappers.unwrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.fabric.server.ui.CustomUIContainer
import com.wolfyscript.viewportl.ui.layout.Offset
import com.wolfyscript.viewportl.ui.modifier.InventoryDrawScope
import net.minecraft.world.item.ItemStack

class CacheInventoryDrawScope(
    override val nodeOffset: Offset,
    override val width: Int,
    override val height: Int,
    val container: CustomUIContainer?,
) : InventoryDrawScope {

    override fun drawStack(
        offset: Offset,
        stack: ItemStackSnapshot,
    ) {
        if (container == null) return

        val finalPos = offset + nodeOffset
        val slot = finalPos.x.roundToSlots() + finalPos.y.roundToSlots() * 9
        if (slot < container.size) {
            container.setItem(slot, stack.unwrap())
        } else {
            ScafallProvider.get().logger.warn("Skipping out of bounds slot: $slot")
        }
    }

    override fun clear() {
        if (container == null) return

        for (x in 0 until width) {
            for (y in 0 until height) {
                container.setItem(
                    (nodeOffset.x.roundToSlots() + x) + (nodeOffset.y.roundToSlots() + y) * 9,
                    ItemStack.EMPTY,
                )
            }
        }
    }
}