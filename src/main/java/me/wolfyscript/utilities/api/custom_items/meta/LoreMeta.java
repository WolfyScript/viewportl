package me.wolfyscript.utilities.api.custom_items.meta;


import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        clearLore(itemOther);
        clearLore(item);
        return true;
    }

    private void clearLore(ItemBuilder itemBuilder) {
        if (itemBuilder.getItemMeta().hasLore()) {
            List<String> lore = itemBuilder.getItemMeta().getLore();
            Iterator<String> loreItr = lore.iterator();
            while (loreItr.hasNext()) {
                String line = WolfyUtilities.unhideString(loreItr.next());
                if (line.startsWith("durability_tag") || line.startsWith("itemSettings") || line.startsWith("WU_Durability")) {
                    loreItr.remove();
                }
            }
            itemBuilder.setLore(lore);
        }
    }
}
