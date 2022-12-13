package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/float")
public class NBTTagConfigListFloat extends NBTTagConfigListPrimitive<Float, NBTTagConfigFloat> {

    @JsonCreator
    NBTTagConfigListFloat(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("values") List<NBTTagConfigFloat> elements) {
        super(wolfyUtils, elements, NBTTagConfigFloat.class);
    }

    public NBTTagConfigListFloat(WolfyUtils wolfyUtils, NBTTagConfig parent, List<NBTTagConfigFloat> elements) {
        super(wolfyUtils, parent, NBTTagConfigFloat.class, elements);
    }

    public NBTTagConfigListFloat(NBTTagConfigList<NBTTagConfigFloat> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListFloat copy() {
        return new NBTTagConfigListFloat(this);
    }
}
