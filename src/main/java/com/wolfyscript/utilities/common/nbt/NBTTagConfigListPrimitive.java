package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

public abstract class NBTTagConfigListPrimitive<VAL, T extends NBTTagConfigPrimitive<VAL>> extends NBTTagConfigList<T> {

    public NBTTagConfigListPrimitive(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<T>> elements,  String key, String path, Class<T> elementType) {
        super(wolfyUtils, elements, key, path, elementType);
    }

    public NBTTagConfigListPrimitive(NBTTagConfigList<T> other) {
        super(other);
    }

}
