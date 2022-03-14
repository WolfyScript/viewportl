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

package me.wolfyscript.utilities.api.inventory.tags;

import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.HashSet;
import java.util.Set;

public class CustomTag<T extends Keyed> implements Keyed {

    private final NamespacedKey namespacedKey;

    protected final Set<T> values;

    public CustomTag(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
        this.values = new HashSet<>();
    }

    public Set<T> getValues() {
        return values;
    }

    public void add(T value) {
        values.add(value);
    }

    public void remove(T value) {
        values.remove(value);
    }

    public boolean contains(T value) {
        return values.contains(value);
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

}
