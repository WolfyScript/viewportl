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

package me.wolfyscript.utilities.api.nms.nbt;

public interface NBTTagList extends NBTBase {

    NBTCompound getCompound(int index);

    NBTTagList getTagList(int index);

    NBTBase getTag(int index);

    NBTBase remove(int index);

    short getShort(int index);

    int getInt(int index);

    int[] getIntArray(int index);

    double getDouble(int index);

    float getFloat(int index);

    String getString(int index);

    int size();

    NBTBase set(int index, NBTBase value);

    void add(int index, NBTBase value);

    void clear();

}
