package me.wolfyscript.utilities.util.particles;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;

public abstract class StepAnimator extends Animator {

    protected final int duration;
    protected final int interval;

    public StepAnimator(boolean useEyeLocation, int duration, int interval) {
        super(useEyeLocation);
        this.duration = duration;
        this.interval = interval;
    }

    @Override
    public void onPlayer(Data particleData, Player player, EquipmentSlot equipmentSlot) {
        new StepScheduler(this, particleData, useEyeLocation ? player.getEyeLocation() : player.getLocation(), player, equipmentSlot);
    }

    @Override
    public void onEntity(Data particleData, Entity entity) {
        new StepScheduler(this, particleData, entity.getLocation());
    }

    @Override
    public void onLocation(Data particleData, Location location) {
        new StepScheduler(this, particleData, location);
    }

    @Override
    public void onBlock(Data particleData, Block block) {
        new StepScheduler(this, particleData, block.getLocation(), block);
    }

    protected abstract void runStep(int iteration, Animator.Data data, Location location, Block block, Player player, Entity entity, EquipmentSlot equipmentSlot);


    static class StepScheduler implements Runnable {

        private final Animator.Data data;
        private final Location location;
        private final Block block;
        private final Player player;
        private final Entity entity;
        private final EquipmentSlot equipmentSlot;

        private final StepAnimator stepAnimator;
        private final BukkitTask task;
        private int iteration = 0;

        public StepScheduler(StepAnimator stepAnimator, Animator.Data data, Location location) {
            this(stepAnimator, data, location, null, null, null, null);
        }

        public StepScheduler(StepAnimator stepAnimator, Animator.Data data, Location location, Block block) {
            this(stepAnimator, data, location, block, null, null, null);
        }

        public StepScheduler(StepAnimator stepAnimator, Animator.Data data, Location location, Player player, EquipmentSlot equipmentSlot) {
            this(stepAnimator, data, location, null, player, null, equipmentSlot);
        }

        public StepScheduler(StepAnimator stepAnimator, Animator.Data data, Location location, Entity entity) {
            this(stepAnimator, data, location, null, null, entity, null);
        }

        private StepScheduler(StepAnimator stepAnimator, Animator.Data data, Location location, Block block, Player player, Entity entity, EquipmentSlot equipmentSlot) {
            this.data = data;
            this.location = location;
            this.block = block;
            this.player = player;
            this.entity = entity;
            this.equipmentSlot = equipmentSlot;
            this.stepAnimator = stepAnimator;
            this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(WolfyUtilities.getWUPlugin(), this, 0, stepAnimator.interval);
        }

        @Override
        public void run() {
            if (this.iteration > stepAnimator.duration) {
                this.task.cancel();
            } else {
                stepAnimator.runStep(++this.iteration, data, location, block, player, entity, equipmentSlot);
            }
        }
    }

}
