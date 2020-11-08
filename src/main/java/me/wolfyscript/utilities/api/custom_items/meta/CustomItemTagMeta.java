package me.wolfyscript.utilities.api.custom_items.meta;


import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.main.WUPlugin;
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
            if (meta1.getPersistentDataContainer().has(new NamespacedKey(WUPlugin.getInstance(), "custom_item"), PersistentDataType.STRING)) {
                meta1.getPersistentDataContainer().remove(new NamespacedKey(WUPlugin.getInstance(), "custom_item"));
            }
            if (meta2.getPersistentDataContainer().has(new NamespacedKey(WUPlugin.getInstance(), "custom_item"), PersistentDataType.STRING)) {
                meta2.getPersistentDataContainer().remove(new NamespacedKey(WUPlugin.getInstance(), "custom_item"));
            }
        }
        itemOther.setItemMeta(meta1);
        item.setItemMeta(meta2);
        return true;
    }
}
