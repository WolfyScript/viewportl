package me.wolfyscript.utilities.api.inventory.custom_items;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.api_references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.particles.ParticleEffect;
import me.wolfyscript.utilities.api.particles.ParticleEffects;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
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
     * <p>Use {@link #hasCustomItem(NamespacedKey)} to make sure the CustomItem exists!
     *
     * @param namespacedKey The NamespacedKey of the item
     * @return CustomItem of the NamespacedKey or null if it doesn't exist
     */
    @Nullable
    public static CustomItem getCustomItem(@Nullable NamespacedKey namespacedKey) {
        return namespacedKey == null ? null : customItems.get(namespacedKey);
    }

    /**
     * Removes the CustomItem from the registry.
     * However, this won't delete the config if one exists!
     * If a config exists the item will be reloaded on the next restart.
     *
     * @param namespacedKey The NamespacedKey of the CustomItem
     */
    public static void removeCustomItem(NamespacedKey namespacedKey) {
        customItems.remove(namespacedKey);
    }

    /**
     * Add a CustomItem to the registry or update a existing one and sets the NamespacedKey in the CustomItem object.
     * <br/>
     * If the registry already contains a value for the NamespacedKey then the value will be updated with the new one.
     * <br/>
     * <b>
     * If the CustomItem is linked with a {@link WolfyUtilitiesRef}, which NamespacedKey is the same as the passed in NamespacedKey, the CustomItem will neither be added or updated!
     * <br/>
     * This is to prevent a infinite loop where a reference tries to call itself when it tries to get the values from it's parent item.
     * <b/>
     *
     * @param namespacedKey The NamespacedKey the CustomItem will be saved under.
     * @param item          The CustomItem to add or update.
     * @return If the CustomItem was added or updated. True if it was successful.
     */
    public static boolean addCustomItem(NamespacedKey namespacedKey, CustomItem item) {
        if (item == null || (item.getApiReference() instanceof WolfyUtilitiesRef && ((WolfyUtilitiesRef) item.getApiReference()).getNamespacedKey().equals(namespacedKey))) {
            return false;
        }
        item.setNamespacedKey(namespacedKey);
        customItems.put(namespacedKey, item);
        return true;
    }

    /**
     * Gets the particle effects that are currently active on the player.
     *
     * @param player The player object
     * @return The active particle effects on the player
     */
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

    /**
     * Removes the stored block at this location and stops every active particle effect.
     *
     * @param location The target location of the block
     */
    public static void removeStoredBlockItem(Location location) {
        ParticleEffects.stopEffect(getStoredBlockEffect(location));
        storedBlocks.remove(location);
    }

    /**
     * The current active particle effect on this Location.
     *
     * @param location The location to be checked.
     * @return The uuid of the currently active particle effect.
     */
    @Nullable
    public static UUID getStoredBlockEffect(@NotNull Location location) {
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

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(WolfyUtilities.getWUPlugin().getDataFolder() + File.separator + "stored_block_items.dat");
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
            oos.writeObject(storedBlocks.entrySet().stream().filter(e -> e.getKey() != null).map(e -> new Pair<>(locationToString(e.getKey()), e.getValue().getKey().toString())).filter(e -> e.getKey() != null).collect(Collectors.toMap(Pair::getKey, Pair::getValue)));
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File file = new File(WolfyUtilities.getWUPlugin().getDataFolder() + File.separator + "stored_block_items.dat");
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
