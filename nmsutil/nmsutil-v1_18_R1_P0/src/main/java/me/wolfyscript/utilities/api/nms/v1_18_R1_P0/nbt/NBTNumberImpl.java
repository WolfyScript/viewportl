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

public abstract class NBTNumberImpl<NBT extends net.minecraft.nbt.NumericTag> extends NBTBaseImpl<NBT> implements me.wolfyscript.utilities.api.nms.nbt.NBTNumber {

    NBTNumberImpl(NBT nbtBase) {
        super(nbtBase);
    }

    @Override
    public long asLong() {
        return nbt.getAsLong();
    }

    @Override
    public int asInt() {
        return nbt.getAsInt();
    }

    @Override
    public short asShort() {
        return nbt.getAsShort();
    }

    @Override
    public byte asByte() {
        return nbt.getAsByte();
    }

    @Override
    public double asDouble() {
        return nbt.getAsDouble();
    }

    @Override
    public float asFloat() {
        return nbt.getAsFloat();
    }
}
