package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/string")
public class NBTTagConfigListString extends NBTTagConfigListPrimitive<String, NBTTagConfigString> {

    public NBTTagConfigListString(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTTagConfigString>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(wolfyUtils, elements, key, path, NBTTagConfigString.class);
    }

    public NBTTagConfigListString(NBTTagConfigList<NBTTagConfigString> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListString copy() {
        return new NBTTagConfigListString(this);
    }
}
