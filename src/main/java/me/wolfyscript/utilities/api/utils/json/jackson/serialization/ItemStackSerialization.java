package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.utils.Reflection;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

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
        JacksonUtil.addSerializerAndDeserializer(module, ItemStack.class, (itemStack, gen, serializerProvider) -> {
            if (itemStack != null) {
                Yaml yaml = new Yaml();
                YamlConfiguration config = new YamlConfiguration();
                config.set("i", itemStack);
                Map<String, Object> map = yaml.load(config.saveToString());
                gen.writeObject(map.get("i"));
                //This method seems to be broken... can't serialize the ItemStack directly to a Map!
                /*
                Map<String, Object> itemMap = itemStack.serialize();
                if (itemStack.hasItemMeta()) {
                    itemMap.put("meta", itemStack.getItemMeta().serialize());
                }
                gen.writeObject(itemMap);
                 */
            }
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            if (node.isValueNode()) {
                //Old Serialization Methods. like Base64 or NMS serialization
                String value = node.asText();
                if (!value.startsWith("{")) {
                    return ItemUtils.deserializeItemStackBase64(value);
                }
                return value.equals("empty") ? null : ItemUtils.convertJsontoItemStack(value);
            }
            YamlConfiguration config = new YamlConfiguration();
            //Loads the Map from the JsonNode && Sets the Map to YamlConfig
            config.set("i", JacksonUtil.getObjectMapper().convertValue(node, new TypeReference<Map<String, Object>>() {
            }));
            try {
                    /*
                    Load new YamlConfig from just saved string.
                    That will convert the Map to an ItemStack!
                     */
                config.loadFromString(config.saveToString());
                return config.getItemStack("i");
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
            //TODO: Find out the reason why the Material is changed after setItemMeta() in the ItemStack.deserialize() method!

            //This method seems to be broken... can't deserialize the ItemStack directly from Map!
            /*
            Map<String, Object> map = JacksonUtil.getObjectMapper().convertValue(node, new TypeReference<Map<String, Object>>() {});
            map.computeIfPresent("meta", (s, o) -> deserializeItemMeta((Map<String, Object>) o));
            ItemStack itemStack = ItemStack.deserialize(map);

             */
            return null;
        });
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
    }
}
