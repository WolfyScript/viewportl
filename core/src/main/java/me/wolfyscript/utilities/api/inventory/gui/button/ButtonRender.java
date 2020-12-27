package me.wolfyscript.utilities.api.inventory.gui.button;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface ButtonRender<C extends CustomCache> {

    /**
     * Run when the button is rendered into the GUI.
     * The returned ItemStack will be set into the slot of the button.
     * Using the values HashMap you can replace specific Strings in the item names (e.g. replace placeholder from language file) with custom values.
     *
     * @param values       The HashMap, which contains the Strings, that will be replaced with it's value.
     * @param cache        The current cache of the GuiHandler
     * @param guiHandler   The current GuiHandler.
     * @param player       The current Player.
     * @param guiInventory The GUIInventory in which this render was called from.
     * @param itemStack    The current itemsStack of the button.
     * @param slot         The slot in which the button is rendered.
     * @param helpEnabled  Returns true if help is enabled.
     * @return The itemStack that should be set into the GUI.
     */
    ItemStack render(HashMap<String, Object> values, C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, ItemStack itemStack, int slot, boolean helpEnabled);
}
