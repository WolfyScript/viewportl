package me.wolfyscript.utilities.api.utils.particles;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;

public class ParticleEffect {

    private List<Particle> particles = new ArrayList<>();

    private Material icon;
    private String name;
    private List<String> description;

    private String referencePath;

    private int count;
    private int duration;
    private int cooldown;

    public ParticleEffect() {

    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void setParticles(List<Particle> particles) {
        this.particles = particles;
    }

    void setReferencePath(String referencePath) {
        this.referencePath = referencePath;
    }

    public int getCount() {
        return count;
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

    public void setCount(int count) {
        this.count = count;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void addParticle(Particle particle){
        particles.add(particle);
    }

    void prepare(){
        for(Particle particle : particles){
            particle.prepare(referencePath);
        }
    }

    public void spawnOnLocation(Location location, int tick){
        for(Particle particle : getParticles()){
            particle.spawnOnLocation(location, tick);
        }
    }

    public void spawnOnBlock(Block block, int tick) {
        for(Particle particle : getParticles()){
            particle.spawnOnBlock(block, tick);
        }
    }

    public void spawnOnPlayer(Player player, EquipmentSlot slot, int tick) {
        for(Particle particle : particles){
            particle.spawnOnPlayer(player, slot, tick);
        }
    }

    public enum Action{
        LOCATION,
        BLOCK,
        CHEST,
        FEET,
        HAND,
        HEAD,
        LEGS,
        OFF_HAND
    }
}
