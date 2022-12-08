package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/int")
public class NBTTagConfigListInt extends NBTTagConfigListPrimitive<Integer, NBTTagConfigInt> {

    public NBTTagConfigListInt(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTTagConfigInt>> elements, @JacksonInject("key") String key, @JacksonInject("nbt_tag_config.parent") NBTTagConfig parent) {
        super(wolfyUtils, elements, key, parent, NBTTagConfigInt.class);
    }

    public NBTTagConfigListInt(NBTTagConfigList<NBTTagConfigInt> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListInt copy() {
        return new NBTTagConfigListInt(this);
    }
}
