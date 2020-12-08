package me.wolfyscript.utilities.api.inventory.custom_items.meta;


import me.wolfyscript.utilities.api.inventory.custom_items.Meta;
import me.wolfyscript.utilities.api.inventory.custom_items.MetaSettings;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;

public class FlagsMeta extends Meta {

    public FlagsMeta() {
        super("flags");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        if (option.equals(MetaSettings.Option.IGNORE)) {
            itemOther.getItemMeta().getItemFlags().forEach(itemOther::removeItemFlags);
            item.getItemMeta().getItemFlags().forEach(item::removeItemFlags);
        }
        return true;
    }
}
