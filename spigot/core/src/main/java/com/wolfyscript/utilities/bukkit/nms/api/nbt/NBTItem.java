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

package com.wolfyscript.utilities.bukkit.nms.api.nbt;

import java.util.Set;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Deprecated(since = "4.16.2.0")
public abstract class NBTItem {

    protected final ItemStack bukkitItemStack;

    protected NBTItem(ItemStack bukkitItemStack, boolean directAccess) { //It looks like that directAccess isn't used, but it is used in implementations of this class!
        this.bukkitItemStack = bukkitItemStack;
    }

    /**
     * @return The original item that this NBTItem was created from.
     */
    public ItemStack getItemStack() {
        return bukkitItemStack;
    }

    /**
     * Creates a new Bukkit ItemStack with the updated NBT Tags
     *
     * @return new ItemsStack instance with new NBT tags added.
     */
    public abstract ItemStack create();

    /**
     * Sets the tag with the specific key to the new value.
     *
     * @param key     The key of the tag
     * @param nbtBase The NBT value.
     */
    public abstract void setTag(String key, NBTBase nbtBase);

    @Nullable
    public abstract NBTBase getTag(String key);

    public abstract NBTCompound getCompound(String key);

    public abstract NBTCompound getCompound();

    public abstract boolean hasKey(String key);

    public abstract boolean hasKeyOfType(String key, int typeId);

    public abstract Set<String> getKeys();


}
