package com.wolfyscript.utilities.bukkit.world.items.data

import com.wolfyscript.utilities.world.items.data.Bees
import org.bukkit.block.Beehive
import org.bukkit.inventory.meta.BlockStateMeta

class BeesImpl : Bees {

    companion object {
        internal val ITEM_META_CONVERTER = ItemMetaDataKeyConverter<Bees>({
            if (this is BlockStateMeta) {
                val state = blockState
                if (state is Beehive) {
                    TODO("Not yet implemented")
                }
            }
            null
        }, {
            if (this is BlockStateMeta) {
                val state = this.blockState
                if (state is Beehive) {
                    state.entityCount
                }
            }
        })
    }

    override fun entityCount(): Int {
        TODO("Not yet implemented")
    }

}