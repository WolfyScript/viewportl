package me.wolfyscript.utilities.api.custom_items;

import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.Pair;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class CustomItems {

    private static TreeMap<String, CustomItem> customItems = new TreeMap<>();

    private static HashMap<Location, Pair<String, UUID>> storedBlocks = new HashMap<>();

    private static HashMap<UUID, HashMap<EquipmentSlot, UUID>> playerItemParticles = new HashMap<>();

    public CustomItems(Plugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::save, 12000, 12000);
    }

    public static List<CustomItem> getCustomItems() {
        return new ArrayList<>(customItems.values());
    }

    public static CustomItem getCustomItem(String key) {
        return getCustomItem(key, true);
    }

    public static CustomItem getCustomItem(String key, boolean replace) {
        if (customItems.containsKey(key) && customItems.get(key) != null) {
            if (replace)
                return customItems.get(key).getRealItem();
            return customItems.get(key).clone();
        }
        return null;
    }

    public static void addCustomItem(CustomItem item) {
        customItems.put(item.getId(), item);
    }

    public static void removeCustomItem(String id) {
        customItems.remove(id);
    }

    public static void removeCustomItem(CustomItem item) {
        customItems.remove(item.getId());
    }

    public static CustomItem getCustomItem(String key, String name) {
        return getCustomItem(key + ":" + name);
    }

    public static CustomItem getCustomItem(String key, String name, boolean replace) {
        return getCustomItem(key + ":" + name, replace);
    }

    public static void setCustomItem(ItemConfig itemConfig) {
        customItems.put(itemConfig.getId(), new CustomItem(itemConfig));
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
        for (Map.Entry<Location, Pair<String, UUID>> entry : storedBlocks.entrySet()) {
            if (entry.getKey().equals(location)) {
                return getCustomItem(entry.getValue().getKey());
            }
        }
        return null;
    }

    public static void setStoredBlockItem(Location location, CustomItem customItem) {
        ParticleEffects.stopEffect(getStoredBlockEffect(location));
        ParticleContent particleContent = customItem.getParticleContent();
        UUID uuid = null;
        if (particleContent != null) {
            NamespacedKey particle = particleContent.getParticleEffect(ParticleEffect.Action.BLOCK);
            uuid = ParticleEffects.spawnEffectOnBlock(particle, location.getBlock());
        }
        storedBlocks.put(location, new Pair<>(customItem.getId(), uuid));
    }

    public static void removeStoredBlockItem(Location location) {
        ParticleEffects.stopEffect(getStoredBlockEffect(location));
        storedBlocks.remove(location);
    }

    @Nullable
    public static UUID getStoredBlockEffect(Location location) {
        for (Map.Entry<Location, Pair<String, UUID>> entry : storedBlocks.entrySet()) {
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
        for (Map.Entry<Location, Pair<String, UUID>> entry : storedBlocks.entrySet()) {
            if (!hasStoredBlockEffect(entry.getKey())) {
                CustomItem customItem = getCustomItem(entry.getValue().getKey());
                if (customItem != null) {
                    if (customItem.getParticleContent() != null && customItem.getParticleContent().containsKey(ParticleEffect.Action.BLOCK)) {
                        ParticleContent particleContent = customItem.getParticleContent();
                        NamespacedKey effectID = particleContent.getParticleEffect(ParticleEffect.Action.BLOCK);
                        if (effectID != null) {
                            UUID uuid = ParticleEffects.spawnEffectOnBlock(effectID, entry.getKey().getBlock());
                            storedBlocks.put(entry.getKey(), new Pair<>(customItem.getId(), uuid));
                        }
                    }
                }
            }
        }
    }


    private static String locationToString(Location location) {
        return location.getWorld().getUID() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    private static Location stringToLocation(String loc) {
        String[] args = loc.split(";");
        return new Location(Bukkit.getWorld(UUID.fromString(args[0])), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(Main.getInstance().getDataFolder() + File.separator + "stored_block_items.dat"));
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
            HashMap<String, String> saveMap = new HashMap<>();
            for (Map.Entry<Location, Pair<String, UUID>> entry : storedBlocks.entrySet()) {
                saveMap.put(locationToString(entry.getKey()), entry.getValue().getKey());
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
                        storedBlocks.put(stringToLocation(entry.getKey()), new Pair<>(entry.getValue(), null));
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
