package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderLong;

@KeyedStaticId(key = "long")
public class NBTTagConfigLong extends NBTTagConfigPrimitive<Long> {

    @JsonCreator
    public NBTTagConfigLong(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProviderLong value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
    }

    public NBTTagConfigLong(NBTTagConfigPrimitive<Long> other) {
        super(other);
    }

    @Override
    public NBTTagConfigLong copy() {
        return new NBTTagConfigLong(this);
    }

}
