package com.wolfyscript.utilities.bukkit.world.items.data

import com.wolfyscript.utilities.bukkit.world.items.enchanting.EnchantmentImpl
import com.wolfyscript.utilities.world.items.data.Enchantments
import com.wolfyscript.utilities.world.items.enchanting.Enchantment
import org.bukkit.inventory.ItemFlag

class EnchantmentsImpl(override val showInTooltip: Boolean, val enchants: MutableMap<Enchantment, Int>) : Enchantments {

    companion object {
        internal val ITEM_META_CONVERTER = ItemMetaDataKeyConverter<Enchantments>(
            {
                EnchantmentsImpl(
                    !hasItemFlag(ItemFlag.HIDE_ENCHANTS),
                    enchants.mapKeys<org.bukkit.enchantments.Enchantment, Int, Enchantment> {
                        EnchantmentImpl(it.key)
                    }.toMutableMap()
                )
            },
            {
                removeEnchantments()
                for (entry in (it as EnchantmentsImpl).enchants) {
                    addEnchant((entry.key as EnchantmentImpl).bukkit, entry.value, true)
                }
                if (it.showInTooltip) {
                    removeItemFlags(ItemFlag.HIDE_ENCHANTS)
                } else {
                    addItemFlags(ItemFlag.HIDE_ENCHANTS)
                }
            }
        )
    }

    override fun level(enchantment: Enchantment): Int? {
        return enchants[enchantment]
    }

    override fun set(enchantment: Enchantment, level: Int) {
        enchants[enchantment] = level
    }

}