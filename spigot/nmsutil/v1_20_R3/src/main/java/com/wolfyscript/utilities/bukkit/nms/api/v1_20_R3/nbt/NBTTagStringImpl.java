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

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagString;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
import net.minecraft.nbt.StringTag;

public class NBTTagStringImpl extends NBTBaseImpl<StringTag> implements NBTTagString {

    public static final NBTTagType<NBTTagString> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.STRING, nbtBase -> new NBTTagStringImpl((StringTag) nbtBase));

    NBTTagStringImpl(StringTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagString of(String value) {
        return new NBTTagStringImpl(StringTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagString> getType() {
        return TYPE;
    }

    @Override
    public String asString() {
        return nbt.getAsString();
    }

    @Override
    public String toString() {
        return nbt.toString();
    }
}
