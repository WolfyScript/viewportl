package me.wolfyscript.utilities.api.inventory.gui.button;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;

import java.io.IOException;

/**
 * @param <C> The type of the {@link CustomCache}
 */
public interface ButtonActionExtended<C extends CustomCache> extends ButtonAction<C> {

    /**
     * @param cache      The current cache of the GuiHandler.
     * @param guiHandler The current GuiHandler.
     * @param player     The current Player.
     * @param inventory  The original/previous inventory. No changes to this inventory will be applied on render!
     * @param slot       The slot in which the button is rendered.
     * @param event      The previous event of the click that caused the update. Can be a InventoryClickEvent or InventoryDragEvent
     * @return a boolean indicating whether the interaction should be cancelled. If true the interaction is cancelled.
     * @throws IOException if an error occurs on the execution.
     */
    boolean run(C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, Button<C> button, int slot, InventoryInteractEvent event) throws IOException;

    @Override
    default boolean run(C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException {
        return run(cache, guiHandler, player, inventory, null, slot, event);
    }
}
