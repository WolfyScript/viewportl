package com.wolfyscript.utilities.bukkit.persistent.world;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.WolfyCore;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BlockStorage {

    private static final org.bukkit.NamespacedKey DATA_KEY = new org.bukkit.NamespacedKey("wolfyutils", "data");

    private final Vector pos;
    private final ChunkStorage chunkStorage;
    private final WolfyCoreCommon core;
    private final PersistentDataContainer persistentContainer;

    private final Map<NamespacedKey, CustomBlockData> data = new HashMap<>();

    public BlockStorage(ChunkStorage chunkStorage, Vector pos, PersistentDataContainer persistentContainer) {
        this.chunkStorage = chunkStorage;
        this.core = chunkStorage.getCore();
        this.pos = pos;
        this.persistentContainer = persistentContainer;
    }

    public ChunkStorage getChunkStorage() {
        return chunkStorage;
    }

    public Vector getPos() {
        return pos;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public void remove() {
        getChunkStorage().removeBlock(getPos());
    }

    public void onUnload() {
        data.values().forEach(CustomBlockData::onUnload);
    }

    public void onLoad() {
        data.values().forEach(CustomBlockData::onLoad);
    }

    public void addOrSetData(CustomBlockData blockData) {
        if (blockData != null) {
            var dataTypeRegistry = core.getRegistries().getCustomBlockData();
            if (dataTypeRegistry.keySet().contains(blockData.key())) {
                data.put(blockData.key(), blockData);
            }
        }
    }

    public <D extends CustomBlockData> Optional<D> getData(NamespacedKey key, Class<D> dataType) {
        CustomBlockData customData = data.get(key);
        if (customData != null) {
            if (dataType.isInstance(customData)) {
                return Optional.of(dataType.cast(customData));
            }
        }
        return Optional.empty();
    }

    public Collection<CustomBlockData> getDataValues() {
        return data.values();
    }

    private PersistentDataContainer getPersistentData() {
        if (!persistentContainer.has(DATA_KEY, PersistentDataType.TAG_CONTAINER)) {
            persistentContainer.set(DATA_KEY, PersistentDataType.TAG_CONTAINER, persistentContainer.getAdapterContext().newPersistentDataContainer());
        }
        return persistentContainer.get(DATA_KEY, PersistentDataType.TAG_CONTAINER);
    }

    private void saveToPersistent() {
        var objectMapper = core.getWolfyUtils().getJacksonMapperUtil().getGlobalMapper();
        var dataPersistent = getPersistentData();
        for (Map.Entry<NamespacedKey, CustomBlockData> entry : data.entrySet()) {
            try {
                dataPersistent.set(((BukkitNamespacedKey) entry.getKey()).bukkit(), PersistentDataType.STRING, objectMapper.writeValueAsString(entry.getValue()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        persistentContainer.set(DATA_KEY, PersistentDataType.TAG_CONTAINER, dataPersistent);
    }

    private void loadFromPersistent(ChunkStorage chunkStorage) {
        var dataTypeRegistry = core.getRegistries().getCustomBlockData();
        var objectMapper = core.getWolfyUtils().getJacksonMapperUtil().getGlobalMapper();
        var dataPersistent = getPersistentData();
        for (org.bukkit.NamespacedKey key : dataPersistent.getKeys()) {
            NamespacedKey wuKey = BukkitNamespacedKey.fromBukkit(key);
            String customDataString = dataPersistent.get(key, PersistentDataType.STRING);
            CustomBlockData blockData = null;
            try {
                blockData = objectMapper.reader(new InjectableValues.Std()
                        .addValue(WolfyCore.class, core)
                        .addValue(ChunkStorage.class, chunkStorage)
                        .addValue(Vector.class, pos)
                ).forType(CustomBlockData.class).readValue(customDataString);
            } catch (JsonProcessingException e) {
                core.getLogger().severe("Failed to load custom block data \"" + key + "\" at pos " + pos);
                e.printStackTrace();
            }
            if (blockData != null) {
                data.put(wuKey, blockData);
            }
        }
    }

    public void copyToOtherBlockStorage(BlockStorage storage) {
        data.values().forEach(customBlockData -> {
            CustomBlockData copy = customBlockData.copyTo(storage);
            storage.addOrSetData(copy);
        });
        storage.onLoad();
    }

    public static class PersistentType implements PersistentDataType<PersistentDataContainer, BlockStorage> {

        private final ChunkStorage chunkStorage;
        private final Vector pos;

        public PersistentType(ChunkStorage chunkStorage, Vector pos) {
            this.chunkStorage = chunkStorage;
            this.pos = pos;
        }

        @NotNull
        @Override
        public Class<PersistentDataContainer> getPrimitiveType() {
            return PersistentDataContainer.class;
        }

        @NotNull
        @Override
        public Class<BlockStorage> getComplexType() {
            return BlockStorage.class;
        }

        @NotNull
        @Override
        public PersistentDataContainer toPrimitive(@NotNull BlockStorage complex, @NotNull PersistentDataAdapterContext context) {
            complex.saveToPersistent();
            return complex.persistentContainer;
        }

        @NotNull
        @Override
        public BlockStorage fromPrimitive(@NotNull PersistentDataContainer data, @NotNull PersistentDataAdapterContext context) {
            var blockStorage = new BlockStorage(chunkStorage, pos.clone(), data);
            blockStorage.loadFromPersistent(chunkStorage);
            return blockStorage;
        }
    }


}
