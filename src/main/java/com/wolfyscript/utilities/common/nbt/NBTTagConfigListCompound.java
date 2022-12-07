package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/compound")
public class NBTTagConfigListCompound extends NBTTagConfigList<NBTTagConfigCompound> {

    public NBTTagConfigListCompound(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTTagConfigCompound>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(wolfyUtils, elements, key, path, NBTTagConfigCompound.class);
    }

    private NBTTagConfigListCompound(NBTTagConfigListCompound other) {
        super(other);
    }

    @Override
    public NBTTagConfigListCompound copy() {
        return new NBTTagConfigListCompound(this);
    }
}
