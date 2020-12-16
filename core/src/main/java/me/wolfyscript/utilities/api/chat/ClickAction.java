package me.wolfyscript.utilities.api.chat;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.entity.Player;

public interface ClickAction {
    void run(WolfyUtilities api, Player player);
}
