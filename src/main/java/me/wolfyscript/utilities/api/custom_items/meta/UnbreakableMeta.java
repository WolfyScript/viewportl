package me.wolfyscript.utilities.api.custom_items.meta;


import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import org.bukkit.inventory.meta.ItemMeta;

public class UnbreakableMeta extends Meta {

    public UnbreakableMeta() {
        super("unbreakable");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if (option.equals(MetaSettings.Option.IGNORE)) {
            meta1.setUnbreakable(false);
            meta2.setUnbreakable(false);
        }
        return true;
    }
}
