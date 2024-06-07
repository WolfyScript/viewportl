package com.wolfyscript.utilities.bukkit.world.items.data

import com.wolfyscript.utilities.functions.ReceiverBiConsumer
import com.wolfyscript.utilities.functions.ReceiverFunction
import org.bukkit.inventory.meta.ItemMeta

data class ItemMetaDataKeyConverter<T: Any>(val fetcher: ReceiverFunction<ItemMeta, T?>, val applier: ReceiverBiConsumer<ItemMeta, T>)
