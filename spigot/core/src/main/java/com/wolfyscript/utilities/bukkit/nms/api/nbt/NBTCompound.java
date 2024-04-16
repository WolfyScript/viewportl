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

import java.util.Set;
import org.jetbrains.annotations.Nullable;

@Deprecated(since = "4.16.2.0")
public interface NBTCompound extends NBTBase {

    Set<String> getKeys();

    int size();

    boolean hasKey(String key);

    boolean hasKeyOfType(String key, int typeId);

    @Nullable
    NBTBase set(String key, NBTBase nbtBase);

    void setByte(String key, byte value);

    void setBoolean(String key, boolean value);

    void setShort(String key, short value);

    void setInt(String key, int value);

    void setLong(String key, long value);

    void setFloat(String key, float value);

    void setDouble(String key, double value);

    void setString(String key, String value);

    void setByteArray(String key, byte[] value);

    void setIntArray(String key, int[] value);

    void setLongArray(String key, long[] value);

    @Nullable
    NBTBase get(String key);

    byte getByte(String key);

    boolean getBoolean(String key);

    short getShort(String key);

    int getInt(String key);

    long getLong(String key);

    float getFloat(String key);

    double getDouble(String key);

    String getString(String key);

    byte[] getByteArray(String key);

    int[] getIntArray(String key);

    long[] getLongArray(String key);

    NBTCompound getCompound(String key);

}
