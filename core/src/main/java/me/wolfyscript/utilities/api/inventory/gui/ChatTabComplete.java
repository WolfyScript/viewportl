package me.wolfyscript.utilities.api.inventory.gui;

import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ChatTabComplete<C extends CustomCache> {

    @Nullable
    List<String> onTabComplete(GuiHandler<C> guiHandler, @NotNull Player sender, @NotNull String[] args);

}
