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

@KeyedStaticId(key = "list/int")
public class NBTTagConfigListInt extends NBTTagConfigListPrimitive<Integer, NBTTagConfigInt> {

    @JsonCreator
    NBTTagConfigListInt(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("values") List<NBTTagConfigInt> elements) {
        super(wolfyUtils, elements, NBTTagConfigInt.class);
    }

    public NBTTagConfigListInt(WolfyUtils wolfyUtils, NBTTagConfig parent,  List<NBTTagConfigInt> elements) {
        super(wolfyUtils, parent, NBTTagConfigInt.class, elements);
    }

    public NBTTagConfigListInt(NBTTagConfigList<NBTTagConfigInt> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListInt copy() {
        return new NBTTagConfigListInt(this);
    }
}
