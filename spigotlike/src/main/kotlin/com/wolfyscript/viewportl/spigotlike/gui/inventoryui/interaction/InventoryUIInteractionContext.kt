package com.wolfyscript.viewportl.spigotlike.gui.inventoryui.interaction

import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.interaction.InteractionContext
import org.bukkit.event.inventory.InventoryInteractEvent

data class InventoryUIInteractionContext(
    override val runtime: ViewRuntime,
    val event: InventoryInteractEvent,
    val valueHandler: ValueHandler
) : InteractionContext