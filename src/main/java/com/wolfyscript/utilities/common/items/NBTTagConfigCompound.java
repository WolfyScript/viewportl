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

package com.wolfyscript.utilities.common.items;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.HashMap;
import java.util.Map;

@KeyedStaticId(key = "compound")
public class NBTTagConfigCompound extends NBTTagConfig {

    private final Map<String, NBTTagConfig> children;

    public NBTTagConfigCompound(WolfyUtils wolfyUtils, @JsonProperty("children") Map<String, NBTTagConfig> childConfigs) {
        super(wolfyUtils);
        this.children = childConfigs == null ? new HashMap<>() : childConfigs;
    }

    @JsonAnySetter
    public void setRootEntries(String key, NBTTagConfig tagConfig) {
        children.put(key, tagConfig);
    }

}
