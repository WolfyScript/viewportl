package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.List;

@KeyedStaticId(key = "list/float")
public class QueryNodeListFloat extends QueryNodeList<Float> {

    public QueryNodeListFloat(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<Float>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(wolfyUtils, elements, key, path, NBTType.NBTTagByte, Float.class);
    }

    public QueryNodeListFloat(QueryNodeList<Float> other) {
        super(other);
    }

    @Override
    public QueryNodeListFloat copy() {
        return new QueryNodeListFloat(this);
    }
}
