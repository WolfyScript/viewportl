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

package me.wolfyscript.utilities.util.value_comparators;

import me.wolfyscript.utilities.util.NamespacedKey;

public class ValueComparatorOr implements ValueComparator<ValueComparator<?>> {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("or");

    private final ValueComparator<?> value;
    private final ValueComparator<?> other;

    public ValueComparatorOr(ValueComparator<?> value, ValueComparator<?> other) {
        this.value = value;
        this.other = other;
    }

    @Override
    public boolean evaluate() {
        return value.evaluate() || other.evaluate();
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return null;
    }
}
