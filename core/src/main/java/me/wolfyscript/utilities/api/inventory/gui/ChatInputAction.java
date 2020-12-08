package me.wolfyscript.utilities.api.inventory.gui;

import org.bukkit.entity.Player;

public interface ChatInputAction {
    boolean onChat(GuiHandler guiHandler, Player player, String msg, String[] args);
}
