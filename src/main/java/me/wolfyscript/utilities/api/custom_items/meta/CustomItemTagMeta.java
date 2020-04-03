package me.wolfyscript.utilities.api.custom_items.meta;


import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import me.wolfyscript.utilities.main.Main;
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
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if (option.equals(MetaSettings.Option.IGNORE)) {
            if (WolfyUtilities.hasVillagePillageUpdate()) {
                if (meta1.getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "custom_item"), PersistentDataType.STRING)) {
                    meta1.getPersistentDataContainer().remove(new NamespacedKey(Main.getInstance(), "custom_item"));
                }
                if (meta2.getPersistentDataContainer().has(new NamespacedKey(Main.getInstance(), "custom_item"), PersistentDataType.STRING)) {
                    meta2.getPersistentDataContainer().remove(new NamespacedKey(Main.getInstance(), "custom_item"));
                }
            }
        }
        return true;
    }
}
