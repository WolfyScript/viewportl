package me.wolfyscript.utilities.util.json.jackson.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class ItemStackSerialization {

    public static void create(SimpleModule module){
        JacksonUtil.addSerializerAndDeserializer(module, ItemStack.class, (itemStack, gen, serializerProvider) -> {
            if (itemStack != null) {
                Yaml yaml = new Yaml();
                YamlConfiguration config = new YamlConfiguration();
                config.set("i", itemStack);
                Map<String, Object> map = yaml.load(config.saveToString());
                gen.writeObject(map.get("i"));
            }
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            if (node.isValueNode()) {
                //Old Serialization Methods. like Base64 or NMS serialization
                String value = node.asText();
                if (!value.startsWith("{")) {
                    return WolfyUtilities.getWUCore().getNmsUtil().getItemUtil().getBase64ItemStack(value);
                }
                return value.equals("empty") ? null : WolfyUtilities.getWUCore().getNmsUtil().getItemUtil().getJsonItemStack(value);
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
            return null;
        });
    }
}
