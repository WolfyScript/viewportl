package com.wolfyscript.utilities.bukkit.world.items.data

import com.wolfyscript.utilities.world.items.data.AttributeModifiers

class AttributeModifiersImpl(override val showInTooltip: Boolean) : AttributeModifiers {

    companion object {
        internal val ITEM_META_CONVERTER = ItemMetaDataKeyConverter<AttributeModifiers>(
            { TODO("Not yet implemented") },
            { TODO("Not yet implemented") }
        )
    }

}