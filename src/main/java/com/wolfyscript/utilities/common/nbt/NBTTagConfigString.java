package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

@KeyedStaticId(key = "string")
public class NBTTagConfigString extends NBTTagConfigPrimitive<String> {

    @JsonCreator
    public NBTTagConfigString(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<String> value, @JacksonInject("key") String key, @JacksonInject("nbt_tag_config.parent") NBTTagConfig parent) {
        super(wolfyUtils, value, key, parent);
    }

    public NBTTagConfigString(NBTTagConfigPrimitive<String> other) {
        super(other);
    }

    @Override
    public NBTTagConfigString copy() {
        return new NBTTagConfigString(this);
    }

}
