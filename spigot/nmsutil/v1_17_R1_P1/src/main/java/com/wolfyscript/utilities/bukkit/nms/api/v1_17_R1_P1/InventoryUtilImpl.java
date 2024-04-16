package com.wolfyscript.utilities.bukkit.nms.api.v1_17_R1_P1;

import com.wolfyscript.utilities.bukkit.nms.api.InventoryUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.world.inventory.CreativeModeTab;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;

public class InventoryUtilImpl extends InventoryUtil {

    protected InventoryUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public final void initItemCategories() {
        for (Material material : Material.values()) {
            if (material.isLegacy()) continue;
            var item = CraftMagicNumbers.getItem(material);
            if (item != null) {
                var creativeModeTab = item.getItemCategory();
                if (creativeModeTab != null) {
                    CreativeModeTab.of(creativeModeTab.getRecipeFolderName()).ifPresent(tab-> tab.registerMaterial(material));
                }
            }
        }
    }
}
