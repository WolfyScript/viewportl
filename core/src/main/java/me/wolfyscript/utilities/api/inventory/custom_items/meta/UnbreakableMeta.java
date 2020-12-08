package me.wolfyscript.utilities.api.inventory.custom_items.meta;


import me.wolfyscript.utilities.api.inventory.custom_items.Meta;
import me.wolfyscript.utilities.api.inventory.custom_items.MetaSettings;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.inventory.meta.ItemMeta;

public class UnbreakableMeta extends Meta {

    public UnbreakableMeta() {
        super("unbreakable");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        ItemMeta meta1 = itemOther.getItemMeta();
        ItemMeta meta2 = item.getItemMeta();
        if (option.equals(MetaSettings.Option.IGNORE)) {
            meta1.setUnbreakable(false);
            meta2.setUnbreakable(false);
            itemOther.setItemMeta(meta1);
            item.setItemMeta(meta2);
        }
        return true;
    }
}
