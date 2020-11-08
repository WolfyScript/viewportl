package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.inventory.GuiHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Deprecated because there are now better options in the ButtonState and Button constructors
 * using lambda.
 */
@Deprecated
public interface ButtonActionRender extends ButtonAction {

    @Deprecated
    ItemStack render(HashMap<String, Object> values, GuiHandler guiHandler, Player player, ItemStack icon, int slot, boolean helpEnabled);
}
