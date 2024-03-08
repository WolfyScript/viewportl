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
import com.wolfyscript.utilities.world.items.data.AttributeModifiers
import com.wolfyscript.utilities.world.items.data.BannerPatterns
import com.wolfyscript.utilities.world.items.data.Bees
import com.wolfyscript.utilities.world.items.data.BlockEntityData
import com.wolfyscript.utilities.world.items.data.BlockState
import com.wolfyscript.utilities.world.items.data.BucketEntityData
import com.wolfyscript.utilities.world.items.data.CanBreak
import com.wolfyscript.utilities.world.items.data.CanPlaceOn
import com.wolfyscript.utilities.world.items.data.ChargedProjectiles
import com.wolfyscript.utilities.world.items.data.Container
import com.wolfyscript.utilities.world.items.data.ContainerLoot
import com.wolfyscript.utilities.world.items.data.DebugStickState
import com.wolfyscript.utilities.world.items.data.DyedColor
import com.wolfyscript.utilities.world.items.data.Enchantments
import com.wolfyscript.utilities.world.items.data.EntityData
import com.wolfyscript.utilities.world.items.data.FireworkExplosion
import com.wolfyscript.utilities.world.items.data.Fireworks
import com.wolfyscript.utilities.world.items.data.HideAdditionalTooltip
import com.wolfyscript.utilities.world.items.data.IntangibleProjectiles
import com.wolfyscript.utilities.world.items.data.ItemLore
import com.wolfyscript.utilities.world.items.data.LodestoneTracker
import com.wolfyscript.utilities.world.items.data.MapDecorations
import com.wolfyscript.utilities.world.items.data.PotionContents
import com.wolfyscript.utilities.world.items.data.Profile
import com.wolfyscript.utilities.world.items.data.ShieldBannerBaseColor
import com.wolfyscript.utilities.world.items.data.SuspiciousStew
import com.wolfyscript.utilities.world.items.data.Trim
import com.wolfyscript.utilities.world.items.data.Unbreakable
import com.wolfyscript.utilities.world.items.data.WrittenBookContents
import net.kyori.adventure.text.Component
import javax.xml.crypto.Data

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

    fun baseColor() : DataKey<DyedColor>

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
