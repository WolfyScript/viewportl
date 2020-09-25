package me.wolfyscript.utilities.api.custom_items;

import me.wolfyscript.utilities.api.custom_items.api_references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.Pair;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class CustomItems {

    private static final TreeMap<NamespacedKey, CustomItem> customItems = new TreeMap<>();

    private static final HashMap<Location, Pair<NamespacedKey, UUID>> storedBlocks = new HashMap<>();

    private static final HashMap<UUID, HashMap<EquipmentSlot, UUID>> playerItemParticles = new HashMap<>();

    public CustomItems(Plugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::save, 12000, 12000);
    }

    public static TreeMap<NamespacedKey, CustomItem> getCustomItems() {
        return customItems;
    }

    @Deprecated
    public static CustomItem getCustomItem(String id) {
        return getCustomItem(id, true);
    }

    @Deprecated
    public static CustomItem getCustomItem(String id, boolean replace) {
        return getCustomItem(new NamespacedKey(id.split(":")[0], id.split(":")[1]));
    }

    @Deprecated
    public static CustomItem getCustomItem(String key, String name) {
        return getCustomItem(key + ":" + name);
    }

    @Deprecated
    public static CustomItem getCustomItem(String key, String name, boolean replace) {
        return getCustomItem(key + ":" + name, replace);
    }

    @Deprecated
    public static void removeCustomItem(String id) {
        removeCustomItem(new NamespacedKey(id.split(":")[0], id.split(":")[1]));
    }

    /**
     *
     * @param namespacedKey NamespacedKey of the item
     * @return true if there is an CustomItem for the NamespacedKey
     */
    public static boolean hasCustomItem(NamespacedKey namespacedKey){
        return customItems.containsKey(namespacedKey);
    }

    /**
     * Used to get the CustomItem from WolfyUtilities by NamespacedKey.
     * Can return null when the CustomItem doesn't exist!
     * <p>Use {@link #hasCustomItem(NamespacedKey)} to make the CustomItem exists!
     *
     * @param namespacedKey
     * @return CustomItem of the NamespacedKey or null if it doesn't exist
     */
    @Nullable
    public static CustomItem getCustomItem(@Nullable NamespacedKey namespacedKey) {
        if(namespacedKey == null) return null;
        return customItems.get(namespacedKey);
    }

    public static void removeCustomItem(NamespacedKey namespacedKey) {
        customItems.remove(namespacedKey);
    }

    /**
     * Add or Update an CustomItem.
     * If the CustomItem has a WolfyUtilitiesRef and it's NamespacedKey is the same as the parsed in NamespacedKey, the CustomItem will neither be added or updated!
     *
     * @param namespacedKey the NamspacedKey the CustomItem will be saved under.
     * @param item the CustomItem to add or update.
     * @return true if the CustomItem was added or updated.
     */
    public static boolean addCustomItem(NamespacedKey namespacedKey, CustomItem item) {
        if(item == null || (item.getApiReference() instanceof WolfyUtilitiesRef && ((WolfyUtilitiesRef) item.getApiReference()).getNamespacedKey().equals(namespacedKey))){
            return false;
        }
        item.setNamespacedKey(namespacedKey);
        customItems.put(namespacedKey, item);
        return true;
    }

    public static HashMap<EquipmentSlot, UUID> getActiveItemEffects(Player player) {
        if (!hasActiveItemEffects(player)) {
            playerItemParticles.put(player.getUniqueId(), new HashMap<>());
        }
        return playerItemParticles.get(player.getUniqueId());
    }

    public static boolean hasActiveItemEffects(Player player) {
        return playerItemParticles.containsKey(player.getUniqueId());
    }

    public static boolean hasActiveItemEffects(Player player, EquipmentSlot equipmentSlot) {
        return playerItemParticles.getOrDefault(player.getUniqueId(), new HashMap<>()).containsKey(equipmentSlot);
    }

    public static UUID getActiveItemEffects(Player player, EquipmentSlot equipmentSlot) {
        return playerItemParticles.getOrDefault(player.getUniqueId(), new HashMap<>()).get(equipmentSlot);
    }

    public static void setActiveParticleEffect(Player player, EquipmentSlot equipmentSlot, UUID uuid) {
        if (hasActiveItemEffects(player, equipmentSlot)) {
            stopActiveParticleEffect(player, equipmentSlot);
        }
        getActiveItemEffects(player).put(equipmentSlot, uuid);
    }

    public static void stopActiveParticleEffect(Player player, EquipmentSlot equipmentSlot) {
        ParticleEffects.stopEffect(getActiveItemEffects(player, equipmentSlot));
        getActiveItemEffects(player).remove(equipmentSlot);
    }

    //StoredBlocks Methods
    public static boolean isBlockStored(Location location) {
        if (storedBlocks.containsKey(location)) {
            return storedBlocks.containsKey(location);
        } else {
            for (Location location1 : storedBlocks.keySet()) {
                if (location1.equals(location)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    public static CustomItem getStoredBlockItem(Location location) {
        for (Map.Entry<Location, Pair<NamespacedKey, UUID>> entry : storedBlocks.entrySet()) {
            if (entry.getKey().equals(location)) {
                return getCustomItem(entry.getValue().getKey());
            }
        }
        return null;
    }

    public static void setStoredBlockItem(Location location, CustomItem customItem) {
        ParticleEffects.stopEffect(getStoredBlockEffect(location));
        if(customItem.getApiReference() instanceof WolfyUtilitiesRef || customItem.hasNamespacedKey()){
            ParticleContent particleContent = customItem.getParticleContent();
            UUID uuid = null;
            if (particleContent != null) {
                NamespacedKey particle = particleContent.getParticleEffect(ParticleEffect.Action.BLOCK);
                uuid = ParticleEffects.spawnEffectOnBlock(particle, location.getBlock());
            }
            storedBlocks.put(location, new Pair<>( customItem.getApiReference() instanceof WolfyUtilitiesRef ? ((WolfyUtilitiesRef) customItem.getApiReference()).getNamespacedKey() : customItem.getNamespacedKey(), uuid));
        }
    }

    public static void removeStoredBlockItem(Location location) {
        ParticleEffects.stopEffect(getStoredBlockEffect(location));
        storedBlocks.remove(location);
    }

    @Nullable
    public static UUID getStoredBlockEffect(Location location) {
        for (Map.Entry<Location, Pair<NamespacedKey, UUID>> entry : storedBlocks.entrySet()) {
            if (entry.getKey().equals(location)) {
                return entry.getValue().getValue();
            }
        }
        return null;
    }

    public static boolean hasStoredBlockEffect(Location location) {
        if (isBlockStored(location)) {
            return getStoredBlockEffect(location) != null;
        }
        return false;
    }

    public static void initiateMissingBlockEffects() {
        for (Map.Entry<Location, Pair<NamespacedKey, UUID>> entry : storedBlocks.entrySet()) {
            if (!hasStoredBlockEffect(entry.getKey())) {
                CustomItem customItem = getCustomItem(entry.getValue().getKey());
                if (customItem != null && customItem.getApiReference() instanceof WolfyUtilitiesRef) {
                    if (customItem.getParticleContent() != null && customItem.getParticleContent().containsKey(ParticleEffect.Action.BLOCK)) {
                        ParticleContent particleContent = customItem.getParticleContent();
                        NamespacedKey effectID = particleContent.getParticleEffect(ParticleEffect.Action.BLOCK);
                        if (effectID != null) {
                            UUID uuid = ParticleEffects.spawnEffectOnBlock(effectID, entry.getKey().getBlock());
                            storedBlocks.put(entry.getKey(), new Pair<>(((WolfyUtilitiesRef) customItem.getApiReference()).getNamespacedKey(), uuid));
                        }
                    }
                }
            }
        }
    }

    private static String locationToString(Location location) {
        if(location == null || location.getWorld() == null) return null;
        return location.getWorld().getUID() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    private static Location stringToLocation(String loc) {
        String[] args = loc.split(";");
        try{
            UUID uuid = UUID.fromString(args[0]);
            World world = Bukkit.getWorld(uuid);
            if(world != null){
                return new Location(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            }
        }catch (IllegalArgumentException e){
            Bukkit.getLogger().warning("Couldn't find world "+args[0]);
        }
        return null;
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(Main.getInstance().getDataFolder() + File.separator + "stored_block_items.dat"));
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
            HashMap<String, String> saveMap = new HashMap<>();
            for (Map.Entry<Location, Pair<NamespacedKey, UUID>> entry : storedBlocks.entrySet()) {
                if (entry.getKey() != null) {
                    String loc = locationToString(entry.getKey());
                    if(loc != null){
                        saveMap.put(loc, entry.getValue().getKey().toString());
                    }
                }
            }
            oos.writeObject(saveMap);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File file = new File(Main.getInstance().getDataFolder() + File.separator + "stored_block_items.dat");
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                BukkitObjectInputStream ois = new BukkitObjectInputStream(fis);
                try {
                    Object object = ois.readObject();
                    HashMap<String, String> loadMap = (HashMap<String, String>) object;
                    for (Map.Entry<String, String> entry : loadMap.entrySet()) {
                        String[] key = entry.getValue().split(":");
                        Location location = stringToLocation(entry.getKey());
                        if(location != null){
                            storedBlocks.put(location, new Pair<>(new NamespacedKey(key[0], key[1]), null));
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
