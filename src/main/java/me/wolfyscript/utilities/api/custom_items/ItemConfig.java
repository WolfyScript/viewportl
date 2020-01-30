package me.wolfyscript.utilities.api.custom_items;

import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.JsonConfiguration;
import me.wolfyscript.utilities.api.custom_items.custom_data.CustomData;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemConfig extends CustomConfig {

    public ItemConfig(ConfigAPI configAPI, String namespace, String path, String key, String defaultPath, String defaultName, boolean override, String fileType) {
        super(configAPI, namespace, key, path, defaultPath, defaultName, override, fileType);
    }

    public ItemConfig(ConfigAPI configAPI, String namespace, String path, String key, String defaultName, boolean override, String fileType) {
        super(configAPI, namespace, key, path, "me/wolfyscript/utilities/custom_items/", defaultName, override, fileType);
    }

    public ItemConfig(ConfigAPI configAPI, String namespace, String path, String key, boolean override, String fileType) {
        super(configAPI, namespace, key, path, "me/wolfyscript/utilities/custom_items/", "item", override, fileType);
    }

    public ItemConfig(ConfigAPI configAPI, String namespace, String path, String key, boolean override) {
        super(configAPI, namespace, key, path, "me/wolfyscript/utilities/custom_items/", "item", override, "json");
    }

    public ItemConfig(ConfigAPI configAPI, String namespace, String path, String key) {
        super(configAPI, namespace, key, path, "me/wolfyscript/utilities/custom_items/", "item", false, "json");
    }

    public ItemConfig(String jsonData, ConfigAPI configAPI, String namespace, String key) {
        super(jsonData, configAPI, namespace, key, "me/wolfyscript/utilities/custom_items/", "item");
    }

    public ItemConfig(ConfigAPI configAPI, String namespace, String key) {
        super(configAPI, namespace, key, "me/wolfyscript/utilities/custom_items/", "item");
    }

    /*
    Constructors for CustomCrafting compatibility
     */
    public ItemConfig(String namespace, String key, String defPath, String defName, String fileType, boolean override, ConfigAPI configAPI) {
        super(configAPI, namespace, key, configAPI.getApi().getPlugin().getDataFolder().getParent() + "/CustomCrafting/recipes/" + namespace + "/items", defPath, defName, override, fileType);
    }

    public ItemConfig(String namespace, String key, String defPath, String defName, boolean override, ConfigAPI configAPI) {
        this(namespace, key, defPath, defName, "json", override, configAPI);
    }

    public ItemConfig(String namespace, String key, String defPath, String defName, String fileType, ConfigAPI configAPI) {
        this(namespace, key, defPath, defName, fileType, false, configAPI);
    }

    public ItemConfig(String namespace, String key, String fileType, ConfigAPI configAPI) {
        this(namespace, key, "me/wolfyscript/utilities/custom_items/", "item", fileType, false, configAPI);
    }

    public ItemConfig(String namespace, String key, boolean override, ConfigAPI configAPI) {
        this(namespace, key, "me/wolfyscript/utilities/custom_items/", "item", override, configAPI);
    }

    public ItemStack getCustomItem(boolean replaceLang) {
        return getItem("item", replaceLang);
    }

    public ItemStack getCustomItem() {
        return getCustomItem(true);
    }

    public void setCustomItem(CustomItem itemStack) {
        setItem("item", new ItemStack(itemStack));
        setMetaSettings(itemStack.getMetaSettings());
        setBurnTime(itemStack.getBurnTime());
        setConsumed(itemStack.isConsumed());
        setRarityPercentage(itemStack.getRarityPercentage());
        setPermission(itemStack.getPermission());
        setCustomData(itemStack.getCustomDataMap());
        setReplacementItem(itemStack.getReplacement());
        setDurabilityCost(itemStack.getDurabilityCost());
        setEquipmentSlots(itemStack.getEquipmentSlots().toArray(new EquipmentSlot[0]));
        setParticleData(itemStack.getParticleContent());
        if (itemStack.getAllowedBlocks().isEmpty()) {
            setAllowedBlocks(new ArrayList<>(Collections.singleton(Material.FURNACE)));
        } else {
            setAllowedBlocks(itemStack.getAllowedBlocks());
        }
    }

    public void setItem(ItemStack itemStack) {
        saveItem("item", itemStack);
    }

    public void setDurabilityCost(int durabilityCost) {
        set("durability_cost", durabilityCost);
    }

    public int getDurabilityCost() {
        return getInt("durability_cost");
    }

    public void setConsumed(boolean consumed) {
        set("consumed", consumed);
    }

    public boolean isConsumed() {
        return getBoolean("consumed");
    }

    public void setReplacementItem(CustomItem customItem) {
        if (customItem != null) {
            if (!customItem.getId().isEmpty() && !customItem.getId().equals("NULL")) {
                set("replacement.item_key", customItem.getId());
            } else {
                setItem("replacement.item", new ItemStack(customItem));
            }
        } else {
            set("replacement.item", null);
        }
    }

    public CustomItem getReplacementItem() {
        String id = getString("replacement.item_key");
        if (id != null && !id.isEmpty()) {
            return CustomItems.getCustomItem(id);
        } else if (getItem("replacement.item") != null) {
            return new CustomItem(getItem("replacement.item"));
        }
        return null;
    }

    public void setAllowedBlocks(ArrayList<Material> furnaces) {
        List<String> mats = new ArrayList<>();
        furnaces.forEach(material -> mats.add(material.name().toLowerCase(Locale.ROOT)));
        set("fuel.allowed_blocks", mats);
    }

    public ArrayList<Material> getAllowedBlocks() {
        ArrayList<Material> furnaces = new ArrayList<>();
        if (getStringList("fuel.allowed_blocks") != null) {
            getStringList("fuel.allowed_blocks").forEach(s -> {
                Material material = Material.matchMaterial(s);
                if (material != null) {
                    furnaces.add(material);
                }
            });
        }
        return furnaces;
    }

    public void setBurnTime(int burntime) {
        set("fuel.burntime", burntime);
    }

    public int getBurnTime() {
        return getInt("fuel.burntime", 0);
    }

    public void setMetaSettings(MetaSettings metaSettings) {
        set("meta", metaSettings.toString());
    }

    public MetaSettings getMetaSettings() {
        if (getString("meta") != null && !getString("meta").isEmpty()) {
            return new MetaSettings(getString("meta"));
        }
        return new MetaSettings();
    }

    public void setRarityPercentage(double percentage){
        set("rarity_percentage", percentage);
    }

    public double getRarityPercentage(){
        return getDouble("rarity_percentage", 1.0d);
    }

    public void setPermission(String permission){
        set("permission", permission);
    }

    public String getPermission(){
        return getString("permission", "");
    }

    public void setCustomData(HashMap<String, CustomData> customDataList){
        HashMap<String, Map<String, Object>> customDatas = new HashMap<>();
        for(CustomData customData : customDataList.values()){
            customDatas.put(customData.getId(), customData.toMap());
        }
        set("custom_data", customDatas);
    }

    public HashMap<String, CustomData> getCustomData(){
        HashMap<String, CustomData> customDataMap = new HashMap<>();
        for(CustomData customData : CustomItem.getAvailableCustomData().values()){
            customDataMap.put(customData.getId(), customData.getDefaultCopy());
        }
        Object result = get("custom_data");
        if (result instanceof Map) {
            Map<String, Map<String, Object>> customDatas = (Map<String, Map<String, Object>>) result;
            for (Map.Entry<String, Map<String, Object>> entry : customDatas.entrySet()) {
                if (CustomItem.getAvailableCustomData().containsKey(entry.getKey())) {
                    customDataMap.put(entry.getKey(), CustomItem.getAvailableCustomData().get(entry.getKey()).fromMap(entry.getValue()));
                }
            }
        }
        return customDataMap;
    }

    public List<EquipmentSlot> getEquipmentSlots() {
        List<EquipmentSlot> slots = new ArrayList<>();
        getStringList("equipment_slots").forEach(s -> {
            slots.add(EquipmentSlot.valueOf(s.toUpperCase(Locale.ROOT)));
        });
        return slots;
    }

    public void setEquipmentSlots(EquipmentSlot... slots) {
        set("equipment_slots", slots);
    }

    public ParticleContent getParticleData() {
        return ((JsonConfiguration) configuration).get(ParticleContent.class, "particles");
    }

    public void setParticleData(ParticleContent particleContent) {
        set("particles", particleContent);
    }
}
