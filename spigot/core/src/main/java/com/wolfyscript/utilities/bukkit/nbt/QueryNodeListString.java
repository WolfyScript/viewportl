package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.List;

@KeyedStaticId(key = "list/string")
public class QueryNodeListString extends QueryNodeList<String> {

    public QueryNodeListString(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<String>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(wolfyUtils, elements, key, path, NBTType.NBTTagByte, String.class);
    }

    public QueryNodeListString(QueryNodeList<String> other) {
        super(other);
    }

    @Override
    public QueryNodeListString copy() {
        return new QueryNodeListString(this);
    }
}
