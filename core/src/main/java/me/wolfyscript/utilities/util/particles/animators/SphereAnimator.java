package me.wolfyscript.utilities.util.particles.animators;

import me.wolfyscript.utilities.util.RandomUtils;
import me.wolfyscript.utilities.util.particles.Animator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

public class SphereAnimator extends Animator {

    private final int radius;

    public SphereAnimator() {
        super(false);
        this.radius = 1;
    }

    public SphereAnimator(boolean useEyeLocation, int radius) {
        super(useEyeLocation);
        this.radius = radius;
    }

    @Override
    protected void onPlayer(Data particleData, Player player, EquipmentSlot equipmentSlot) {
        spawnCircleParticle(useEyeLocation ? player.getEyeLocation() : player.getLocation(), particleData);
    }

    @Override
    protected void onEntity(Data particleData, Entity entity) {
        spawnCircleParticle(entity.getLocation(), particleData);
    }

    @Override
    protected void onLocation(Data particleData, Location location) {
        spawnCircleParticle(location, particleData);
    }

    @Override
    protected void onBlock(Data particleData, Block block) {
        spawnCircleParticle(block.getLocation(), particleData);
    }

    private void spawnCircleParticle(Location location, Data data) {
        for (int i = 0; i < data.getCount(); i++) {
            Vector vector = RandomUtils.getRandomVector().multiply(this.radius);
            location.add(vector);
            spawn(location, data);
            location.subtract(vector);
        }
    }

    @Override
    public String toString() {
        return "SphereAnimator{" +
                "radius=" + radius +
                "} " + super.toString();
    }
}
