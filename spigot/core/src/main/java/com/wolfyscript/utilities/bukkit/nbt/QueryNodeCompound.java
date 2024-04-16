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
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.eval.operator.BoolOperatorConst;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@KeyedStaticId(key = "compound")
public class QueryNodeCompound extends QueryNode<NBTCompound> {

    protected boolean preservePath = true;
    //If include is true it includes this node with each and every child node.
    protected boolean includeAll = false;
    //If includes has values it includes this node with the specified child nodes.
    protected Map<String, Boolean> includes;
    //Checks and verifies the child nodes. This node is only included if all the child nodes are valid.
    protected Map<String, QueryNode<?>> required;
    //Child nodes to proceed to next. This is useful for further child compound tag settings.
    @JsonIgnore
    protected Map<String, QueryNode<?>> children;

    public QueryNodeCompound(@JacksonInject WolfyUtils wolfyUtils, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, key, parentPath);
        this.nbtType = NBTType.NBTTagCompound;
        this.includes = new HashMap<>();
        this.required = new HashMap<>();
        this.children = new HashMap<>();
    }

    protected QueryNodeCompound(QueryNodeCompound other) {
        super(other.wolfyUtils, other.key, other.parentPath);
        this.nbtType = NBTType.NBTTagCompound;
        this.includes = new HashMap<>(other.includes);
        this.preservePath = other.preservePath;
        this.includeAll = other.includeAll;
        this.required = other.required.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().copy()));
        this.children = other.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().copy()));
    }

    @JsonAnySetter
    public void loadNonNestedChildren(String key, JsonNode node) {
        //Sets the children that are specified in the root of the object without the "children" node!
        //That is supported behaviour!
        QueryNode.loadFrom(node, parentPath + "." + this.key, key).ifPresent(queryNode -> children.putIfAbsent(key, queryNode));
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
    public void setRequired(Map<String, QueryNode<?>> required) {
        this.required = required;
    }

    @JsonGetter
    public Map<String, QueryNode<?>> getRequired() {
        return required;
    }

    @JsonSetter("children")
    public void setChildren(Map<String, JsonNode> children) {
        this.children = children.entrySet().stream().map(entry -> QueryNode.loadFrom(entry.getValue(), parentPath + "." + this.key, entry.getKey()).map(queryNode -> Map.entry(entry.getKey(), queryNode)).orElse(null)).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonGetter
    public Map<String, QueryNode<?>> getChildren() {
        return children;
    }

    @Override
    public boolean check(String key, NBTType nbtType, EvalContext context, NBTCompound value) {
        return !value.getKeys().isEmpty();
    }

    @Override
    protected Optional<NBTCompound> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getCompound(key));
    }

    @Override
    public void applyValue(String path, String key, EvalContext context, NBTCompound value, NBTCompound resultContainer) {
        String newPath = path + "." + key;
        NBTCompound container = preservePath ? resultContainer.addCompound(key) : resultContainer;
        applyChildrenToCompound(newPath, context, value, container);
    }

    /**
     *
     *
     * @param containerPath The path of the current container.
     * @param value The value of the NBTCompound at the current path.
     * @param resultContainer The current container to apply the children to.
     */
    protected void applyChildrenToCompound(String containerPath, EvalContext context, NBTCompound value, NBTCompound resultContainer) {
        Set<String> keys;
        if (!includes.isEmpty()) {
            keys = value.getKeys().stream().filter(s -> includes.getOrDefault(s, includeAll)).collect(Collectors.toSet());
        } else {
            keys = value.getKeys().stream().filter(s -> getChildren().containsKey(s) || includeAll).collect(Collectors.toSet());
        }
        //Process child nodes with the specified settings.
        for (String childKey : keys) {
            QueryNode<?> subQueryNode = getChildren().get(childKey);
            if (subQueryNode != null) {
                subQueryNode.visit(containerPath, childKey, context, value, resultContainer);
            } else {
                QueryNodeBoolean node = new QueryNodeBoolean(wolfyUtils, new BoolOperatorConst(wolfyUtils, true), childKey, containerPath);
                node.visit(containerPath, childKey, context, value, resultContainer);
            }
        }
    }

    @Override
    public QueryNodeCompound copy() {
        return new QueryNodeCompound(this);
    }

}
