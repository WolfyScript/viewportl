package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.entity.Player;

public interface ChatInputAction<C extends CustomCache> {

    boolean onChat(GuiHandler<C> guiHandler, Player player, String msg, String[] args);
}
