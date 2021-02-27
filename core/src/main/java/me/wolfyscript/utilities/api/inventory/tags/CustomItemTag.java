package me.wolfyscript.utilities.api.inventory.tags;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class CustomItemTag extends CustomTag<CustomItem> {

    public CustomItemTag(NamespacedKey namespacedKey) {
        super(namespacedKey);
    }

    public boolean isItemValid(ItemStack itemStack, boolean exactMeta) {
        return values.parallelStream().anyMatch(customItem1 -> customItem1.isSimilar(itemStack, exactMeta));
    }
}
