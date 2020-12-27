package me.wolfyscript.utilities.api.inventory.gui.button;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

/**
 * This interface is identical to the {@link ButtonAction}, however the behavior is different as it is similar to {@link ButtonPreRender}.
 * It is called 1 tick after the execution, right before the ButtonPreRender and only if the button was clicked!
 * <br/>
 * It can be used for caching or other code that needs to be executed just before render, but only after execution happened, to prepare data for the next render.
 * <br/>
 * For example it can be used for setting items into cache for something like item input see {@link ItemInputButton}
 *
 * @param <C> The type of the cache to support custom cache objects.
 */
public interface ButtonPostAction<C extends CustomCache> {

    /**
     * @param cache      The current cache of the GuiHandler.
     * @param guiHandler The current GuiHandler.
     * @param player     The current Player.
     * @param inventory  The original/previous inventory. No changes to this inventory will be applied on render!
     * @param slot       The slot in which the button is rendered.
     * @param event      The previous event of the click that caused the update. Can be a InventoryClickEvent or InventoryDragEvent
     * @throws IOException if an error occurs on the execution.
     */
    void run(C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException;

}
