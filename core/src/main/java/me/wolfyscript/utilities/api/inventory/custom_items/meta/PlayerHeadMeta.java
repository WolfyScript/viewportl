package me.wolfyscript.utilities.api.inventory.custom_items.meta;


import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;

public class PlayerHeadMeta extends Meta {

    public PlayerHeadMeta() {
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        if(option.equals(MetaSettings.Option.EXACT)){
            String valueOther = itemOther.getPlayerHeadValue();
            String value = item.getPlayerHeadValue();
            if(!valueOther.equals(value)){
                return false;
            }
        }
        itemOther.removePlayerHeadValue();
        item.removePlayerHeadValue();
        return true;
    }
}
