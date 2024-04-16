package com.wolfyscript.utilities.bukkit.data

import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.data.DataComponentMap
import com.wolfyscript.utilities.data.DataKey
import com.wolfyscript.utilities.platform.adapters.ItemStack

class ItemStackDataComponentMap internal constructor(private val itemStack: ItemStackImpl) : DataComponentMap<ItemStack> {

    override fun keySet(): Set<DataKey<*, ItemStack>> {
        TODO("Not yet implemented")
    }

    override fun remove(key: DataKey<*, ItemStack>): Boolean {

        return true
    }

    override fun size(): Int {
        return 0
    }

    override fun <T: Any> get(key: DataKey<T, ItemStack>): T? {
        return key.readFrom(itemStack)
    }

    override fun has(key: DataKey<*, ItemStack>): Boolean {
        return key.readFrom(itemStack) != null
    }

    override fun <T: Any> set(key: DataKey<T, ItemStack>, data: T) {
        key.writeTo(data, itemStack)
    }

}
