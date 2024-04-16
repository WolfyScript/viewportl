package com.wolfyscript.utilities.bukkit.persistent.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import com.wolfyscript.utilities.bukkit.world.items.CustomItemBlockData;
import com.wolfyscript.utilities.config.jackson.KeyedTypeIdResolver;
import com.wolfyscript.utilities.config.jackson.KeyedTypeResolver;

/**
 * This data is used to store persistent data on Blocks.<br>
 * The data is saved directly inside the Chunks' {@link org.bukkit.persistence.PersistentDataContainer}, so it persists across server restarts.<br>
 * In order to save it the {@link CustomBlockData} is serialized into a JsonString using Jackson.<br>
 * The String is then saved into the {@link org.bukkit.persistence.PersistentDataContainer} with the id ({@link org.bukkit.NamespacedKey}) as the key.<br>
 * <br>
 * <strong>To make use of the implementation it must be registered via {@link BukkitRegistries#getCustomBlockData()}!</strong>
 * <br>
 * On Deserialization the key is used to find the registered data type (See {@link BukkitRegistries#getCustomBlockData()})<br>
 * The String content is then deserialized to that type using Jackson.<br>
 * There are injectable values that can be used in the constructor to get access to the Core, ChunkStorage, Position, etc.<br>
 * <ul>
 *     <li>{@link com.wolfyscript.utilities.bukkit.WolfyCoreCommon}</li>
 *     <li>{@link ChunkStorage}</li>
 *     <li>{@link org.bukkit.util.Vector}</li>
 * </ul>
 * That can be injected using the {@link com.fasterxml.jackson.annotation.JacksonInject} annotation.<br>
 * <br>
 * One of the default data, that stores the CustomItems on blocks is {@link CustomItemBlockData}
 */
@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "id")
@JsonPropertyOrder(value = {"id"})
public abstract class CustomBlockData implements Keyed {

    @JsonProperty("id")
    private final NamespacedKey id;

    protected CustomBlockData(NamespacedKey id) {
        this.id = id;
    }

    /**
     * Called when the BlockStorage is initialising its data.
     * Usually right after the data was constructed.
     */
    public abstract void onLoad();

    /**
     * Called when the BlockStorage is removed from the ChunkStorage or the Chunk is unloaded.
     */
    public abstract void onUnload();

    public abstract CustomBlockData copy();

    /**
     * Copies this data to the other BlockStorage.
     *
     * @param storage The other BlockStorage to copy the data to.
     * @return The data that was copied to the other BlockStorage.
     */
    public abstract CustomBlockData copyTo(BlockStorage storage);

    @JsonIgnore
    @Override
    public NamespacedKey key() {
        return id;
    }
}
