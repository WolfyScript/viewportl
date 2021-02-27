package me.wolfyscript.utilities.util.entity;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerUtils {

    static final Map<UUID, PlayerStore> indexedStores = new HashMap<>();

    private static final HashMap<UUID, HashMap<EquipmentSlot, UUID>> playerItemParticles = new HashMap<>();
    static final File STORE_FOLDER = new File(WolfyUtilities.getWUPlugin().getDataFolder(), "players");


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
        ParticleUtils.stopAnimation(getActiveItemEffects(player, equipmentSlot));
        getActiveItemEffects(player).remove(equipmentSlot);
    }

    public static void loadStores() {
        WolfyUtilities.getWUPlugin().getLogger().info("Loading Player Data");
        if (!STORE_FOLDER.exists()) {
            STORE_FOLDER.mkdirs();
        }
        for (String s : STORE_FOLDER.list()) {
            UUID uuid = UUID.fromString(s.replace(".store", ""));
            indexedStores.put(uuid, PlayerStore.load(uuid));
        }
    }

    public static void saveStores() {
        if (!STORE_FOLDER.exists()) {
            STORE_FOLDER.mkdirs();
        }
        indexedStores.forEach((uuid, playerStore) -> playerStore.save(uuid));
    }

    @NotNull
    public static PlayerStore getStore(@NotNull Player player) {
        return getStore(player.getUniqueId());
    }

    @NotNull
    public static PlayerStore getStore(@NotNull UUID uuid) {
        if (!indexedStores.containsKey(uuid)) {
            PlayerStore playerStore = new PlayerStore();
            indexedStores.put(uuid, playerStore);
            playerStore.save(uuid);
        }
        return indexedStores.get(uuid);
    }

}
