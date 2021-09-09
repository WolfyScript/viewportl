package me.wolfyscript.utilities.util.particles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ParticlePosEntity extends ParticlePos {

    private final Entity entity;

    public ParticlePosEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Location getLocation() {
        return entity.getLocation();
    }
}
