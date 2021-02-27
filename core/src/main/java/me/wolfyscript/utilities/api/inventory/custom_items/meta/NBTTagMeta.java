package me.wolfyscript.utilities.api.inventory.custom_items.meta;

import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;

public class NBTTagMeta extends Meta {

    public NBTTagMeta() {
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        return false;
    }
}
