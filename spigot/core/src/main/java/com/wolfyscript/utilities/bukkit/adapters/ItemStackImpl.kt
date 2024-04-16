package com.wolfyscript.utilities.bukkit.adapters

import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit
import com.wolfyscript.utilities.bukkit.data.ItemStackDataComponentMap
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig
import com.wolfyscript.utilities.data.DataComponentMap
import com.wolfyscript.utilities.world.items.ItemStackConfig
import org.bukkit.inventory.ItemStack

class ItemStackImpl(wolfyUtils: WolfyUtilsBukkit, bukkitRef: ItemStack?) : BukkitRefAdapter<ItemStack?>(bukkitRef), com.wolfyscript.utilities.platform.adapters.ItemStack {
    private val wolfyUtils: WolfyUtils = wolfyUtils
    private val componentMap = ItemStackDataComponentMap(this)

    override fun getItem(): NamespacedKey {
        if (getBukkitRef() == null) {
            throw IllegalStateException()
        }
        return BukkitNamespacedKey.fromBukkit(getBukkitRef()!!.type.key)
    }

    override fun getAmount(): Int {
        return getBukkitRef()!!.amount
    }

    override fun snapshot(): ItemStackConfig {
        return BukkitItemStackConfig(wolfyUtils, this)
    }

    override fun data(): ItemStackDataComponentMap {
        return componentMap
    }
}
