package me.wolfyscript.utilities.api.custom_items.meta;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.Meta;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomDurabilityMeta extends Meta {

    public CustomDurabilityMeta() {
        super("custom_durability");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE, MetaSettings.Option.HIGHER, MetaSettings.Option.LOWER);
    }

    @Override
    public boolean check(ItemMeta metaOther, ItemMeta meta) {
        if(WolfyUtilities.hasVillagePillageUpdate()){
            boolean meta0 = CustomItem.hasCustomDurability(meta);
            boolean meta1 = CustomItem.hasCustomDurability(metaOther);
            if (meta0 && meta1) {
                switch (option) {
                    case EXACT:
                        return CustomItem.getCustomDurability(metaOther) == CustomItem.getCustomDurability(meta);
                    case IGNORE:
                        CustomItem.setCustomDurability(metaOther, 0);
                        CustomItem.setCustomDurability(meta, 0);
                        ((Damageable) metaOther).setDamage(0);
                        ((Damageable) meta).setDamage(0);
                        return true;
                    case LOWER:
                        return CustomItem.getCustomDurability(metaOther) < CustomItem.getCustomDurability(meta);
                    case HIGHER:
                        return CustomItem.getCustomDurability(metaOther) > CustomItem.getCustomDurability(meta);
                }
                return true;
            } else {
                return !meta0 && !meta1;
            }
        }else{
            boolean meta0 = ItemUtils.hasCustomDurability(meta);
            boolean meta1 = ItemUtils.hasCustomDurability(metaOther);
            if (meta0 && meta1) {
                switch (option) {
                    case EXACT:
                        return ItemUtils.getCustomDurability(metaOther) == ItemUtils.getCustomDurability(meta);
                    case IGNORE:
                        ItemUtils.setCustomDurability(metaOther, 0);
                        ItemUtils.setCustomDurability(meta, 0);
                        ((Damageable) metaOther).setDamage(0);
                        ((Damageable) meta).setDamage(0);
                        return true;
                    case LOWER:
                        return ItemUtils.getCustomDurability(metaOther) < ItemUtils.getCustomDurability(meta);
                    case HIGHER:
                        return ItemUtils.getCustomDurability(metaOther) > ItemUtils.getCustomDurability(meta);
                }
                return true;
            } else {
                return !meta0 && !meta1;
            }
        }
    }
}
