package com.wolfyscript.utilities.bukkit.world.items

import com.wolfyscript.utilities.WolfyCore
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.bukkit.data.SpigotDataKeyProvider
import com.wolfyscript.utilities.platform.world.items.Items
import com.wolfyscript.utilities.world.items.ItemStackConfig

class ItemsImpl(wolfyCore: WolfyCore) : Items {

    override val dataKeyProvider: SpigotDataKeyProvider = SpigotDataKeyProvider(wolfyCore)

    override fun createStackConfig(wolfyUtils: WolfyUtils, itemId: String): ItemStackConfig {
        return BukkitItemStackConfig(wolfyUtils, itemId)
    }

}
