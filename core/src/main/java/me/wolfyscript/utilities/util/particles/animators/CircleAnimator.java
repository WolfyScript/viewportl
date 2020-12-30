package me.wolfyscript.utilities.util.particles.animators;

import me.wolfyscript.utilities.util.particles.Animator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class CircleAnimator extends Animator {

    private final int radius;

    public CircleAnimator() {
        super(false);
        this.radius = 1;
    }

    public CircleAnimator(boolean useEyeLocation, int radius) {
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
        int inc = 360 / data.getCount();
        double bx = location.getX();
        double y = location.getY();
        double bz = location.getZ();
        World w = location.getWorld();

        for (double i = 0.0; i < 360.0D; i += inc) {
            double angle = i * Math.PI / 180.0D;
            double x = bx + (double) this.radius * Math.cos(angle);
            double z = bz + (double) this.radius * Math.sin(angle);
            Location l = new Location(w, x, y, z);
            spawn(l, data);
        }
    }

    @Override
    public String toString() {
        return "CircleAnimator{" +
                "radius=" + radius +
                "} " + super.toString();
    }
}
