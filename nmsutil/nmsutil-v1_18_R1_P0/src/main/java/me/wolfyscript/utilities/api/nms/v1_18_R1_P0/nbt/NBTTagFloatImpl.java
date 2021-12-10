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

import me.wolfyscript.utilities.api.nms.nbt.NBTTagFloat;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.FloatTag;

public class NBTTagFloatImpl extends NBTNumberImpl<FloatTag> implements NBTTagFloat {

    public static final NBTTagType<NBTTagFloat> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.FLOAT, nbtBase -> new NBTTagFloatImpl((FloatTag) nbtBase));

    private NBTTagFloatImpl() {
        super(FloatTag.valueOf(0f));
    }

    NBTTagFloatImpl(FloatTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagFloat of(float value) {
        return new NBTTagFloatImpl(FloatTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagFloat> getType() {
        return TYPE;
    }
}
