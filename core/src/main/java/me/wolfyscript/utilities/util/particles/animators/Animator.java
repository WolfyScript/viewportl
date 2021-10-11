package me.wolfyscript.utilities.util.particles.animators;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * The Animator is used to draw/spawn the {@link ParticleEffect}s.<br>
 * It uses the {@link Timer.Runner} to calculate positions of particles dependent on the time passed, and possible other data.<br>
 *
 *
 */
@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"key"})
public abstract class Animator implements Keyed {

    private final NamespacedKey key;

    protected Animator(NamespacedKey key) {
        this.key = key;
    }

    /**
     * Spawns the {@link ParticleEffect} at the specified location in the world.
     * This will send the particles to all players in range.
     *
     * @param effect The effect to spawn.
     * @param location The location to spawn it on.
     */
    protected void spawnParticle(ParticleEffect effect, Location location) {
        if(location.getWorld() != null) {
            location.getWorld().spawnParticle(effect.getParticle(), location, effect.getCount(), effect.getOffset().getX(), effect.getOffset().getY(), effect.getOffset().getZ(), effect.getExtra(), effect.getData());
        }
    }

    /**
     * Spawns the {@link ParticleEffect} at the specified location in the world.
     * If a player is specified only that player will receive the particles, else {@link #spawnParticle(ParticleEffect, Location)}
     *
     * @param effect The effect to spawn.
     * @param location The location to spawn it on.
     * @param player The player to send the particles to. If null, sends it to all players in range (If the value is always null use {@link #spawnParticle(ParticleEffect, Location)}!).
     */
    protected void spawnParticle(ParticleEffect effect, Location location, @Nullable Player player) {
        if (player != null) {
            player.spawnParticle(effect.getParticle(), location, effect.getCount(), effect.getOffset().getX(), effect.getOffset().getY(), effect.getOffset().getZ(), effect.getExtra(), effect.getData());
        } else {
            spawnParticle(effect, location);
        }
    }

    /**
     * Called each time a {@link ParticleEffect} is spawned.<br>
     * The {@link me.wolfyscript.utilities.util.particles.timer.Timer.Runner} contains the current state of the effect, like tick, or other type specific data.
     * This method must call the {@link Timer.Runner#increase()} to increase the timer and get the passed time and continue.
     *
     * @param timer The timer that contains the state of the effect.
     * @param effect The effect that is being spawned.
     * @param origin The origin location of the effect. If the effect is part of an animation this includes the offset.
     * @param player The player to spawn the effect for. Null if no player is specified, which means the effect is spawned for all the players in range.
     */
    public abstract void draw(Timer.Runner timer, ParticleEffect effect, Location origin, @Nullable Player player);

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }
}
