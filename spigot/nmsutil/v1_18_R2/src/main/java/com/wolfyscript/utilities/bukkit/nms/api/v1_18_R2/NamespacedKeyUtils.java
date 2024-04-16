package com.wolfyscript.utilities.bukkit.nms.api.v1_18_R2;

import com.wolfyscript.utilities.NamespacedKey;
import net.minecraft.resources.ResourceLocation;

public class NamespacedKeyUtils {

    private NamespacedKeyUtils(){}

    public static ResourceLocation toMC(NamespacedKey key) {
        return new ResourceLocation(key.getNamespace(), key.getKey());
    }
}
