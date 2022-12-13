package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/string")
public class NBTTagConfigListString extends NBTTagConfigListPrimitive<String, NBTTagConfigString> {

    @JsonCreator
    NBTTagConfigListString(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTTagConfigString>> elements) {
        super(wolfyUtils, elements, NBTTagConfigString.class);
    }

    public NBTTagConfigListString(WolfyUtils wolfyUtils, NBTTagConfig parent, List<Element<NBTTagConfigString>> elements) {
        super(wolfyUtils, parent, NBTTagConfigString.class, elements);
    }

    public NBTTagConfigListString(NBTTagConfigList<NBTTagConfigString> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListString copy() {
        return new NBTTagConfigListString(this);
    }
}
