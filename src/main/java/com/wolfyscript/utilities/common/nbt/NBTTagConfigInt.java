package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

@KeyedStaticId(key = "int")
public class NBTTagConfigInt extends NBTTagConfigPrimitive<Integer> {

    @JsonCreator
    public NBTTagConfigInt(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Integer> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
    }

    public NBTTagConfigInt(NBTTagConfigPrimitive<Integer> other) {
        super(other);
    }

    @Override
    public NBTTagConfigPrimitive<Integer> copy() {
        return new NBTTagConfigInt(this);
    }

}
