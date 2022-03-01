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

package me.wolfyscript.utilities.api.nms.v1_18_R2.nbt;

import me.wolfyscript.utilities.api.nms.nbt.NBTTagInt;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagType;
import net.minecraft.nbt.IntTag;

public class NBTTagIntImpl extends NBTNumberImpl<IntTag> implements NBTTagInt {

    public static final NBTTagType<NBTTagInt> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.INT, nbtBase -> new NBTTagIntImpl((IntTag) nbtBase));

    private NBTTagIntImpl() {
        super(IntTag.valueOf(0));
    }

    NBTTagIntImpl(IntTag nbtBase) {
        super(nbtBase);
    }

    public static NBTTagInt of(int value) {
        return new NBTTagIntImpl(IntTag.valueOf(value));
    }

    @Override
    public NBTTagType<NBTTagInt> getType() {
        return TYPE;
    }
}
