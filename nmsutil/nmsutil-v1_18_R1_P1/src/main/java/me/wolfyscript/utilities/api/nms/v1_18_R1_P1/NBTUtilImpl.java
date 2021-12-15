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

package me.wolfyscript.utilities.api.nms.v1_18_R1_P1;

import me.wolfyscript.utilities.api.nms.NBTUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.nbt.NBTItem;
import me.wolfyscript.utilities.api.nms.v1_18_R1_P1.nbt.NBTItemImpl;
import org.bukkit.inventory.ItemStack;

public class NBTUtilImpl extends NBTUtil {

    protected NBTUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
        this.nbtTag = new NBTTagImpl();
    }

    @Override
    public NBTItem getItem(ItemStack bukkitItemStack) {
        return new NBTItemImpl(bukkitItemStack, false);
    }

    @Override
    public NBTItem getDirectItem(ItemStack bukkitItemStack) {
        return new NBTItemImpl(bukkitItemStack, true);
    }

}
