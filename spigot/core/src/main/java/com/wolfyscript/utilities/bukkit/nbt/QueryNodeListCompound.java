package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.List;

@KeyedStaticId(key = "list/compound")
public class QueryNodeListCompound extends QueryNodeList<NBTCompound> {

    public QueryNodeListCompound(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTCompound>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(wolfyUtils, elements, key, path, NBTType.NBTTagCompound, NBTCompound.class);
    }

    private QueryNodeListCompound(QueryNodeListCompound other) {
        super(other);
    }

    @Override
    public QueryNodeListCompound copy() {
        return new QueryNodeListCompound(this);
    }
}
