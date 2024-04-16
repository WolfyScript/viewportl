package com.wolfyscript.utilities.bukkit.world.items.data

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.platform.adapters.ItemStack
import com.wolfyscript.utilities.world.items.data.ChargedProjectiles
import org.bukkit.inventory.meta.CrossbowMeta

class ChargedProjectilesImpl(val projectiles: List<ItemStack>) : ChargedProjectiles {

    companion object {
        internal val ITEM_META_CONVERTER = ItemMetaDataKeyConverter<ChargedProjectiles>(
            {
                if (this is CrossbowMeta) {
                    val projectiles = chargedProjectiles.map {
                        ItemStackImpl(WolfyCoreCommon.instance.wolfyUtils, it)
                    }
                    return@ItemMetaDataKeyConverter ChargedProjectilesImpl(projectiles)
                }
                null
            },
            { TODO("Not yet implemented") }
        )

    }
}