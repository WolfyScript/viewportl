package me.wolfyscript.utilities.api.custom_items.meta;

import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import org.bukkit.inventory.meta.ItemMeta;

public class NameMeta extends Meta {

    public NameMeta() {
        super("name");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if (option.equals(MetaSettings.Option.IGNORE)) {
            meta1.setDisplayName(null);
            meta2.setDisplayName(null);
        }
        return true;
    }
}
