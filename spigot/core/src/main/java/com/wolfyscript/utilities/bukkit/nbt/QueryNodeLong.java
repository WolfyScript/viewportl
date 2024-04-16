package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderLong;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.Optional;

@KeyedStaticId(key = "long")
public class QueryNodeLong extends QueryNodePrimitive<Long> {

    @JsonCreator
    public QueryNodeLong(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProviderLong value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
        this.nbtType = NBTType.NBTTagLong;
    }

    public QueryNodeLong(QueryNodePrimitive<Long> other) {
        super(other);
    }

    @Override
    protected Optional<Long> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getLong(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, Long value, NBTCompound resultContainer) {
        resultContainer.setLong(key, value);
    }

    @Override
    public QueryNodeLong copy() {
        return new QueryNodeLong(this);
    }

}
