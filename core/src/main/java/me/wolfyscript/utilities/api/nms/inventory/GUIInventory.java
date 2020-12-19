package me.wolfyscript.utilities.api.nms.inventory;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.inventory.Inventory;

public interface GUIInventory<C extends CustomCache> extends Inventory {

    GuiWindow<C> getWindow();

    GuiHandler<C> getGuiHandler();
}
