package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.Optional;

@KeyedStaticId(key = "int")
public class QueryNodeInt extends QueryNodePrimitive<Integer> {

    @JsonCreator
    public QueryNodeInt(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Integer> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
        this.nbtType = NBTType.NBTTagInt;
    }

    public QueryNodeInt(QueryNodePrimitive<Integer> other) {
        super(other);
    }

    @Override
    protected Optional<Integer> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getInteger(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, Integer value, NBTCompound resultContainer) {
        resultContainer.setInteger(key, value);
    }

    @Override
    public QueryNode<Integer> copy() {
        return new QueryNodeInt(this);
    }

}
