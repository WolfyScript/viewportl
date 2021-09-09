package me.wolfyscript.utilities.util.particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParticlePosPlayer extends ParticlePos {

    private final Player player;

    public ParticlePosPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    public Player getPlayer() {
        return player;
    }
}
