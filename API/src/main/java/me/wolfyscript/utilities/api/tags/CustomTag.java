package me.wolfyscript.utilities.api.tags;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class CustomTag {

    private final NamespacedKey namespacedKey;

    private List<Material> materials = new ArrayList<>();
    private List<CustomItem> customItems = new ArrayList<>();

    public CustomTag(NamespacedKey namespacedKey, List<Material> materials){
        this.namespacedKey = namespacedKey;
        this.materials = materials;
    }

    public void setCustomItems(List<CustomItem> customItems) {
        this.customItems = customItems;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

}
