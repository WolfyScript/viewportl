package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.List;

@KeyedStaticId(key = "list/long")
public class QueryNodeListLong extends QueryNodeList<Long> {

    public QueryNodeListLong(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<Long>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(wolfyUtils, elements, key, path, NBTType.NBTTagByte, Long.class);
    }

    public QueryNodeListLong(QueryNodeList<Long> other) {
        super(other);
    }

    @Override
    public QueryNodeListLong copy() {
        return new QueryNodeListLong(this);
    }
}
