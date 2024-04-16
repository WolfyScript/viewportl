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

package com.wolfyscript.utilities.bukkit.nms.api;

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

@Deprecated(since = "4.16.2.0")
public abstract class NBTTag {

    public abstract NBTTagEnd end();

    public abstract NBTCompound compound();

    public abstract NBTTagList list();

    public abstract NBTTagByte ofByte(byte value);

    public NBTTagByte ofByte(boolean value) {
        return value ? ofByte((byte) 1) : ofByte((byte) 0);
    }

    public abstract NBTTagByteArray ofByteArray(byte[] value);

    public abstract NBTTagDouble ofDouble(double value);

    public abstract NBTTagFloat ofFloat(float value);

    public abstract NBTTagInt ofInt(int value);

    public abstract NBTTagIntArray ofIntArray(int[] array);

    public abstract NBTTagLong ofLong(long value);

    public abstract NBTTagLongArray ofLongArray(long[] array);

    public abstract NBTTagShort ofShort(short value);

    public abstract NBTTagString ofString(String value);

}
