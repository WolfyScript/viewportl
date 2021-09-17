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

    protected void spawnParticle(ParticleEffect effect, Location location) {
        if(location.getWorld() != null) {
            location.getWorld().spawnParticle(effect.getParticle(), location, effect.getCount(), effect.getOffset().getX(), effect.getOffset().getY(), effect.getOffset().getZ(), effect.getExtra(), effect.getData());
        }
    }

    protected void spawnParticle(ParticleEffect effect, Location location, @Nullable Player player) {
        if (player != null) {
            player.spawnParticle(effect.getParticle(), location, effect.getCount(), effect.getOffset().getX(), effect.getOffset().getY(), effect.getOffset().getZ(), effect.getExtra(), effect.getData());
        } else {
            spawnParticle(effect, location);
        }
    }

    public abstract void draw(Timer.Runner timer, ParticleEffect effect, Location origin, @Nullable Player player);

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }
}
