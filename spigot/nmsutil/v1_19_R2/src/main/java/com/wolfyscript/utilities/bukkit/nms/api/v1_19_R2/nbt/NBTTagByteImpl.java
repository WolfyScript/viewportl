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

package com.wolfyscript.utilities.bukkit.nms.api.v1_19_R2.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagByte;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
import net.minecraft.nbt.ByteTag;

public class NBTTagByteImpl extends NBTNumberImpl<ByteTag> implements NBTTagByte {

    public static final NBTTagType<NBTTagByte> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.BYTE, nbtBase -> new NBTTagByteImpl((ByteTag) nbtBase));

    private NBTTagByteImpl() {
        super(ByteTag.ZERO);
    }

    NBTTagByteImpl(ByteTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagByte of(byte value) {
        return new NBTTagByteImpl(ByteTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagByte> getType() {
        return TYPE;
    }
}
