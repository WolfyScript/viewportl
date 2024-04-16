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

@KeyedStaticId(key = "double")
public class QueryNodeDouble extends QueryNodePrimitive<Double> {

    @JsonCreator
    public QueryNodeDouble(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProvider<Double> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, value, key, parentPath);
        this.nbtType = NBTType.NBTTagDouble;
    }

    private QueryNodeDouble(QueryNodeDouble other) {
        super(other);
    }

    @Override
    protected Optional<Double> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getDouble(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, Double value, NBTCompound resultContainer) {
        resultContainer.setDouble(key, value);
    }

    @Override
    public QueryNodeDouble copy() {
        return new QueryNodeDouble(this);
    }

}
