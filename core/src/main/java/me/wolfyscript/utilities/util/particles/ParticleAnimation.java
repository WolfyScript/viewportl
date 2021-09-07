package me.wolfyscript.utilities.util.particles;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.Keyed;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is for combining multiple ParticleEffects and spawn them simultaneously.
 * They are required to spawn continues ParticleEffects.
 * If you want to just spawn a one time ParticleEffect use the methods of the {@link ParticleEffect} class instead.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ParticleAnimation implements Keyed {

    @JsonIgnore
    private NamespacedKey key;

    private final String name;
    private final List<String> description;
    private final Material icon;

    private final int delay;
    private final int interval;
    private final int loop;
    private final List<ParticleEffectSettings> particleEffects;
    private final Map<Integer, List<ParticleEffectSettings>> effectsPerTick;

    /**
     * @param icon            The Material as the icon.
     * @param name            The name of this animation.
     * @param description     The description of this animation.
     * @param delay           The delay before the particles are spawned.
     * @param interval        The interval in which the ParticleEffects are spawned. In ticks.
     * @param effectSettings The ParticleEffects that will be spawned by this animation.
     */
    public ParticleAnimation(Material icon, String name, List<String> description, int delay, int interval, int loop, ParticleEffectSettings... effectSettings) {
        this.icon = icon;
        this.particleEffects = Arrays.asList(effectSettings);
        this.effectsPerTick = particleEffects.stream().collect(Collectors.toMap(settings -> settings.tick, settings -> {
            List<ParticleEffectSettings> values = new ArrayList<>();
            values.add(settings);
            return values;
        }, (list, list2) -> {
            list.addAll(list2);
            return list;
        }));
        this.name = name;
        this.description = Objects.requireNonNullElse(description, new ArrayList<>());
        this.delay = delay;
        this.interval = interval;
        this.loop = loop;
    }

    /**
     * Spawn the animation at the specified location in the world.
     *
     * @param location The location to spawn the animation at.
     */
    public void spawnOnLocation(Location location) {
        runScheduler(location);
    }

    /**
     * Spawn the animation on the specified block.
     *
     * @param block The block to spawn the animation on.
     */
    public void spawnOnBlock(Block block) {
        BlockCustomItemStore blockStore = WorldUtils.getWorldCustomItemStore().get(block.getLocation());
        if (blockStore != null) {
            blockStore.setParticleUUID(runScheduler(block.getLocation()));
        }
    }

    /**
     * Spawn the animation on the specified entity.
     *
     * @param entity The entity to spawn the animation on.
     */
    public void spawnOnEntity(Entity entity) {
        runScheduler(entity.getLocation());
    }

    /**
     * Spawn the animation on the specified Player and Equipment Slot.
     *
     * @param player The {@link Player} to spawn the animation on.
     * @param slot   The {@link EquipmentSlot} this animation is spawned on.
     */
    public void spawnOnPlayer(Player player, EquipmentSlot slot) {
        PlayerUtils.setActiveParticleEffect(player, slot, runScheduler(player.getLocation()));
    }

    private UUID runScheduler(Location location) {
        return new Scheduler(location).start();
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    public void setKey(NamespacedKey key) {
        this.key = key;
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
                ", namespacedKey=" + key +
                '}';
    }

    public static class ParticleEffectSettings {

        private final ParticleEffect effect;
        private final Vector offset;
        private final int tick;

        public ParticleEffectSettings(ParticleEffect effect, Vector offset, int tick) {
            this.effect = effect;
            this.offset = offset;
            this.tick = tick;
        }

        public ParticleEffect getEffect() {
            return effect;
        }

        public Vector getOffset() {
            return offset;
        }
    }

    /**
     * This scheduler runs the ParticleAnimations with the specified delay and interval.
     * If it is started it is saved in cache and is assigned a UUID (See ParticleUtils).
     * Using these UUIDs you can stop specific animations.
     *
     */
    public class Scheduler implements Runnable {

        private BukkitTask task = null;
        private UUID uuid = null;
        private final Location location;
        private final Player player;
        private final Block block;
        private final Entity entity;
        private int tick = 0;
        private int looped = 0;

        public Scheduler(Location location) {
            this(location, null);
        }

        public Scheduler(Block block) {
            this(block, null);
        }

        public Scheduler(Entity entity) {
            this(entity, null);
        }

        /**
         *
         * @param location The location to spawn the animation at.
         * @param player The player to send the particles to. If null particles are sent to all surrounding players.
         */
        public Scheduler(Location location, Player player) {
            this(location, null, null, player);
        }

        public Scheduler(Block block, Player player) {
            this(null, block, null, player);
        }

        public Scheduler(Entity entity, Player player) {
            this(null, null, entity, player);
        }

        private Scheduler(Location location, Block block, Entity entity, Player player) {
            this.location = location;
            this.block = block;
            this.entity = entity;
            this.player = player;
        }

        /**
         * Starts and caches the animation.
         * It may be stopped if it is a continues animations, or before it is stopped automatically if it is limited.
         *
         * @return The UUID of the running animation.
         */
        public UUID start() {
            this.task = Bukkit.getScheduler().runTaskTimer(WolfyUtilities.getWUPlugin(), this, delay, 1);
            this.uuid = ParticleUtils.addScheduler(this);
            return uuid;
        }

        /**
         * Stops the current running animation.
         *
         */
        public void stop() {
            Objects.requireNonNull(task).cancel();
            ParticleUtils.removeScheduler(uuid);
            this.uuid = null;
            this.task = null;
        }

        public boolean isRunning() {
            return task != null && !task.isCancelled();
        }

        public void run() {
            if (loop == -1 || looped < loop) {
                if (tick < interval) {
                    //Spawn tick specific ParticleEffects
                    List<ParticleEffectSettings> settings = effectsPerTick.get(tick);
                    if (settings != null && !settings.isEmpty()) {
                        for (ParticleEffectSettings setting : settings) {
                            if(location != null && location.getWorld() != null) {
                                setting.getEffect().spawn(location.clone().add(setting.offset), player);
                            }
                        }
                    }
                    tick++;
                } else {
                    tick = 0;
                    if (loop != -1) {
                        looped++;
                    }
                }
            } else {
                stop();
            }
        }

    }
}
