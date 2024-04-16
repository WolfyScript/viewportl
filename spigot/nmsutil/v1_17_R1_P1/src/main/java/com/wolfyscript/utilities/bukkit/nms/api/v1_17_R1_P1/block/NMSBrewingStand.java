package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1_P1.block;

import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.bukkit.block.BrewingStand;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBrewingStand;

public class NMSBrewingStand extends CraftBrewingStand implements com.wolfyscript.utilities.bukkit.nms.api.block.NMSBrewingStand {

    public NMSBrewingStand(BrewingStand brewingStand) {
        super(brewingStand.getWorld(), (BrewingStandBlockEntity) ((CraftWorld)brewingStand.getWorld()).getHandle().getBlockEntity(((CraftBlock) brewingStand.getBlock()).getPosition()));
    }

    @Override
    public int getFuelLevel() {
        return getTileEntity().fuel;
    }

    @Override
    public void setFuelLevel(int level) {
        getTileEntity().fuel = level;
    }

    @Override
    public int getBrewingTime() {
        return getTileEntity().brewTime;
    }

    @Override
    public void setBrewingTime(int brewTime) {
        getTileEntity().brewTime = brewTime;
    }
}
