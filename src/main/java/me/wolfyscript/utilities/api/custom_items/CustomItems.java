package me.wolfyscript.utilities.api.custom_items;

import me.wolfyscript.utilities.api.custom_items.api_references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.Pair;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import me.wolfyscript.utilities.main.WUPlugin;
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
import java.util.*;
import java.util.stream.Collectors;

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

    public static List<String> getNamespaces() {
        return customItems.keySet().stream().map(NamespacedKey::getNamespace).distinct().collect(Collectors.toList());
    }

    /**
     * Get all the items of the specific namespace.
     *
     * @param namespace the namespace you want to get the items from
     * @return A list of all the items of the specific namespace
     */
    public static List<CustomItem> getCustomItems(String namespace) {
        return customItems.entrySet().stream().filter(entry -> entry.getKey().getNamespace().equals(namespace)).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    /**
     * @param namespacedKey NamespacedKey of the item
     * @return true if there is an CustomItem for the NamespacedKey
     */
    public static boolean hasCustomItem(NamespacedKey namespacedKey) {
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
        return namespacedKey == null ? null : customItems.get(namespacedKey);
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
        playerItemParticles.putIfAbsent(player.getUniqueId(), new HashMap<>());
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
        return storedBlocks.containsKey(location) || storedBlocks.keySet().stream().anyMatch(location1 -> location1.equals(location));
    }

    @Nullable
    public static CustomItem getStoredBlockItem(Location location) {
        return storedBlocks.entrySet().stream().filter(entry -> entry.getKey().equals(location)).map(entry -> getCustomItem(entry.getValue().getKey())).findFirst().orElse(null);
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
            storedBlocks.put(location, new Pair<>(customItem.getNamespacedKey(), uuid));
        }
    }

    public static void removeStoredBlockItem(Location location) {
        ParticleEffects.stopEffect(getStoredBlockEffect(location));
        storedBlocks.remove(location);
    }

    @Nullable
    public static UUID getStoredBlockEffect(Location location) {
        return storedBlocks.entrySet().stream().filter(entry -> entry.getKey().equals(location) && entry.getValue() != null && entry.getValue().getValue() != null).map(entry -> entry.getValue().getValue()).findFirst().orElse(null);
    }

    public static boolean hasStoredBlockEffect(Location location) {
        if (isBlockStored(location)) {
            return getStoredBlockEffect(location) != null;
        }
        return false;
    }

    public static void initiateMissingBlockEffects() {
        storedBlocks.entrySet().stream().filter(entry -> !hasStoredBlockEffect(entry.getKey())).forEach(entry -> {
            CustomItem customItem = getCustomItem(entry.getValue().getKey());
            if (customItem != null) {
                if (customItem.getParticleContent() != null && customItem.getParticleContent().containsKey(ParticleEffect.Action.BLOCK)) {
                    ParticleContent particleContent = customItem.getParticleContent();
                    NamespacedKey effectID = particleContent.getParticleEffect(ParticleEffect.Action.BLOCK);
                    if (effectID != null) {
                        UUID uuid = ParticleEffects.spawnEffectOnBlock(effectID, entry.getKey().getBlock());
                        storedBlocks.put(entry.getKey(), new Pair<>(customItem.getNamespacedKey(), uuid));
                    }
                }
            }
        });
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
            if (world != null) {
                return new Location(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            }
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Couldn't find world " + args[0]);
        }
        return null;
    }

    @Deprecated
    public static CustomItem getCustomItem(String id) {
        return getCustomItem(id, true);
    }

    @Deprecated
    public static CustomItem getCustomItem(String id, boolean replace) {
        return getCustomItem(NamespacedKey.getByString(id));
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

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(WUPlugin.getInstance().getDataFolder() + File.separator + "stored_block_items.dat"));
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
            oos.writeObject(storedBlocks.entrySet().stream().filter(e -> e.getKey() != null).map(e -> new Pair<>(locationToString(e.getKey()), e.getValue().getKey().toString())).filter(e -> e.getKey() != null).collect(Collectors.toMap(Pair::getKey, Pair::getValue)));
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File file = new File(WUPlugin.getInstance().getDataFolder() + File.separator + "stored_block_items.dat");
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                BukkitObjectInputStream ois = new BukkitObjectInputStream(fis);
                try {
                    Object object = ois.readObject();
                    HashMap<String, String> loadMap = (HashMap<String, String>) object;
                    loadMap.forEach((key, value) -> {
                        Location location = stringToLocation(key);
                        if (location != null) {
                            storedBlocks.put(location, new Pair<>(NamespacedKey.getByString(value), null));
                        }
                    });
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
