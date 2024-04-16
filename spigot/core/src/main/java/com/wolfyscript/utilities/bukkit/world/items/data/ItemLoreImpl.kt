package com.wolfyscript.utilities.bukkit.world.items.data

import com.wolfyscript.utilities.world.items.data.ItemLore
import net.kyori.adventure.text.Component

class ItemLoreImpl : ItemLore {

    companion object {
        internal val ITEM_META_CONVERTER = ItemMetaDataKeyConverter<ItemLore>(
            { TODO("Not yet implemented") },
            { TODO("Not yet implemented") }
        )
    }

    override fun lines(): MutableList<Component> {
        TODO("Not yet implemented")
    }

}