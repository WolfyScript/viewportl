package me.wolfyscript.utilities.api.inventory.custom_items.meta;


import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;

import java.util.ArrayList;

public class LoreMeta extends Meta {

    public LoreMeta() {
        super("lore");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        if (option.equals(MetaSettings.Option.IGNORE)) {
            itemOther.setLore(new ArrayList<>());
            item.setLore(new ArrayList<>());
        }
        return true;
    }
}
