/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.wolfyscript.utilities.data

import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.platform.adapters.ItemStack
import com.wolfyscript.utilities.world.items.DyeColor
import com.wolfyscript.utilities.world.items.data.*
import net.kyori.adventure.text.Component

interface Keys {

    fun customData() // TODO

    fun damage(): DataKey<Int>

    fun repairCost(): DataKey<Int>

    fun unbreakable(): DataKey<Unbreakable>

    fun enchantments(): DataKey<Enchantments>

    fun storedEnchantments(): DataKey<Enchantments>

    fun customName(): DataKey<Component>

    fun itemLore(): DataKey<ItemLore>

    fun canBreak() : DataKey<CanBreak>

    fun canPlaceOn() : DataKey<CanPlaceOn>

    fun dyedColor() : DataKey<DyedColor>

    fun attributeModifiers() : DataKey<AttributeModifiers>

    fun chargedProjectiles() : DataKey<ChargedProjectiles>

    fun intangibleProjectile() : DataKey<IntangibleProjectiles>

    fun bundleContents() : DataKey<List<ItemStack>>

    fun mapColor() : DataKey<Int>

    fun mapDecorations() : DataKey<MapDecorations>

    fun mapId() : DataKey<Int>

    fun mapInfo() : DataKey<MapInfo>

    fun customModelData() : DataKey<Int>

    fun potionContents() : DataKey<PotionContents>

    fun writableBookContents() : DataKey<WrittenBookContents>

    fun writtenBookContents() : DataKey<WrittenBookContents>

    fun trim() : DataKey<Trim>

    fun suspiciousStew() : DataKey<SuspiciousStew>

    fun hideAdditionalTooltip() : DataKey<HideAdditionalTooltip>

    fun debugStickState() : DataKey<DebugStickState>

    fun entityData() : DataKey<EntityData>

    fun bucketEntityData() : DataKey<BucketEntityData>

    fun instrument() : DataKey<NamespacedKey>

    fun recipes() : DataKey<List<NamespacedKey>>

    fun lodestoneTracker() : DataKey<LodestoneTracker>

    fun fireworkExplosion() : DataKey<FireworkExplosion>

    fun fireworks() : DataKey<Fireworks>

    fun profile() : DataKey<Profile>

    fun noteBlockSound() : DataKey<NamespacedKey>

    fun baseColor() : DataKey<DyeColor>

    fun bannerPatterns() : DataKey<BannerPatterns>

    fun potDecorations() : DataKey<List<NamespacedKey>>

    fun container() : DataKey<Container>

    fun bees() : DataKey<Bees>

    fun lock() : DataKey<String>

    fun containerLoot() : DataKey<ContainerLoot>

    fun blockEntityData() : DataKey<BlockEntityData>

    fun blockState() : DataKey<BlockState>

    fun enchantmentGlintOverride() : DataKey<Boolean>

}
