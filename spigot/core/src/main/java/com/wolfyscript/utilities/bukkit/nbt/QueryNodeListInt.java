package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.List;

@KeyedStaticId(key = "list/int")
public class QueryNodeListInt extends QueryNodeList<Integer> {

    public QueryNodeListInt(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<Integer>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(wolfyUtils, elements, key, path, NBTType.NBTTagByte, Integer.class);
    }

    public QueryNodeListInt(QueryNodeList<Integer> other) {
        super(other);
    }

    @Override
    public QueryNodeListInt copy() {
        return new QueryNodeListInt(this);
    }
}
