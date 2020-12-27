package me.wolfyscript.utilities.api.inventory.gui.button;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ButtonPreRender<C extends CustomCache> {

    /**
     * This method is run before the render method and provides the previous inventory, which includes all the items of last render.
     * <br/>
     * It can be used for caching or other code that needs to be executed just before render, requires items from last render or needs to prepare data for the next render.
     * <br/>
     * For example it can be used for setting items into cache for something like item input see {@link ItemInputButton}
     * <br/>
     * .
     *
     * @param cache       The current cache of the GuiHandler
     * @param guiHandler  The current GuiHandler.
     * @param player      The current Player.
     * @param inventory   The original/previous inventory. No changes to this inventory will be applied on render!
     * @param itemStack   The original/previous item stack. No changes to this item stack will be applied on render!
     * @param slot        The slot in which the button is rendered.
     * @param helpEnabled Returns true if help is enabled.
     */
    void prepare(C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, boolean helpEnabled);
}
