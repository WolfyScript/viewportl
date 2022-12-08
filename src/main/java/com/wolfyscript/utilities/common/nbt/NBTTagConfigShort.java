package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderShort;

@KeyedStaticId(key = "short")
public class NBTTagConfigShort extends NBTTagConfigPrimitive<Short> {

    @JsonCreator
    public NBTTagConfigShort(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProviderShort value, @JacksonInject("key") String key, @JacksonInject("nbt_tag_config.parent") NBTTagConfig parent) {
        super(wolfyUtils, value, key, parent);
    }

    public NBTTagConfigShort(NBTTagConfigPrimitive<Short> other) {
        super(other);
    }

    @Override
    public NBTTagConfigShort copy() {
        return new NBTTagConfigShort(this);
    }

}
