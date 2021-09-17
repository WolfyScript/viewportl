package me.wolfyscript.utilities.util.particles;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalKeyReference;
import me.wolfyscript.utilities.util.particles.animators.Animator;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import me.wolfyscript.utilities.util.particles.timer.TimerLinear;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Contains the location, offset, ParticleEffects, etc.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@OptionalKeyReference(field = "key")
public class ParticleEffect implements Keyed {

    @JsonIgnore
    private NamespacedKey key;

    private final String name;
    private final List<String> description;
    private final Material icon;

    @JsonIgnore
    private final Class<?> dataType;
    private final org.bukkit.Particle particle;
    private Object data = null;
    private Vector offset = new Vector(0, 0, 0);
    private int count = 1;
    private double extra = 0d;
    private Timer timer = new TimerLinear();
    private Animator animator;

    @JsonCreator
    public ParticleEffect(@JsonProperty("particle") Particle particle) {
        this.name = "";
        this.description = List.of();
        this.icon = Material.FIREWORK_STAR;
        this.particle = particle;
        this.dataType = particle.getDataType();
    }

    public ParticleEffect(Particle particle, int count, Vector offset, double extra, Object data, Timer timer, Animator animator) {
        this.name = "";
        this.description = List.of();
        this.icon = Material.FIREWORK_ROCKET;

        this.particle = particle;
        this.dataType = particle.getDataType();
        this.count = count;
        this.offset = offset;
        this.extra = extra;
        this.data = data;
        this.timer = timer;
        this.animator = animator;
    }

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    public void setKey(NamespacedKey key) {
        this.key = key;
    }

    public boolean hasKey() {
        return key != null;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getDescription() {
        return description;
    }

    public Material getIcon() {
        return this.icon;
    }

    public org.bukkit.Particle getParticle() {
        return particle;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public Vector getOffset() {
        return offset;
    }

    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getExtra() {
        return extra;
    }

    public void setExtra(double extra) {
        this.extra = extra;
    }

    public Timer getTimeSupplier() {
        return timer;
    }

    public void setTimeSupplier(Timer timer) {
        this.timer = timer;
    }

    public Animator getAnimator() {
        return animator;
    }

    public void setAnimator(Animator animator) {
        this.animator = animator;
    }

    @Override
    public String toString() {
        return "ParticleEffect{" +
                "key=" + key +
                ", name='" + name + '\'' +
                ", description=" + description +
                ", icon=" + icon +
                ", dataType=" + dataType +
                ", particle=" + particle +
                ", data=" + data +
                ", offset=" + offset +
                ", count=" + count +
                ", extra=" + extra +
                ", timer=" + timer +
                ", animator=" + animator +
                '}';
    }

    /**
     * Spawns the effect at the specified location.
     * Particles are sent to all players in range.
     *
     * @param location The location at which the effect is spawned at.
     */
    public void spawn(Location location) {
        new Task(location).run();
    }

    /**
     * Spawns the effect at the specified location.
     * If the player is specified, the particles are only send to that player.
     *
     * @param location The location at which the effect is spawned at.
     * @param player   The optional player to send the particles to.
     */
    public void spawn(Location location, @Nullable Player player) {
        new Task(location, player).run();
    }

    public void spawn(@NotNull Block block) {
        new Task(block.getLocation()).run();
    }

    public void spawn(Entity entity) {
        new Task(entity.getLocation()).run();
    }

    /**
     * Task that executes the particle effect and runs it in a new thread.
     */
    public class Task implements Runnable {

        private final Player player;
        private final Location origin;
        private final Timer.Runner runner = timer.createRunner();

        public Task(Location origin) {
            this(origin, null);
        }

        public Task(Location origin, Player player) {
            this.player = player;
            this.origin = origin;
        }

        @Override
        public void run() {
            Bukkit.getScheduler().runTaskTimer(WolfyUtilities.getWUPlugin(), task -> {
                if (!task.isCancelled()) {
                    animator.draw(runner.increase(), ParticleEffect.this, origin, player);
                    if (runner.shouldStop()) {
                        task.cancel();
                    }
                }
            }, 0, 1);

        }
    }
}
