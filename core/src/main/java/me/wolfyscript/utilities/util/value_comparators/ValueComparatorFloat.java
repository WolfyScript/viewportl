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

public class ValueComparatorFloat extends ValueComparatorNumber<Float> {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("comparator/float");

    public ValueComparatorFloat(ValueProvider<Float> valueThis, ValueProvider<Float> valueThat, Operator operator) {
        super(valueThis, valueThat, operator);
    }

    @Override
    public boolean evaluate() {
        return evaluateByOrder(this.valueThis.getValue().compareTo(this.valueThat.getValue()));
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return KEY;
    }
}
