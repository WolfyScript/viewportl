package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.List;

@KeyedStaticId(key = "list/double")
public class QueryNodeListDouble extends QueryNodeList<Double> {

    public QueryNodeListDouble(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<Double>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(wolfyUtils, elements, key, path, NBTType.NBTTagByte, Double.class);
    }

    public QueryNodeListDouble(QueryNodeList<Double> other) {
        super(other);
    }

    @Override
    public QueryNodeListDouble copy() {
        return new QueryNodeListDouble(this);
    }
}
