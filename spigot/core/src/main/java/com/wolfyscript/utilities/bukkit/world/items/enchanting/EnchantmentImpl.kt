package com.wolfyscript.utilities.bukkit.world.items.enchanting

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey
import org.bukkit.enchantments.Enchantment

internal class EnchantmentImpl(val bukkit: Enchantment) : com.wolfyscript.utilities.world.items.enchanting.Enchantment {

    override fun maxLevel(): Int {
        return bukkit.maxLevel
    }

    override fun minLevel(): Int {
        return bukkit.startLevel
    }

    override fun key(): BukkitNamespacedKey {
        return BukkitNamespacedKey.fromBukkit(bukkit.key)
    }
}
