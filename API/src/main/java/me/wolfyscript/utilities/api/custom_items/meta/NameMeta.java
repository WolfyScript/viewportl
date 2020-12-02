package me.wolfyscript.utilities.api.custom_items.meta;

import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;

public class NameMeta extends Meta {

    public NameMeta() {
        super("name");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemBuilder meta1, ItemBuilder meta2) {
        if (option.equals(MetaSettings.Option.IGNORE)) {
            meta1.setDisplayName(null);
            meta2.setDisplayName(null);
        }
        return true;
    }
}
