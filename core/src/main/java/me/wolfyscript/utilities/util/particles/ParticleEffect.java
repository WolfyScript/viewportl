package me.wolfyscript.utilities.util.particles;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.animators.Animator;
import me.wolfyscript.utilities.util.particles.timer.TimeSupplier;
import me.wolfyscript.utilities.util.particles.timer.TimeSupplierLinear;
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
import java.util.Objects;

/**
 * Contains the location, offset, ParticleEffects, etc.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ParticleEffect implements Keyed {

    private final NamespacedKey key;
    private final String name;
    private final List<String> description;
    private final Material icon;

    @JsonIgnore
    private Class<?> dataClass;
    private org.bukkit.Particle particle = Particle.FLAME;
    private Object data = null;
    private Vector offset = new Vector(0, 0, 0);
    private Integer count = 1;
    private Double speed = 0d;
    private TimeSupplier timeSupplier = new TimeSupplierLinear();
    private Animator animator;

    @JsonCreator
    public ParticleEffect(@JsonProperty NamespacedKey key, @JsonProperty String name, @JsonProperty List<String> description, @JsonProperty Material icon) {
        this.key = Objects.requireNonNull(key, "Namespaced Key must not be null!");
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public ParticleEffect(ParticleEffect particleEffect) {
        this.key = particleEffect.getNamespacedKey();
        this.particle = particleEffect.getParticle();
        this.name = particleEffect.getName();
        this.description = particleEffect.getDescription();
        this.icon = particleEffect.getIcon();

        this.dataClass = particleEffect.dataClass;
        this.data = particleEffect.data;
        this.offset = particleEffect.getOffset();
        this.count = particleEffect.getCount();
        this.speed = particleEffect.getSpeed();
        this.animator = particleEffect.animator;
    }

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
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

    public void setParticle(org.bukkit.Particle particle) {
        this.particle = particle;
        this.dataClass = particle.getDataType();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Class<?> getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class<?> dataClass) {
        this.dataClass = dataClass;
    }

    public Vector getOffset() {
        return offset;
    }

    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public TimeSupplier getTimeSupplier() {
        return timeSupplier;
    }

    public void setTimeSupplier(TimeSupplier timeSupplier) {
        this.timeSupplier = timeSupplier;
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
                ", particle=" + particle +
                ", name='" + name + '\'' +
                ", description=" + description +
                ", icon=" + icon +
                ", dataClass=" + dataClass +
                ", data=" + data +
                ", offset=" + offset +
                ", count=" + count +
                ", speed=" + speed +
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
        private final TimeSupplier.Runner runner = timeSupplier.createRunner();

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
