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

package com.wolfyscript.utilities.nbt;


import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.json.annotations.KeyedBaseType;

@KeyedBaseType(baseType = NBTTagConfig.class)
public abstract class NBTTagConfigPrimitive<VAL> extends NBTTagConfig {

    protected final ValueProvider<VAL> value;

    @JsonCreator
    protected NBTTagConfigPrimitive(@JacksonInject WolfyUtils wolfyUtils, ValueProvider<VAL> value) {
        super(wolfyUtils);
        this.value = value;
    }

    protected NBTTagConfigPrimitive(WolfyUtils wolfyUtils, NBTTagConfig parent, ValueProvider<VAL> value) {
        super(wolfyUtils, parent);
        this.value = value;
    }

    protected NBTTagConfigPrimitive(NBTTagConfigPrimitive<VAL> other) {
        super(other.wolfyUtils);
        this.value = other.value;
    }

    public ValueProvider<VAL> getValue() {
        return value;
    }
}
