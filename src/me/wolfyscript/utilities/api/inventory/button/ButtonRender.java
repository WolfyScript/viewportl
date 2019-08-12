package me.wolfyscript.utilities.api.inventory.button;

import me.wolfyscript.utilities.api.inventory.GuiHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface ButtonRender {

    void render(HashMap<String, Object> values, GuiHandler guiHandler, Player player, ItemStack icon);
}
