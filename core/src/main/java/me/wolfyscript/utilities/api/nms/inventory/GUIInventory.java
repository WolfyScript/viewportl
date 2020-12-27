package me.wolfyscript.utilities.api.nms.inventory;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.inventory.Inventory;

/**
 * This interface extends the bukkit inventory interface and is fully compatible with bukkit.
 * <br/>
 * It is also transferable over Bukkit's API like Inventory Events and player inventories.
 * <br/>
 * This makes it possible to easily check which inventory updates are called from and which GuiHandlers are involved.
 *
 * @param <C> The type of {@link CustomCache}
 */
public interface GUIInventory<C extends CustomCache> extends Inventory {

    /**
     * @return The {@link GuiWindow} of this inventory.
     */
    GuiWindow<C> getWindow();

    /**
     * @return The {@link GuiHandler} that this inventory belongs to.
     */
    GuiHandler<C> getGuiHandler();
}
