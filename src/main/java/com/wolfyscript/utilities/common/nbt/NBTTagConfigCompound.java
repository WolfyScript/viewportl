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

package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@KeyedStaticId(key = "compound")
public class NBTTagConfigCompound extends NBTTagConfig {

    protected boolean preservePath = true;
    //If include is true it includes this node with each and every child node.
    protected boolean includeAll = false;
    //If includes has values it includes this node with the specified child nodes.
    protected Map<String, Boolean> includes;
    //Checks and verifies the child nodes. This node is only included if all the child nodes are valid.
    protected Map<String, NBTTagConfig> required;
    //Child nodes to proceed to next. This is useful for further child compound tag settings.
    @JsonIgnore
    protected Map<String, NBTTagConfig> children;

    @JsonCreator
    NBTTagConfigCompound(@JacksonInject WolfyUtils wolfyUtils) {
        super(wolfyUtils);
        this.includes = new HashMap<>();
        this.required = new HashMap<>();
        this.children = new HashMap<>();
    }

    public NBTTagConfigCompound(WolfyUtils wolfyUtils, NBTTagConfig parent) {
        super(wolfyUtils, parent);
        this.includes = new HashMap<>();
        this.required = new HashMap<>();
        this.children = new HashMap<>();
    }

    protected NBTTagConfigCompound(NBTTagConfigCompound other) {
        super(other.wolfyUtils);
        this.includes = new HashMap<>(other.includes);
        this.preservePath = other.preservePath;
        this.includeAll = other.includeAll;
        this.required = other.required.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            NBTTagConfig value = entry.getValue().copy();
            value.setParent(this);
            return value;
        }));
        this.children = other.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            NBTTagConfig value = entry.getValue().copy();
            value.setParent(this);
            return value;
        }));
    }

    @JsonAnySetter
    public void loadNonNestedChildren(String key, NBTTagConfig child) {
        //Sets the children that are specified in the root of the object without the "children" node!
        //That is supported behaviour!
        children.putIfAbsent(key, child);
        child.setParent(this);
    }

    public void setIncludeAll(boolean fullyInclude) {
        this.includeAll = fullyInclude;
    }

    public boolean isIncludeAll() {
        return includeAll;
    }

    @JsonSetter("preservePath")
    public void setPreservePath(boolean preservePath) {
        this.preservePath = preservePath;
    }

    @JsonGetter("preservePath")
    public boolean isPreservePath() {
        return preservePath;
    }

    @JsonSetter
    public void setIncludes(Map<String, Boolean> includes) {
        this.includes = includes;
    }

    @JsonGetter
    public Map<String, Boolean> getIncludes() {
        return includes;
    }

    @JsonSetter
    public void setRequired(Map<String, NBTTagConfig> required) {
        this.required = required;
    }

    @JsonGetter
    public Map<String, NBTTagConfig> getRequired() {
        return required;
    }

    @JsonSetter("children")
    public void setChildren(Map<String, NBTTagConfig> children) {
        this.children = children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            NBTTagConfig value = entry.getValue();
            value.setParent(this);
            return value;
        }));
    }

    @JsonGetter
    public Map<String, NBTTagConfig> getChildren() {
        return children;
    }

    @Override
    public NBTTagConfigCompound copy() {
        return new NBTTagConfigCompound(this);
    }

}
