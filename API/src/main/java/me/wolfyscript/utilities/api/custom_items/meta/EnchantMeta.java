package me.wolfyscript.utilities.api.custom_items.meta;


import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;

public class EnchantMeta extends Meta {

    public EnchantMeta() {
        super("enchant");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        if (option.equals(MetaSettings.Option.IGNORE)) {
            itemOther.getItemMeta().getEnchants().keySet().forEach(itemOther::removeEnchantment);
            item.getItemMeta().getEnchants().keySet().forEach(item::removeEnchantment);
        }
        return true;
    }
}
