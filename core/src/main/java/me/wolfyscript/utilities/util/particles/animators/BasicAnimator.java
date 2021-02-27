package me.wolfyscript.utilities.util.particles.animators;

import me.wolfyscript.utilities.util.particles.Animator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class BasicAnimator extends Animator {

    public BasicAnimator() {
        super(false);
    }

    public BasicAnimator(boolean useEyeLocation) {
        super(useEyeLocation);
    }

    @Override
    protected void onPlayer(Data particleData, Player player, EquipmentSlot equipmentSlot) {
        spawn(useEyeLocation ? player.getEyeLocation() : player.getLocation(), particleData);
    }

    @Override
    protected void onEntity(Data particleData, Entity entity) {
        spawn(entity.getLocation(), particleData);
    }

    @Override
    protected void onLocation(Data particleData, Location location) {
        spawn(location, particleData);
    }

    @Override
    protected void onBlock(Data particleData, Block block) {
        spawn(block.getLocation(), particleData);
    }


}
