package com.wolfyscript.viewportl.spigotlike.gui.inventoryui.interaction

import com.wolfyscript.viewportl.runtime.UIRuntime
import com.wolfyscript.viewportl.gui.interaction.InteractionContext
import org.bukkit.event.inventory.InventoryInteractEvent

data class InventoryUIInteractionContext(
    override val runtime: UIRuntime,
    val event: InventoryInteractEvent,
) : InteractionContext