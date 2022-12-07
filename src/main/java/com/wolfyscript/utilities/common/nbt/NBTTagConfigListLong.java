package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/long")
public class NBTTagConfigListLong extends NBTTagConfigListPrimitive<Long, NBTTagConfigLong> {

    public NBTTagConfigListLong(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTTagConfigLong>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(wolfyUtils, elements, key, path, NBTTagConfigLong.class);
    }

    public NBTTagConfigListLong(NBTTagConfigList<NBTTagConfigLong> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListLong copy() {
        return new NBTTagConfigListLong(this);
    }
}
