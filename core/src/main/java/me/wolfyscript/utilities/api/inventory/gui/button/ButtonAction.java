package me.wolfyscript.utilities.api.inventory.gui.button;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public interface ButtonAction<C extends CustomCache> {

    boolean run(GuiHandler<C> guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) throws IOException;
}
