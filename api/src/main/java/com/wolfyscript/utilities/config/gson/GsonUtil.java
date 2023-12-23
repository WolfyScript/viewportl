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

package com.wolfyscript.utilities.config.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class GsonUtil {

    private static final GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping().serializeNulls().serializeSpecialFloatingPointValues().disableInnerClassSerialization();

    public static GsonBuilder getGsonBuilder() {
        return gsonBuilder;
    }

    public static Gson getGson() {
        return gsonBuilder.create();
    }

    public static Gson getGson(boolean prettyPrinting){
        Gson resultGson;
        if (prettyPrinting) {
            GsonUtil.getGsonBuilder().setPrettyPrinting();
            resultGson = GsonUtil.getGsonBuilder().create();
            try {
                Field pretty = GsonUtil.getGsonBuilder().getClass().getDeclaredField("prettyPrinting");
                pretty.setAccessible(true);
                pretty.setBoolean(GsonUtil.getGsonBuilder(), false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            resultGson = GsonUtil.getGsonBuilder().create();
        }
        return resultGson;
    }

    public static void registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter){
        gsonBuilder.registerTypeHierarchyAdapter(baseType, typeAdapter);
    }

    public static void registerTypeAdapter(Type type, Object typeAdapter){
        gsonBuilder.registerTypeAdapter(type, typeAdapter);
    }

    public static void registerTypeAdapterFactory(TypeAdapterFactory factory) {
        gsonBuilder.registerTypeAdapterFactory(factory);
    }
}
