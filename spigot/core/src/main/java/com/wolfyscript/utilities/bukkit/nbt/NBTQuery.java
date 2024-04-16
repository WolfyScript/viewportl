/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
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

package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.config.jackson.JacksonUtil;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Override the QueryNode settings, as this class is not registered!
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE, defaultImpl = NBTQuery.class)
public class NBTQuery extends QueryNodeCompound {

    @JsonCreator
    public NBTQuery(@JacksonInject WolfyUtils wolfyUtils) {
        super(wolfyUtils, "", "");
        this.nbtType = NBTType.NBTTagCompound;
        this.includes = new HashMap<>();
        this.required = new HashMap<>();
        this.children = new HashMap<>();
    }

    private NBTQuery(NBTQuery other) {
        super(other);
        this.children = other.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().copy()));
    }

    @JsonAnySetter
    public void loadNonNestedChildren(String key, JsonNode node) {
        // Sets the children that are specified in the root of the object without the "children" node!
        // That is supported behaviour!
        QueryNode.loadFrom(node, "", key).ifPresent(queryNode -> children.putIfAbsent(key, queryNode));
    }

    public static Optional<NBTQuery> of(File file) {
        try {
            return Optional.ofNullable(JacksonUtil.getObjectMapper().readValue(file, NBTQuery.class));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<NBTQuery> of(File file, WolfyUtils wolfyUtils) {
        try {
            return Optional.ofNullable(wolfyUtils.getJacksonMapperUtil().getGlobalMapper().readValue(file, NBTQuery.class));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public NBTCompound run(NBTCompound input) {
        return run(input, new EvalContext());
    }

    public NBTCompound run(NBTCompound input, EvalContext context) {
        NBTContainer container = new NBTContainer();
        // Start at the root of the container
        applyChildrenToCompound("", context, input, container);
        return container;
    }

    @Override
    public NBTQuery copy() {
        return new NBTQuery(this);
    }


}
