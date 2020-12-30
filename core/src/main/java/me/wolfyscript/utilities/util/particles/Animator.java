package me.wolfyscript.utilities.util.particles;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(StepAnimator.class)})
public abstract class Animator {

    protected boolean useEyeLocation;

    public Animator(boolean useEyeLocation) {
        this.useEyeLocation = useEyeLocation;
    }

    protected abstract void onPlayer(Data particleData, Player player, EquipmentSlot equipmentSlot);

    protected abstract void onEntity(Data particleData, Entity entity);

    protected abstract void onLocation(Data particleData, Location location);

    protected abstract void onBlock(Data particleData, Block block);

    protected void spawn(Location location, Data data) {
        if (location.getWorld() != null) {
            if (data.dataClass == null || !data.dataClass.isInstance(data.data)) {
                location.getWorld().spawnParticle(data.particleEffect.getParticle(), location.add(data.relative), data.count, data.offset.getX(), data.offset.getY(), data.offset.getZ(), data.speed);
            } else {
                location.getWorld().spawnParticle(data.particleEffect.getParticle(), location.add(data.relative), data.count, data.offset.getX(), data.offset.getY(), data.offset.getZ(), data.speed, data.data);
            }
        }
    }

    public static class Data {

        private final ParticleEffect particleEffect;
        private final Class<?> dataClass;
        private Object data;
        private Vector relative, offset;
        private int count;
        private double speed;

        public Data(ParticleEffect particleEffect) {
            this.particleEffect = particleEffect;
            this.dataClass = particleEffect.getDataClass();
            this.relative = new Vector(particleEffect.getRelative().getX(), particleEffect.getRelative().getY(), particleEffect.getRelative().getZ());
            this.offset = new Vector(particleEffect.getOffset().getX(), particleEffect.getOffset().getY(), particleEffect.getOffset().getZ());
            this.data = particleEffect.getData();
            this.count = particleEffect.getCount() == null ? 1 : particleEffect.getCount();
            this.speed = particleEffect.getSpeed() == null ? 1 : particleEffect.getSpeed();
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            if (dataClass.isInstance(data)) {
                this.data = data;
            }
        }

        public Vector getOffset() {
            return offset;
        }

        public void setOffset(Vector offset) {
            this.offset = offset;
        }

        public Vector getRelative() {
            return relative;
        }

        public void setRelative(Vector relative) {
            this.relative = relative;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

    }
}
