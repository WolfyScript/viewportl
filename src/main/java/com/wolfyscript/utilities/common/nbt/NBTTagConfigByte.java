package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;

@KeyedStaticId(key = "byte")
public class NBTTagConfigByte extends NBTTagConfigPrimitive<Byte> {

    @JsonCreator
    public NBTTagConfigByte(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Byte> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
    }

    private NBTTagConfigByte(NBTTagConfigByte other) {
        super(other);
    }

    @Override
    public NBTTagConfigByte copy() {
        return new NBTTagConfigByte(this);
    }

}
