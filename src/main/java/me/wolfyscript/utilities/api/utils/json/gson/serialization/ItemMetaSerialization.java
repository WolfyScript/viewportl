package me.wolfyscript.utilities.api.utils.json.gson.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.api.utils.Reflection;
import me.wolfyscript.utilities.api.utils.json.gson.GsonUtil;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

public class ItemMetaSerialization implements JsonSerializer<ItemMeta>, JsonDeserializer<ItemMeta> {

    private static final Class<?> SerializableMetaClass;
    private static final Method deserializeMeta;

    static {
        SerializableMetaClass = Reflection.getOBC("inventory.CraftMetaItem$SerializableMeta");
        if(SerializableMetaClass != null){
            deserializeMeta = Reflection.getMethod(SerializableMetaClass, "deserialize", Map.class);
            if(deserializeMeta != null){
                deserializeMeta.setAccessible(true);
            }
        }else{
            deserializeMeta = null;
        }
    }

    @Override
    public ItemMeta deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Map<String, Object> map = GsonUtil.getGson().fromJson(jsonElement, Map.class);
        JsonObject json = jsonElement.getAsJsonObject();
        if(deserializeMeta != null){
            try {
                if(json.has("enchants")){
                    Map<String, Integer> enchantMap = new TreeMap<>();
                    json.getAsJsonObject("enchants").entrySet().forEach(entry -> enchantMap.put(entry.getKey(), entry.getValue().getAsInt()));
                    map.put("enchants", enchantMap);
                }
                if(json.has("Damage")){
                    map.put("Damage", json.get("Damage").getAsInt());
                }
                if(json.has("custom-model-data")){
                    map.put("custom-model-data", json.get("custom-model-data").getAsInt());
                }


                System.out.println("Deserialize: "+map);



                return (ItemMeta) deserializeMeta.invoke(null, map);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public JsonElement serialize(ItemMeta itemMeta, Type type, JsonSerializationContext jsonSerializationContext) {
        return GsonUtil.getGson().toJsonTree(itemMeta.serialize());
    }
}
