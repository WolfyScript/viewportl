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

package com.wolfyscript.utilities.bukkit.nms.api.nbt;

import org.jetbrains.annotations.Nullable;

@Deprecated(since = "4.16.2.0")
public interface NBTTagType<T extends NBTBase> {

    static NBTTagType<NBTTagEnd> invalidType(int typeId) {
        return new NBTTagType<>() {
            @Override
            public NBTTagEnd get(Object nbtBase) {
                throw new IllegalArgumentException("Invalid tag id: " + typeId);
            }

            @Override
            public Type getType() {
                return Type.TAG_END;
            }
        };
    }

    T get(@Nullable Object nbtBase);

    Type getType();

    enum Type{

        TAG_END,
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BYTE_ARRAY,
        STRING,
        LIST,
        COMPOUND,
        INT_ARRAY,
        LONG_ARRAY;

        public static Type of(int typeId) {
            if (typeId >= 0 && typeId < values().length) {
                return Type.values()[typeId];
            }
            throw new IllegalArgumentException("Invalid tag id: " + typeId);
        }

        public boolean is(int typeId) {
            return this.ordinal() == typeId;
        }

        public int getId() {
            return this.ordinal();
        }
    }

}
