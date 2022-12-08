package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/float")
public class NBTTagConfigListFloat extends NBTTagConfigListPrimitive<Float, NBTTagConfigFloat> {

    public NBTTagConfigListFloat(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTTagConfigFloat>> elements, @JacksonInject("key") String key, @JacksonInject("nbt_tag_config.parent") NBTTagConfig parent) {
        super(wolfyUtils, elements, key, parent, NBTTagConfigFloat.class);
    }

    public NBTTagConfigListFloat(NBTTagConfigList<NBTTagConfigFloat> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListFloat copy() {
        return new NBTTagConfigListFloat(this);
    }
}
