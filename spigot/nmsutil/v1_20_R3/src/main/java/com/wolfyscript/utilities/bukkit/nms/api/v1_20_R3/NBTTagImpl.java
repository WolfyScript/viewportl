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

package com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3;

import com.wolfyscript.utilities.bukkit.nms.api.NBTTag;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTCompound;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagByte;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagByteArray;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagDouble;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagEnd;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagFloat;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagInt;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagIntArray;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagList;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagLong;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagLongArray;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagShort;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagString;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagByteArrayImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagByteImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagCompoundImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagDoubleImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagEndImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagFloatImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagIntArrayImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagIntImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagListImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagLongArrayImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagLongImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagShortImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt.NBTTagStringImpl;

public class NBTTagImpl extends NBTTag {

    @Override
    public NBTTagEnd end() {
        return NBTTagEndImpl.of();
    }

    @Override
    public NBTCompound compound() {
        return NBTTagCompoundImpl.of();
    }

    @Override
    public NBTTagList list() {
        return NBTTagListImpl.of();
    }

    @Override
    public NBTTagByte ofByte(byte value) {
        return NBTTagByteImpl.of(value);
    }

    @Override
    public NBTTagByteArray ofByteArray(byte[] value) {
        return NBTTagByteArrayImpl.of(value);
    }

    @Override
    public NBTTagDouble ofDouble(double value) {
        return NBTTagDoubleImpl.of(value);
    }

    @Override
    public NBTTagFloat ofFloat(float value) {
        return NBTTagFloatImpl.of(value);
    }

    @Override
    public NBTTagInt ofInt(int value) {
        return NBTTagIntImpl.of(value);
    }

    @Override
    public NBTTagIntArray ofIntArray(int[] array) {
        return NBTTagIntArrayImpl.of(array);
    }

    @Override
    public NBTTagLong ofLong(long value) {
        return NBTTagLongImpl.of(value);
    }

    @Override
    public NBTTagLongArray ofLongArray(long[] array) {
        return NBTTagLongArrayImpl.of(array);
    }

    @Override
    public NBTTagShort ofShort(short value) {
        return NBTTagShortImpl.of(value);
    }

    @Override
    public NBTTagString ofString(String value) {
        return NBTTagStringImpl.of(value);
    }
}
