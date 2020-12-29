package me.wolfyscript.utilities.util.particles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;

public abstract class StepAnimator extends Animator {

    @JsonIgnore
    protected final StepScheduler.LocationStep locationStep;
    @JsonIgnore
    protected final StepScheduler.BlockStep blockStep;
    @JsonIgnore
    protected final StepScheduler.EntityStep entityStep;
    @JsonIgnore
    protected final StepScheduler.PlayerStep playerStep;
    private final int duration;
    private final int interval;

    public StepAnimator(boolean useEyeLocation, StepScheduler.LocationStep locationStep, StepScheduler.BlockStep blockStep, StepScheduler.EntityStep entityStep, StepScheduler.PlayerStep playerStep, int duration, int interval) {
        super(useEyeLocation);
        this.duration = duration;
        this.interval = interval;
        this.locationStep = locationStep;
        this.blockStep = blockStep;
        this.entityStep = entityStep;
        this.playerStep = playerStep;
    }

    @Override
    public void onPlayer(Data particleData, Player player, EquipmentSlot equipmentSlot) {
        new StepScheduler(particleData, useEyeLocation ? player.getEyeLocation() : player.getLocation(), player, equipmentSlot, playerStep, duration, interval);
    }

    @Override
    public void onEntity(Data particleData, Entity entity) {
        new StepScheduler(particleData, entity.getLocation(), entity, entityStep, duration, interval);
    }

    @Override
    public void onLocation(Data particleData, Location location) {
        new StepScheduler(particleData, location, locationStep, duration, interval);
    }

    @Override
    public void onBlock(Data particleData, Block block) {
        new StepScheduler(particleData, block.getLocation(), block, blockStep, duration, interval);
    }

    static class StepScheduler implements Runnable {

        private final Animator.Data data;
        private final Location location;
        private final Block block;
        private final Player player;
        private final Entity entity;
        private final EquipmentSlot equipmentSlot;

        private final int interval;
        private final int duration;
        private final StepScheduler.Step step;
        private final BukkitTask task;
        private int iteration = 0;

        public StepScheduler(Animator.Data data, Location location, StepScheduler.LocationStep step, int duration, int interval) {
            this(data, location, null, null, null, null, duration, interval, step);
        }

        public StepScheduler(Animator.Data data, Location location, Block block, StepScheduler.BlockStep step, int duration, int interval) {
            this(data, location, block, null, null, null, duration, interval, step);
        }

        public StepScheduler(Animator.Data data, Location location, Player player, EquipmentSlot equipmentSlot, StepScheduler.PlayerStep step, int duration, int interval) {
            this(data, location, null, player, null, equipmentSlot, duration, interval, step);
        }

        public StepScheduler(Animator.Data data, Location location, Entity entity, StepScheduler.EntityStep step, int duration, int interval) {
            this(data, location, null, null, entity, null, duration, interval, step);
        }

        private StepScheduler(Animator.Data data, Location location, Block block, Player player, Entity entity, EquipmentSlot equipmentSlot, int duration, int interval, StepScheduler.Step step) {
            this.data = data;
            this.location = location;
            this.block = block;
            this.player = player;
            this.entity = entity;
            this.equipmentSlot = equipmentSlot;
            this.duration = duration;
            this.interval = interval;
            this.step = step;
            this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(WolfyUtilities.getWUPlugin(), this, 0, interval);
        }

        @Override
        public void run() {
            if (this.iteration * this.interval > this.duration) {
                this.task.cancel();
            } else {
                ++this.iteration;
                if (step != null) {
                    step.run(duration, interval, iteration, data, location, block, player, entity, equipmentSlot);
                }
            }
        }

        public interface Step {

            void run(int duration, int interval, int iteration, Animator.Data data, Location location, Block block, Player player, Entity entity, EquipmentSlot equipmentSlot);

        }

        public interface LocationStep extends StepScheduler.Step {

            @Override
            default void run(int duration, int interval, int iteration, Animator.Data data, Location location, Block block, Player player, Entity entity, EquipmentSlot equipmentSlot) {
                this.run(duration, interval, iteration, data, location);
            }

            void run(int duration, int interval, int iteration, Animator.Data data, Location location);
        }

        public interface BlockStep extends StepScheduler.Step {

            @Override
            default void run(int duration, int interval, int iteration, Animator.Data data, Location location, Block block, Player player, Entity entity, EquipmentSlot equipmentSlot) {
                this.run(duration, interval, iteration, data, location, block);
            }

            void run(int duration, int interval, int iteration, Animator.Data data, Location location, Block block);

        }

        public interface PlayerStep extends StepScheduler.Step {

            @Override
            default void run(int duration, int interval, int iteration, Animator.Data data, Location location, Block block, Player player, Entity entity, EquipmentSlot equipmentSlot) {
                this.run(duration, interval, iteration, data, location, player, equipmentSlot);
            }

            void run(int duration, int interval, int iteration, Animator.Data data, Location location, Player player, EquipmentSlot equipmentSlot);

        }

        public interface EntityStep extends StepScheduler.Step {

            @Override
            default void run(int duration, int interval, int iteration, Animator.Data data, Location location, Block block, Player player, Entity entity, EquipmentSlot equipmentSlot) {
                this.run(duration, interval, iteration, data, location, entity);
            }

            void run(int duration, int interval, int iteration, Animator.Data data, Location location, Entity entity);
        }
    }

}
