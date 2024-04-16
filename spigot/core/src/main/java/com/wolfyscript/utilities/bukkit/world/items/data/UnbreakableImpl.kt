package com.wolfyscript.utilities.bukkit.world.items.data

import com.wolfyscript.utilities.world.items.data.Unbreakable
import org.bukkit.inventory.ItemFlag

class UnbreakableImpl(override val showInTooltip: Boolean) : Unbreakable {

    companion object {
        internal val ITEM_META_CONVERTER = ItemMetaDataKeyConverter<Unbreakable>({
            if (isUnbreakable) {
                val showInTooltip = hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                UnbreakableImpl(showInTooltip)
            }
            null
        }, { data ->
            isUnbreakable = true
            if (data.showInTooltip) {
                addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
            } else {
                removeItemFlags(ItemFlag.HIDE_UNBREAKABLE)
            }
        })
    }

}
