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

package me.wolfyscript.utilities.util.value_providers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.context.EvalContext;

public abstract class ValueProviderVariable<V> extends AbstractValueProvider<V> {

    @JsonProperty("var")
    private final String variable;
    @JsonIgnore
    private final Class<V> typeClass;

    protected ValueProviderVariable(NamespacedKey key, Class<V> typeClass, String variable) {
        super(key);
        this.typeClass = typeClass;
        this.variable = variable;
    }

    @Override
    public V getValue(EvalContext context) {
        return typeClass.cast(context.getVariable(variable));
    }

}
