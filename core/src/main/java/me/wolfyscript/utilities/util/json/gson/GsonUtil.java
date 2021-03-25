package me.wolfyscript.utilities.util.json.gson;

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
