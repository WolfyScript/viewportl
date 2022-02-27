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
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;

public class ValueComparatorAnd extends ValueComparator<ValueComparator<?>> {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("comparator/and");

    public ValueComparatorAnd(ValueProvider<ValueComparator<?>> valueThis, ValueProvider<ValueComparator<?>> valueThat) {
        super(valueThis, valueThat);
    }

    @Override
    public boolean evaluate() {
        return valueThis.getValue().evaluate() && valueThat.getValue().evaluate();
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return null;
    }
}
