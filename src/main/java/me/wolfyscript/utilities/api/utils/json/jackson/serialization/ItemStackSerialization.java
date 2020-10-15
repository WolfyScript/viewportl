package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.utils.Reflection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class ItemStackSerialization {

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

    public static void create(SimpleModule module){
        module.addSerializer(ItemStack.class, new Serializer());
        module.addDeserializer(ItemStack.class, new Deserializer());
    }

    /*
            -----------------------------------------------------------------------------------------
            JsonObject result = new JsonObject();
            result.addProperty("v", Bukkit.getUnsafe().getDataVersion());
            result.addProperty("type", itemStack.getType().name());
            if (itemStack.getAmount() != 1) {
                result.addProperty("amount", itemStack.getAmount());
            }
            ItemMeta meta = itemStack.getItemMeta();
            if (!Bukkit.getItemFactory().equals(meta, null)) {
                result.add("meta", jsonSerializationContext.serialize(meta, ItemMeta.class));
            }
            return result;
            -----------------------------------------------------------------------------------------
            return new JsonPrimitive(ItemUtils.serializeItemStackBase64(itemStack));
    */
    public static class Serializer extends StdSerializer<ItemStack> {

        public Serializer(){
            this(ItemStack.class);
        }

        protected Serializer(Class<ItemStack> t) {
            super(t);
        }

        @Override
        public void serialize(ItemStack itemStack, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if(itemStack != null) {
                Map<String, Object> itemMap = itemStack.serialize();
                if (itemStack.hasItemMeta()) {
                    itemMap.put("meta", itemStack.getItemMeta().serialize());
                }
                gen.writeObject(itemMap);
            }
        }
    }

    public static class Deserializer extends StdDeserializer<ItemStack> {

        public Deserializer() {
            this(ItemStack.class);
        }

        protected Deserializer(Class<ItemStack> t) {
            super(t);
        }

        public static ItemMeta deserializeItemMeta(Map<String, Object> map) {
            ItemMeta meta = null;
            try {
                Class<?> clazz = Reflection.getOBC("inventory.CraftMetaItem$SerializableMeta");
                Method deserialize = clazz.getMethod("deserialize", Map.class);
                meta = (ItemMeta) deserialize.invoke(null, map);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return meta;
        }

        @Override
        public ItemStack deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            Map<String, Object> map = p.readValueAs(new TypeReference<Map<String, Object>>() {
            });
            map.computeIfPresent("meta", (s, o) -> deserializeItemMeta((Map<String, Object>) o));
            ItemStack itemStack = ItemStack.deserialize(map);//TODO: Find out the reason why the Material is changed after setItemMeta() in the ItemStack.deserialize() method!
            return itemStack;
        }
    }
}
