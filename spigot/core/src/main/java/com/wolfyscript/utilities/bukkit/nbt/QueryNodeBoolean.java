/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.eval.operator.BoolOperator;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTList;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.Optional;

@KeyedStaticId(key = "bool")
public class QueryNodeBoolean extends QueryNode<Object> {

    private final BoolOperator value;

    @JsonCreator
    public QueryNodeBoolean(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") BoolOperator value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(wolfyUtils, key, parentPath);
        this.value = value;
    }

    private QueryNodeBoolean(QueryNodeBoolean other) {
        super(other.wolfyUtils, other.key, other.parentPath);
        this.value = other.value;
    }

    @Override
    public boolean check(String key, NBTType nbtType, EvalContext context, Object value) {
        return this.key.equals(key) && this.value.evaluate(context);
    }

    @Override
    protected Optional<Object> readValue(String path, String key, NBTCompound parent) {
        var type = parent.getType(key);
        return Optional.ofNullable(switch (type) {
            case NBTTagInt -> parent.getInteger(key);
            case NBTTagIntArray -> parent.getIntArray(key);
            case NBTTagByte -> parent.getByte(key);
            case NBTTagByteArray -> parent.getByteArray(key);
            case NBTTagShort -> parent.getShort(key);
            case NBTTagLong -> parent.getLong(key);
            case NBTTagDouble -> parent.getDouble(key);
            case NBTTagFloat -> parent.getFloat(key);
            case NBTTagString -> parent.getString(key);
            case NBTTagCompound -> parent.getCompound(key);
            case NBTTagList -> getListOfType(parent.getListType(key), key, parent);
            default -> null;
        });
    }

    private NBTList<?> getListOfType(NBTType nbtType, String key, NBTCompound container) {
        return switch (nbtType) {
            case NBTTagInt -> container.getIntegerList(key);
            case NBTTagIntArray -> container.getIntArrayList(key);
            case NBTTagLong -> container.getLongList(key);
            case NBTTagDouble -> container.getDoubleList(key);
            case NBTTagFloat -> container.getFloatList(key);
            case NBTTagString -> container.getStringList(key);
            case NBTTagCompound -> container.getCompoundList(key);
            default -> null;
        };
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, Object value, NBTCompound resultContainer) {
        if (value instanceof Integer integer) {
            resultContainer.setInteger(key, integer);
        } else if (value instanceof Byte cVal) {
            resultContainer.setByte(key, cVal);
        } else if (value instanceof Short cVal) {
            resultContainer.setShort(key, cVal);
        } else if (value instanceof Long cVal) {
            resultContainer.setLong(key, cVal);
        } else if (value instanceof Double cVal) {
            resultContainer.setDouble(key, cVal);
        } else if (value instanceof Float cVal) {
            resultContainer.setFloat(key, cVal);
        } else if (value instanceof String cVal) {
            resultContainer.setString(key, cVal);
        } else if (value instanceof int[] cVal) {
            resultContainer.setIntArray(key, cVal);
        } else if (value instanceof byte[] cVal) {
            resultContainer.setByteArray(key, cVal);
        }else if (value instanceof NBTCompound cVal) {
            resultContainer.getOrCreateCompound(key).mergeCompound(cVal);
        } else if (value instanceof NBTList list) {
            NBTList<?> nbtList = getListOfType(list.getType(), key, resultContainer);
            if(nbtList != null) {
                nbtList.clear();
                nbtList.addAll(list);
            }
        }
    }

    @Override
    public QueryNodeBoolean copy() {
        return new QueryNodeBoolean(this);
    }
}
