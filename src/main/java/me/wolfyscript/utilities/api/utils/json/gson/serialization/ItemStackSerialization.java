package me.wolfyscript.utilities.api.utils.json.gson.serialization;

import com.google.gson.*;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.api.utils.json.gson.GsonUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.Map;

public class ItemStackSerialization implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if(jsonElement instanceof JsonPrimitive){
            //Old Serialization Methods. like Base64 or NMS serialization
            String value = jsonElement.getAsString();
            if (!value.startsWith("{")) {
                return ItemUtils.deserializeItemStackBase64(jsonElement.getAsString());
            }
            if (value == null || value.equals("empty")) {
                return null;
            }
            return ItemUtils.convertJsontoItemStack(value);
        }else if(jsonElement instanceof JsonObject){
            YamlConfiguration config = new YamlConfiguration();
            config.createSection("i", GsonUtil.getGson().fromJson(jsonElement, Map.class));

            System.out.println("Loaded: "+config.saveToString());
            System.out.println("Item: "+config.getItemStack("i"));
            return config.getItemStack("i");
            /*
            //New Custom ItemStack Serilization
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            int version = jsonObject.get("v").getAsInt();
            Material material = Material.matchMaterial(jsonObject.get("type").getAsString());
            int amount = 1;
            if(jsonObject.has("amount")){
                amount = jsonObject.get("amount").getAsInt();
            }
            ItemStack result = new ItemStack(material);
            result.setAmount(amount);
            if(jsonObject.has("meta")){
                ItemMeta meta = jsonDeserializationContext.deserialize(jsonObject.get("meta"), ItemMeta.class);
                if(meta != null){
                    meta.setVersion(version);
                    if (meta.hasDisplayName()) {
                        String displayName = meta.getDisplayName();
                        meta.setDisplayName(WolfyUtilities.translateColorCodes(displayName));
                    }
                    if (meta.hasLore()) {
                        meta.setLore(meta.getLore().stream().map(s -> WolfyUtilities.translateColorCodes(s)).collect(Collectors.toList()));
                    }
                    result.setItemMeta(meta);
                }
            }

            return result;
            */
        }
        return null;
    }

    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext jsonSerializationContext) {
        if(itemStack != null){
            /*
            Yaml yaml = new Yaml();
            YamlConfiguration config = new YamlConfiguration();
            config.set("i", itemStack);
            Map<String,Object> map = yaml.load(config.saveToString());
            return ((JsonObject)GsonUtil.getGson().toJsonTree(map)).get("i");
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
             */
            return new JsonPrimitive(ItemUtils.serializeItemStackBase64(itemStack));
        }
        return null;
    }
}
