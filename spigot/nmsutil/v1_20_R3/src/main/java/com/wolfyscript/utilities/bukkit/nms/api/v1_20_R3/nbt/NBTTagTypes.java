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

package com.wolfyscript.utilities.bukkit.nms.api.v1_20_R3.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTBase;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
import net.minecraft.nbt.Tag;

public class NBTTagTypes {

    private static final NBTTagType<?>[] types = new NBTTagType<?>[]{
            NBTTagEndImpl.TYPE,
            NBTTagByteImpl.TYPE,
            NBTTagShortImpl.TYPE,
            NBTTagIntImpl.TYPE,
            NBTTagLongImpl.TYPE,
            NBTTagFloatImpl.TYPE,
            NBTTagDoubleImpl.TYPE,
            NBTTagByteArrayImpl.TYPE,
            NBTTagStringImpl.TYPE,
            NBTTagListImpl.TYPE,
            NBTTagCompoundImpl.TYPE,
            NBTTagIntArrayImpl.TYPE,
            NBTTagLongArrayImpl.TYPE
    };

    public static NBTTagType<?> of(int typeId) {
        return typeId >= 0 && typeId < types.length ? types[typeId] : NBTTagType.invalidType(typeId);
    }

    public static NBTBase convert(Tag base) {
        return base != null ? NBTTagTypes.of(base.getId()).get(base) : null;
    }
}
