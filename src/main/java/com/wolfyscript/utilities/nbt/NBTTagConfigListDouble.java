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
import java.util.List;

@KeyedStaticId(key = "list/double")
public class NBTTagConfigListDouble extends NBTTagConfigListPrimitive<Double, NBTTagConfigDouble> {

    @JsonCreator
    NBTTagConfigListDouble(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("values") List<NBTTagConfigDouble> elements) {
        super(wolfyUtils, elements, NBTTagConfigDouble.class);
    }

    public NBTTagConfigListDouble(WolfyUtils wolfyUtils, NBTTagConfig parent, List<NBTTagConfigDouble> elements) {
        super(wolfyUtils, parent, NBTTagConfigDouble.class, elements);
    }

    public NBTTagConfigListDouble(NBTTagConfigList<NBTTagConfigDouble> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListDouble copy() {
        return new NBTTagConfigListDouble(this);
    }
}
