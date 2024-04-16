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

package com.wolfyscript.utilities.bukkit.nms.api.v1_18_R1_P1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTBase;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTCompound;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagList;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
import net.minecraft.nbt.ListTag;

public class NBTTagListImpl extends NBTBaseImpl<ListTag> implements NBTTagList {

    public static final NBTTagType<NBTTagList> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.LIST, nbtBase -> new NBTTagListImpl((ListTag) nbtBase));

    NBTTagListImpl(ListTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagList of() {
        return new NBTTagListImpl(new ListTag());
    }

    @Override
    public NBTTagType<NBTTagList> getType() {
        return TYPE;
    }

    @Override
    public NBTCompound getCompound(int index) {
        return new NBTTagCompoundImpl(nbt.getCompound(index));
    }

    @Override
    public NBTTagList getTagList(int index) {
        return new NBTTagListImpl(nbt.getList(index));
    }

    @Override
    public NBTBase getTag(int index) {
        return NBTTagTypes.convert(nbt.get(index));
    }

    @Override
    public NBTBase remove(int index) {
        return NBTTagTypes.convert(nbt.remove(index));
    }

    @Override
    public short getShort(int index) {
        return nbt.getShort(index);
    }

    @Override
    public int getInt(int index) {
        return nbt.getInt(index);
    }

    @Override
    public int[] getIntArray(int index) {
        return nbt.getIntArray(index);
    }

    @Override
    public double getDouble(int index) {
        return nbt.getDouble(index);
    }

    @Override
    public float getFloat(int index) {
        return nbt.getFloat(index);
    }

    @Override
    public String getString(int index) {
        return nbt.getString(index);
    }

    @Override
    public int size() {
        return nbt.size();
    }

    @Override
    public NBTBase set(int index, NBTBase value) {
        return NBTTagTypes.convert(nbt.set(index, ((NBTBaseImpl<?>) value).nbt));
    }

    @Override
    public void add(int index, NBTBase value) {
        nbt.add(index, ((NBTBaseImpl<?>) value).nbt);
    }

    @Override
    public void clear() {
        nbt.clear();
    }
}
