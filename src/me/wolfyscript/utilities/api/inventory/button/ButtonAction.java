package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.inventory.GuiHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface ButtonAction {
    void run(GuiHandler guiHandler, Inventory inventory, int slot, InventoryClickEvent event);
}
