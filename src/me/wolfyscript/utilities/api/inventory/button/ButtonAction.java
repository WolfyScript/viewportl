package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public interface ButtonAction {
    void run(WolfyUtilities api, Player player, InventoryView inventoryView, Inventory inventory, GuiHandler guiHandler);
}
