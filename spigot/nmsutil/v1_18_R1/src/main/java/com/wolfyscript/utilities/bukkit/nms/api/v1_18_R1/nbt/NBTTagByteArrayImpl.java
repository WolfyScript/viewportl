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

package com.wolfyscript.utilities.bukkit.nms.api.v1_18_R1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagByteArray;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
import net.minecraft.nbt.ByteArrayTag;

public class NBTTagByteArrayImpl extends NBTBaseImpl<ByteArrayTag> implements NBTTagByteArray {

    public static final NBTTagType<NBTTagByteArray> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.BYTE_ARRAY, nbtBase -> new NBTTagByteArrayImpl((ByteArrayTag) nbtBase));

    NBTTagByteArrayImpl(ByteArrayTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagByteArray of(byte[] value) {
        return new NBTTagByteArrayImpl(new ByteArrayTag(value));
    }

    @Override
    public NBTTagType<NBTTagByteArray> getType() {
        return TYPE;
    }
}
