package me.wolfyscript.utilities.api.custom_items.meta;


import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import org.bukkit.inventory.meta.ItemMeta;

public class PotionMeta extends Meta {

    public PotionMeta() {
        super("potion");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if (meta1 instanceof org.bukkit.inventory.meta.PotionMeta && meta2 instanceof org.bukkit.inventory.meta.PotionMeta) {
            if (option.equals(MetaSettings.Option.IGNORE)) {
                ((org.bukkit.inventory.meta.PotionMeta) meta1).clearCustomEffects();
                ((org.bukkit.inventory.meta.PotionMeta) meta2).clearCustomEffects();
            }
        }
        return true;
    }
}
