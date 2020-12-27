package me.wolfyscript.utilities.util.world;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;

import java.util.UUID;

public class BlockCustomItemStore {

    @JsonIgnore
    private CustomItem customItem;
    @JsonIgnore
    private UUID particleUUID;

    public BlockCustomItemStore(CustomItem customItem, UUID particleUUID) {
        this.customItem = customItem;
        this.particleUUID = particleUUID;
    }

    public CustomItem getCustomItem() {
        return customItem;
    }

    @JsonSetter("custom_item")
    private void setCustomItem(String namespacedKey) {
        this.customItem = Registry.CUSTOM_ITEMS.get(NamespacedKey.getByString(namespacedKey));
    }

    @JsonGetter("custom_item")
    private String getCustomItemKey() {
        return customItem.getNamespacedKey().toString();
    }

    public UUID getParticleUUID() {
        return particleUUID;
    }

    public void setParticleUUID(UUID particleUUID) {
        this.particleUUID = particleUUID;
    }
}
