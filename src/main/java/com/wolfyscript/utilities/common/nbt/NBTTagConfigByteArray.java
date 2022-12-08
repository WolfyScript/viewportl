package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

@KeyedStaticId(key = "byte_array")
public class NBTTagConfigByteArray extends NBTTagConfigPrimitive<byte[]> {

    @JsonCreator
    public NBTTagConfigByteArray(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<byte[]> value, @JacksonInject("key") String key, @JacksonInject("nbt_tag_config.parent") NBTTagConfig parent) {
        super(wolfyUtils, value, key, parent);
    }

    private NBTTagConfigByteArray(NBTTagConfigByteArray other) {
        super(other.wolfyUtils, other.value, other.key, other.parent);
    }

    @Override
    public NBTTagConfigByteArray copy() {
        return new NBTTagConfigByteArray(this);
    }

}
