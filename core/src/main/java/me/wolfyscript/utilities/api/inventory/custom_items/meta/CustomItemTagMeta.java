package me.wolfyscript.utilities.api.inventory.custom_items.meta;


import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CustomItemTagMeta extends Meta {

    public CustomItemTagMeta() {
        super("customitem_tag");
        setOption(MetaSettings.Option.IGNORE);
        setAvailableOptions(MetaSettings.Option.IGNORE, MetaSettings.Option.EXACT);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        ItemMeta meta1 = itemOther.getItemMeta();
        ItemMeta meta2 = item.getItemMeta();
        if (option.equals(MetaSettings.Option.IGNORE)) {
            NamespacedKey namespacedKey = new NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");
            if (meta1.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)) {
                meta1.getPersistentDataContainer().remove(namespacedKey);
            }
            if (meta2.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)) {
                meta2.getPersistentDataContainer().remove(namespacedKey);
            }
        }
        itemOther.setItemMeta(meta1);
        item.setItemMeta(meta2);
        return true;
    }
}
