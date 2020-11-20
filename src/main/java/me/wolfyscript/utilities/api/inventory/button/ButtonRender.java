package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.inventory.GuiHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface ButtonRender {

    /**
     * Run when the button is rendered into the GUI.
     * The returned ItemStack will be set into the slot of the button.
     * Using the values HashMap you can replace specific Strings in the item names (e.g. replace placeholder from language file) with custom values.
     *
     * @param values      The HashMap, which contains the Strings, that will be replaced with it's value.
     * @param guiHandler  The current GuiHandler.
     * @param player      The current Player.
     * @param icon        The current itemsStack of the button.
     * @param slot        The slot in which the button is rendered.
     * @param helpEnabled Returns true if help is enabled.
     * @return The itemStack that should be set into the GUI.
     */
    ItemStack render(HashMap<String, Object> values, GuiHandler<?> guiHandler, Player player, ItemStack icon, int slot, boolean helpEnabled);
}
