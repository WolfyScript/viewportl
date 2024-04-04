package com.wolfyscript.utilities.data

import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.gui.functions.ReceiverBiFunction
import com.wolfyscript.utilities.gui.functions.ReceiverFunction
import com.wolfyscript.utilities.platform.adapters.ItemStack
import kotlin.reflect.KClass

class ItemStackDataKey<T : Any> internal constructor(
    private val key: NamespacedKey,
    private val fetcher: ReceiverFunction<ItemStack, T?>,
    private val applier: ReceiverBiFunction<ItemStack, T, ItemStack>
) : DataKey<T, ItemStack> {

    companion object {

        inline fun <reified T : Any> builder(key: NamespacedKey): DataKey.Builder<T, ItemStack> = BuilderImpl(T::class, key)

    }

    override fun readFrom(source: ItemStack): T? {
        return with(fetcher) { source.apply() }
    }

    override fun writeTo(value: T, target: ItemStack) {
        with(applier) {
            target.apply(value)
        }
    }

    override fun key(): NamespacedKey = key

    class BuilderImpl<T : Any>(override val valueType: KClass<T>, private val key: NamespacedKey) : DataKey.Builder<T, ItemStack> {

        private var fetcher: ReceiverFunction<ItemStack, T?> = ReceiverFunction { null }
        private var applier: ReceiverBiFunction<ItemStack, T, ItemStack> = ReceiverBiFunction { this }

        override fun reader(readerFn: ReceiverFunction<ItemStack, T?>): DataKey.Builder<T, ItemStack> = apply {
            this.fetcher = readerFn
        }

        override fun writer(writerFn: ReceiverBiFunction<ItemStack, T, ItemStack>): DataKey.Builder<T, ItemStack> = apply {
            this.applier = writerFn
        }

        override fun build(): DataKey<T, ItemStack> {
            return ItemStackDataKey(key, fetcher, applier)
        }

    }
}
