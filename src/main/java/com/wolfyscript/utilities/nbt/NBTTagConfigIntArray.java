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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

@KeyedStaticId(key = "int_array")
public class NBTTagConfigIntArray extends NBTTagConfigPrimitive<int[]> {

    @JsonCreator
    NBTTagConfigIntArray(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<int[]> value) {
        super(wolfyUtils, value);
    }

    public NBTTagConfigIntArray(WolfyUtils wolfyUtils, NBTTagConfig parent, ValueProvider<int[]> value) {
        super(wolfyUtils, parent, value);
    }

    public NBTTagConfigIntArray(NBTTagConfigIntArray other) {
        super(other.wolfyUtils, other.value);
    }

    @Override
    public NBTTagConfigIntArray copy() {
        return new NBTTagConfigIntArray(this);
    }

}
