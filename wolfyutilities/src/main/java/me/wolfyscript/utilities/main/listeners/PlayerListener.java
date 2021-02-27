package me.wolfyscript.utilities.main.listeners;

import me.wolfyscript.utilities.util.entity.PlayerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerUtils.getStore(event.getPlayer());
    }

}
