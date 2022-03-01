/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.api.nms.v1_18_R1.block;

import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.bukkit.block.BrewingStand;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBrewingStand;

public class NMSBrewingStand extends CraftBrewingStand implements me.wolfyscript.utilities.api.nms.block.NMSBrewingStand {

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
