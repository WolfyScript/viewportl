package me.wolfyscript.utilities.api.inventory.gui.button;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface ButtonPostAction {

    /**
     * This method is identical to the prepareRender method and provides the previous inventory, which includes all the items of last render.
     * It is called right before the prepare render method, but only if the button was executed in any way!
     * <br/>
     * It can be used for caching or other code that needs to be executed just before render/after execution, requires items from last render or needs to prepare data for the next render.
     * <br/>
     * For example it can be used for setting items into cache for something like item input see {@link ItemInputButton}
     * <br/>
     * .
     *
     * @param guiHandler The current GuiHandler.
     * @param player     The current Player.
     * @param inventory  The original/previous inventory. No changes to this inventory will be applied on render!
     * @param itemStack  The original/previous item stack. No changes to this item stack will be applied on render!
     * @param slot       The slot in which the button is rendered.
     * @param event      The previous event of the click that caused the update. Can be a InventoryClickEvent or InventoryDragEvent
     */
    void process(GuiHandler<?> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int slot, InventoryInteractEvent event);
}
