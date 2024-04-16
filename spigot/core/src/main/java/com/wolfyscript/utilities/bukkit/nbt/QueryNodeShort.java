package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderShort;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.Optional;

@KeyedStaticId(key = "short")
public class QueryNodeShort extends QueryNodePrimitive<Short> {

    @JsonCreator
    public QueryNodeShort(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProviderShort value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
        this.nbtType = NBTType.NBTTagShort;
    }

    public QueryNodeShort(QueryNodePrimitive<Short> other) {
        super(other);
    }

    @Override
    protected Optional<Short> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getShort(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, Short value, NBTCompound resultContainer) {
        resultContainer.setShort(key, value);
    }

    @Override
    public QueryNodeShort copy() {
        return new QueryNodeShort(this);
    }

}
