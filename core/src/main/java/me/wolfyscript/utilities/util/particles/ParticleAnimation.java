package me.wolfyscript.utilities.util.particles;

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
public class ParticleAnimation {

    private NamespacedKey namespacedKey;
    private String name;
    private List<String> description;
    private Material icon = Material.FIREWORK_ROCKET;
    private List<ParticleEffect> particleEffects = new ArrayList<>();
    private int delay = 0;
    private int interval = 1;

    public ParticleAnimation() {

    }

    public ParticleAnimation(Material icon, String name, List<String> description, int delay, int interval, ParticleEffect... particleEffects) {
        this.icon = icon;
        this.particleEffects = Arrays.asList(particleEffects);
        this.name = name;
        this.description = description == null ? new ArrayList<>() : description;
        this.delay = delay;
        this.interval = interval;
    }

    public void spawnOnLocation(Location location) {
        runTimer(() -> particleEffects.forEach(particleEffect -> particleEffect.onLocation(location)));
    }

    public void spawnOnBlock(Block block) {
        BlockCustomItemStore blockStore = WorldUtils.getWorldCustomItemStore().get(block.getLocation());
        if (blockStore != null) {
            blockStore.setParticleUUID(runTimer(() -> particleEffects.forEach(particleEffect -> particleEffect.onBlock(block))));
        }
    }

    public void spawnOnEntity(Entity entity) {
        runTimer(() -> particleEffects.forEach(particleEffect -> particleEffect.onEntity(entity)));
    }

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

    public List<ParticleEffect> getParticleEffects() {
        return particleEffects;
    }

    public void setParticleEffects(List<ParticleEffect> particleEffects) {
        this.particleEffects = particleEffects;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "ParticleAnimation{" +
                "namespacedKey=" + namespacedKey +
                ", particleEffects=" + particleEffects +
                ", icon=" + icon +
                ", name='" + name + '\'' +
                ", description=" + description +
                ", interval=" + interval +
                '}';
    }

}
