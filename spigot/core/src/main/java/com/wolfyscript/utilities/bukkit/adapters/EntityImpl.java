package com.wolfyscript.utilities.bukkit.adapters;

import com.wolfyscript.utilities.platform.adapters.Location;
import com.wolfyscript.utilities.platform.adapters.Vector3D;
import com.wolfyscript.utilities.platform.adapters.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EntityImpl<T extends Entity> extends BukkitRefAdapter<T> implements com.wolfyscript.utilities.platform.adapters.Entity {

    public EntityImpl(T entity) {
        super(entity);
    }

    @Override
    public UUID uuid() {
        return getBukkitRef().getUniqueId();
    }

    @Override
    public @NotNull Location getLocation() {
        return BukkitWrapper.adapt(bukkitRef.getLocation());
    }

    @Override
    public @Nullable Location getLocation(Location location) {
        return BukkitWrapper.adapt(bukkitRef.getLocation(((LocationImpl) location).getBukkitRef()));
    }

    @Override
    public void setVelocity(@NotNull Vector3D vector3D) {

    }

    @Override
    public @NotNull Vector3D getVelocity() {
        return null;
    }

    @Override
    public double getHeight() {
        return bukkitRef.getHeight();
    }

    @Override
    public double getWidth() {
        return bukkitRef.getWidth();
    }

    @Override
    public boolean isOnGround() {
        return bukkitRef.isOnGround();
    }

    @Override
    public boolean isInWater() {
        return bukkitRef.isInWater();
    }

    @Override
    public @NotNull World getWorld() {
        return BukkitWrapper.adapt(bukkitRef.getWorld());
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        bukkitRef.setRotation(yaw, pitch);
    }
}
