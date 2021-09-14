package me.wolfyscript.utilities.util.particles.pos;

import org.bukkit.Location;

public class ParticlePosLocation extends ParticlePos {

    private final Location location;

    public ParticlePosLocation(Location location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }
}
