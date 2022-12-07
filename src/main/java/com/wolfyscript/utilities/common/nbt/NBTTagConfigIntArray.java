package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

@KeyedStaticId(key = "int_array")
public class NBTTagConfigIntArray extends NBTTagConfigPrimitive<int[]> {

    @JsonCreator
    public NBTTagConfigIntArray(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<int[]> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
    }

    public NBTTagConfigIntArray(NBTTagConfigIntArray other) {
        super(other.wolfyUtils, other.value, other.key, other.parentPath);
    }

    @Override
    public NBTTagConfigIntArray copy() {
        return new NBTTagConfigIntArray(this);
    }

}
