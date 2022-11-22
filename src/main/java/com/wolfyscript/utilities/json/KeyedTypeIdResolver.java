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

package com.wolfyscript.utilities.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyCore;
import com.wolfyscript.utilities.json.annotations.KeyedBaseType;
import com.wolfyscript.utilities.common.registry.Registry;
import com.wolfyscript.utilities.common.registry.TypeRegistry;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class KeyedTypeIdResolver extends TypeIdResolverBase {

    private static final Map<Class<?>, Registry<?>> TYPE_REGISTRIES = new HashMap<>();
    private static WolfyCore CORE;

    private JavaType superType;

    public static void setCore(WolfyCore core) {
        if (CORE != null) {
            throw new IllegalCallerException("Cannot be called from another program!");
        }
        CORE = core;
    }

    /**
     * Registers a registry to be used for Json serialization and deserialization. <br>
     * To use that the class of the specified type must be annotated with {@link com.wolfyscript.utilities.json.annotations.OptionalKeyReference}.
     *
     * @param type The type to register.
     * @param registry The registry of the specified type.
     * @param <T> The type of the object.
     */
    public static <T extends Keyed> void registerTypeRegistry(Class<T> type, Registry<T> registry) {
        TYPE_REGISTRIES.putIfAbsent(type, registry);
    }

    /**
     * Registers a registry to be used for Json serialization and deserialization. <br>
     * To use that the class of the specified type must be annotated with {@link com.wolfyscript.utilities.json.annotations.OptionalKeyReference}.
     *
     * @param type The type to register.
     * @param registry The registry of the specified type.
     * @param <T> The type of the object.
     */
    public static <T extends Keyed> void registerTypeRegistry(Class<T> type, TypeRegistry<T> registry) {
        TYPE_REGISTRIES.putIfAbsent(type, registry);
    }

    @Override
    public void init(JavaType baseType) {
        superType = baseType;
    }

    @Override
    public String idFromValue(Object value) {
        return getKey(value);
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> aClass) {
        return getKey(value);
    }

    private String getKey(Object value) {
        if (value instanceof Keyed keyed) {
            return keyed.getNamespacedKey().toString();
        }
        throw new IllegalArgumentException(String.format("Object %s is not of type Keyed!", value.getClass().getName()));
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        Class<?> clazz = getTypeClass(CORE.getWolfyUtils().getIdentifiers().getNamespaced(id));
        return clazz != null ? context.constructSpecializedType(superType, clazz) : TypeFactory.unknownType();
    }

    @Nullable
    protected Class<?> getTypeClass(NamespacedKey key) {
        if (key != null) {
            Class<?> rawClass = superType.getRawClass();
            //If it is specified, use the custom base type instead.
            KeyedBaseType baseTypeAnnot = rawClass.getDeclaredAnnotation(KeyedBaseType.class);
            if (baseTypeAnnot != null) {
                rawClass = baseTypeAnnot.baseType();
            }
            //Get the registry of the required base type
            var registry = TYPE_REGISTRIES.get(rawClass);
            if (registry != null) {
                var object = registry.get(key);
                if (object instanceof Class<?> classObj) {
                    return classObj;
                } else if(object instanceof Keyed) {
                    return object.getClass();
                }
            }
        }
        return null;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
