package com.wolfyscript.utilities.bukkit.adapters;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PlayerImpl extends EntityImpl<Player> implements com.wolfyscript.utilities.platform.adapters.Player {

    public PlayerImpl(Player player) {
        super(player);
    }

    @Override
    public void setDisplayName(Component component) {

    }

    @Override
    public Component getDisplayName() {
        return null;
    }
}
