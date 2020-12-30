package me.wolfyscript.utilities.util.particles;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.entity.PlayerUtils;
import me.wolfyscript.utilities.util.world.BlockCustomItemStore;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * This class is for combining multiple ParticleEffects and spawn them simultaneously.
 * They are required to spawn continues ParticleEffects.
 * If you want to just spawn a one time ParticleEffect use the methods of the {@link ParticleEffect} class instead.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ParticleAnimation {

    private final String name;
    private final List<String> description;
    private final Material icon;
    private final int delay;
    private final int interval;
    private final List<ParticleEffect> particleEffects;
    @JsonIgnore
    private NamespacedKey namespacedKey;

    /**
     * Default constructor to create the default values for deserialization.
     */
    public ParticleAnimation() {
        this(Material.FIREWORK_ROCKET, "DEFAULT", null, 0, 1);
    }

    /**
     * @param icon            The Material as the icon.
     * @param name            The name of this animation.
     * @param description     The description of this animation.
     * @param delay           The delay before the particles are spawned.
     * @param interval        The interval in which the ParticleEffects are spawned. In ticks.
     * @param particleEffects The ParticleEffects that will be spawned by this animation.
     */
    public ParticleAnimation(Material icon, String name, List<String> description, int delay, int interval, ParticleEffect... particleEffects) {
        this.icon = icon;
        this.particleEffects = particleEffects == null ? new ArrayList<>() : Arrays.asList(particleEffects);
        this.name = name;
        this.description = description == null ? new ArrayList<>() : description;
        this.delay = delay;
        this.interval = interval;
    }

    /**
     * Spawn the animation at the specified location in the world.
     *
     * @param location The location to spawn the animation at.
     */
    public void spawnOnLocation(Location location) {
        runTimer(() -> particleEffects.forEach(particleEffect -> particleEffect.onLocation(location)));
    }

    /**
     * Spawn the animation on the specified block.
     *
     * @param block The block to spawn the animation on.
     */
    public void spawnOnBlock(Block block) {
        BlockCustomItemStore blockStore = WorldUtils.getWorldCustomItemStore().get(block.getLocation());
        if (blockStore != null) {
            blockStore.setParticleUUID(runTimer(() -> particleEffects.forEach(particleEffect -> particleEffect.onBlock(block))));
        }
    }

    /**
     * Spawn the animation on the specified entity.
     *
     * @param entity The entity to spawn the animation on.
     */
    public void spawnOnEntity(Entity entity) {
        runTimer(() -> particleEffects.forEach(particleEffect -> particleEffect.onEntity(entity)));
    }

    /**
     * Spawn the animation on the specified Player and Equipment Slot.
     *
     * @param player The {@link Player} to spawn the animation on.
     * @param slot   The {@link EquipmentSlot} this animation is spawned on.
     */
    public void spawnOnPlayer(Player player, EquipmentSlot slot) {
        PlayerUtils.setActiveParticleEffect(player, slot, runTimer(() -> {
            if (player != null && player.isValid()) {
                particleEffects.forEach(particleEffect -> particleEffect.onPlayer(player, slot));
            }
        }));
    }

    private UUID runTimer(Runnable runnable) {
        return ParticleUtils.addTask(Bukkit.getScheduler().runTaskTimerAsynchronously(WolfyUtilities.getWUPlugin(), runnable, delay, interval));
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    @Override
    public String toString() {
        return "ParticleAnimation{" +
                "name='" + name + '\'' +
                ", description=" + description +
                ", icon=" + icon +
                ", delay=" + delay +
                ", interval=" + interval +
                ", particleEffects=" + particleEffects +
                ", namespacedKey=" + namespacedKey +
                '}';
    }
}
