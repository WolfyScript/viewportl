package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

@KeyedStaticId(key = "float")
public class NBTTagConfigFloat extends NBTTagConfigPrimitive<Float> {

    @JsonCreator
    public NBTTagConfigFloat(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Float> valueNode, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, valueNode, key, parentPath);
    }

    private NBTTagConfigFloat(NBTTagConfigFloat other) {
        super(other);
    }

    @Override
    public NBTTagConfigFloat copy() {
        return new NBTTagConfigFloat(this);
    }

}
