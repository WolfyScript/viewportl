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

package com.wolfyscript.utilities.bukkit.nms.api.v1_18_R1_P1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTBase;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class NBTTagTypeImpl<T extends NBTBase> implements NBTTagType<T> {

    protected final Type type;
    private final Function<Object, T> function;

    public NBTTagTypeImpl(Type type, Function<Object, T> nbtBase) {
        this.type = type;
        this.function = nbtBase;
    }

    public T get(@Nullable Object nbtBase) {
        if (nbtBase instanceof Tag && type.is(((Tag) nbtBase).getId())) {
            return function.apply(nbtBase);
        }
        return null;
    }

    @Override
    public Type getType() {
        return type;
    }

}
