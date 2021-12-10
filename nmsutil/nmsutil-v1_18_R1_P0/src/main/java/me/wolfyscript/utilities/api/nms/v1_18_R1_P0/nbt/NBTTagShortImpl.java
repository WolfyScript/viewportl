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

package me.wolfyscript.utilities.api.nms.v1_18_R1_P0.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagShort;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.ShortTag;

public class NBTTagShortImpl extends NBTNumberImpl<ShortTag> implements NBTTagShort {

    public static final NBTTagType<NBTTagShort> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.SHORT, nbtBase -> new NBTTagShortImpl((ShortTag) nbtBase));

    private NBTTagShortImpl() {
        super(ShortTag.valueOf((short) 0));
    }

    NBTTagShortImpl(ShortTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagShort of(short value) {
        return new NBTTagShortImpl(ShortTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagShort> getType() {
        return TYPE;
    }
}
