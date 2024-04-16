package com.wolfyscript.utilities.bukkit.persistent.player;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

/**
 * This class stores data for player entities.<br>
 * If the player is offline, then the stored data is inaccessible.<br>
 * <br>
 * {@link CustomPlayerData} stored using {@link #setData(CustomPlayerData)} or {@link #computeIfAbsent(Class, Function)}
 * is directly cached and stored into the {@link PersistentDataContainer} of the player.<br>
 * When data is requested via {@link #getData(Class)} it first tries to look for the cached data, and if unavailable
 * it tries to load it from the {@link PersistentDataContainer}. Only if both fail to find the data it returns an empty value.
 */
public class PlayerStorage {

    private static final org.bukkit.NamespacedKey DATA_KEY = new org.bukkit.NamespacedKey("wolfyutils", "data");

    private final WolfyCoreCommon core;
    private final UUID playerUUID;
    private final Map<NamespacedKey, CustomPlayerData> CACHED_DATA = new HashMap<>();

    public PlayerStorage(WolfyCoreCommon core, UUID playerUUID) {
        this.core = core;
        this.playerUUID = playerUUID;
    }

    /**
     * Gets the player to which this storage belongs.
     *
     * @return The player linked to this storage; or empty Optional if not available or offline.
     */
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(playerUUID));
    }

    /**
     * Gets the {@link PersistentDataContainer} from the owner (Player) that is used to store the data.
     *
     * @return The {@link PersistentDataContainer} of the owner; or empty Optional if owner is offline.
     */
    public Optional<PersistentDataContainer> getPersistentDataContainer() {
        return getPlayer().map(PersistentDataHolder::getPersistentDataContainer);
    }

    /**
     * Adds/Updates the specified custom data to/in this storage.<br>
     * The data is then cached and added to the persistent storage!<br>
     * Therefor this method should only be used a limited amount of times!
     *
     * @param data The data value to add/update
     * @return The previous cached value, if any; otherwise null
     */
    public <T extends CustomPlayerData> T setData(T data) {
        T prev = (T) CACHED_DATA.put(data.key(), data);
        if (prev != null) {
            prev.onUnload();
        }
        getPersistentDataContainer().ifPresent(container -> {
            var dataContainer = container.getOrDefault(DATA_KEY, PersistentDataType.TAG_CONTAINER, container.getAdapterContext().newPersistentDataContainer());
            var objectMapper = core.getWolfyUtils().getJacksonMapperUtil().getGlobalMapper();
            try {
                data.onLoad();
                dataContainer.set(((BukkitNamespacedKey)data.key()).bukkit(), PersistentDataType.STRING, objectMapper.writeValueAsString(data));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            container.set(DATA_KEY, PersistentDataType.TAG_CONTAINER, dataContainer);
        });
        return prev;
    }

    /**
     * Gets the data that is saved under the specified type, or if not available gets and sets the default data.
     *
     * @param dataType     The type of the data.
     * @param defaultValue Function that creates the default value.
     * @param <T>          The type of the data.
     * @return The existing data; or the newly set default value.
     */
    public <T extends CustomPlayerData> Optional<T> computeIfAbsent(Class<T> dataType, Function<Class<T>, T> defaultValue) {
        return getData(dataType).or(() -> {
            T data = defaultValue.apply(dataType);
            setData(data);
            return Optional.ofNullable(data);
        });
    }

    /**
     * Gets the data that is saved under the specified type.<br>
     * In case the value is not yet loaded into the cache it deserializes it from the persistent data container.<br>
     * If the persistent data container is not available, which is the case when the player is offline, it returns an empty Optional.<br>
     *
     * @param dataType The type of the data. Must be registered in {@link BukkitRegistries#getCustomPlayerData()}!
     * @param <T>      The type of the data.
     * @return The data of the specified type; or empty Optional when not available.
     */
    public <T extends CustomPlayerData> Optional<T> getData(Class<T> dataType) {
        NamespacedKey dataID = core.getRegistries().getCustomPlayerData().getKey(dataType);
        if (dataID == null) return Optional.empty(); // Might be null if the type wasn't registered. Check it just in case.
        T dataResult = dataType.cast(CACHED_DATA.get(dataID));
        if (dataID instanceof BukkitNamespacedKey bukkitDataID && dataResult == null) {
            // If there isn't any cached data yet
            Optional<T> data = getPersistentDataContainer().map(container -> {
                var dataContainer = container.getOrDefault(DATA_KEY, PersistentDataType.TAG_CONTAINER, container.getAdapterContext().newPersistentDataContainer());
                var objectMapper = core.getWolfyUtils().getJacksonMapperUtil().getGlobalMapper();
                org.bukkit.NamespacedKey key = bukkitDataID.bukkit();
                if (dataContainer.has(key, PersistentDataType.STRING)) {
                    String jsonData = dataContainer.get(key, PersistentDataType.STRING);
                    if (jsonData != null && !jsonData.equals("null") && !jsonData.isBlank()) {
                        try {
                            return objectMapper.reader(new InjectableValues.Std()
                                            .addValue(WolfyCoreSpigot.class, core)
                                            .addValue(UUID.class, playerUUID)
                                    )
                                    .forType(CustomPlayerData.class)
                                    .readValue(jsonData);
                        } catch (JsonProcessingException e) {
                            core.getLogger().warning("Unable to load custom data from '" + jsonData + "'! Removing it now to prevent further issues!");
                            // Directly modify the container instead of calling removeData() as the unload method will never be called anyway.
                            dataContainer.remove(key);
                            container.set(DATA_KEY, PersistentDataType.TAG_CONTAINER, dataContainer);
                        }
                    }
                }
                return null;
            });
            dataResult = data.orElse(null);
            CACHED_DATA.put(dataID, dataResult);
        }
        return Optional.ofNullable(dataResult);
    }

    /**
     * Removes the data saved under the specified id.
     *
     * @param dataType The type of the data. Must be registered in {@link BukkitRegistries#getCustomPlayerData()}!
     * @param <T>      The type of the data.
     * @return The removed data value; or null if nothing was removed.
     */
    public <T extends CustomPlayerData> T removeData(Class<T> dataType) {
        NamespacedKey dataID = core.getRegistries().getCustomPlayerData().getKey(dataType);
        T prev = dataType.cast(CACHED_DATA.remove(dataID));
        if (prev != null) {
            prev.onUnload();
        }
        getPersistentDataContainer().ifPresent(container -> {
            var dataContainer = container.getOrDefault(DATA_KEY, PersistentDataType.TAG_CONTAINER, container.getAdapterContext().newPersistentDataContainer());
            if (dataID instanceof BukkitNamespacedKey bukkitDataID) {
                dataContainer.remove(bukkitDataID.bukkit());
            }
            container.set(DATA_KEY, PersistentDataType.TAG_CONTAINER, dataContainer);
        });
        return prev;
    }

    /**
     * Removes the data saved under the specified id.
     *
     * @param dataID The id of the data.
     * @return The removed data value; or null if nothing was removed.
     */
    public CustomPlayerData removeData(BukkitNamespacedKey dataID) {
        return removeData(core.getRegistries().getCustomPlayerData().get(dataID));
    }

    /**
     * Updates all the currently cached values in the persistent data container
     * and clears the cache afterwards.
     */
    public void updateAndClearCache() {
        update();
        CACHED_DATA.clear();
    }

    /**
     * Updates all the currently cached values in the persistent data container.
     */
    public void update() {
        if (CACHED_DATA.isEmpty()) return;
        getPersistentDataContainer().ifPresent(container -> {
            var dataContainer = container.getOrDefault(DATA_KEY, PersistentDataType.TAG_CONTAINER, container.getAdapterContext().newPersistentDataContainer());
            var objectMapper = core.getWolfyUtils().getJacksonMapperUtil().getGlobalMapper();
            for (Map.Entry<NamespacedKey, CustomPlayerData> dataEntry : CACHED_DATA.entrySet()) {
                try {
                    if (dataEntry.getKey() instanceof BukkitNamespacedKey bukkitDataID) {
                        dataContainer.set(bukkitDataID.bukkit(), PersistentDataType.STRING, objectMapper.writeValueAsString(dataEntry.getValue()));
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            container.set(DATA_KEY, PersistentDataType.TAG_CONTAINER, dataContainer);
        });
    }

}
