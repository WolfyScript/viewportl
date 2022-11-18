/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
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

package com.wolfyscript.utilities.common.json.jackson;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wolfyscript.utilities.common.WolfyCore;
import com.wolfyscript.utilities.common.WolfyUtils;

/**
 * Used to manage and cache Jackson mappers.
 */
public class MapperUtil {

    private ObjectMapper globalMapper;
    private final WolfyUtils wolfyUtils;

    public MapperUtil(WolfyUtils wolfyUtils) {
        this.wolfyUtils = wolfyUtils;
        this.globalMapper = new ObjectMapper();
    }

    /**
     * Sets the global ObjectMapper to the one specified.
     *
     * @param globalMapper The ObjectMapper to cache.
     */
    public void setGlobalMapper(ObjectMapper globalMapper) {
        this.globalMapper = globalMapper;
    }

    /**
     * Gets the currently cached ObjectMapper.
     *
     * @return The cached ObjectMapper or null
     */
    public ObjectMapper getGlobalMapper() {
        return globalMapper;
    }

    /**
     * Gets the currently cached ObjectMapper and casts it to the specified type.<br>
     * May cause ClassCastException, if it cannot be cast to the type.
     *
     * @return The cached ObjectMapper or null
     */
    public <M extends ObjectMapper> M getGlobalMapper(Class<M> mapperType) {
        return mapperType.cast(globalMapper);
    }

    /**
     * Convenience method for {@link com.wolfyscript.utilities.common.WolfyCore#applyWolfyUtilsJsonMapperModules(ObjectMapper)}
     * that applies the default modules of WolfyUtilities to the specified ObjectMapper.<br>
     * This includes for example default de-/serializers, TypeId handlers, etc.
     *
     * @param mapper The ObjectMapper to apply the modules to.
     * @return The specified mapper for chaining.
     * @param <M> The type of ObjectMapper
     */
    public <M extends ObjectMapper> M applyWolfyUtilsModules(M mapper) {
        return wolfyUtils.getCore().applyWolfyUtilsJsonMapperModules(mapper);
    }

    public <M extends ObjectMapper> M applyWolfyUtilsInjectableValues(M mapper, InjectableValues.Std injectableValues) {
        mapper.setInjectableValues(injectableValues
                .addValue(WolfyCore.class, wolfyUtils.getCore())
                .addValue(WolfyUtils.class, wolfyUtils)
        );
        return mapper;
    }

    private static <T> void addSerializer(SimpleModule module, JsonSerializer<T> serializer) {
        module.addSerializer(serializer.handledType(), serializer);
    }

    private static <T> void addDeserializer(SimpleModule module, JsonDeserializer<T> deserializer) {
        module.addDeserializer((Class<? super T>) deserializer.handledType(), deserializer);
    }

}
