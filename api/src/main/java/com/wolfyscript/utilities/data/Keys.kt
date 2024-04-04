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
import com.wolfyscript.utilities.WolfyCore
import com.wolfyscript.utilities.platform.adapters.ItemStack
import com.wolfyscript.utilities.world.items.DyeColor
import com.wolfyscript.utilities.world.items.MapColor
import com.wolfyscript.utilities.world.items.data.*
import net.kyori.adventure.text.Component
import kotlin.reflect.KClass

interface Keys {

    companion object {
        @JvmField
        val DAMAGE = register<Int>("damage")

        @JvmField
        val REPAIR_COST = register<Int>("repair_cost")

        @JvmField
        val UNBREAKABLE = register<Unbreakable>("unbreakable")

        @JvmField
        val ENCHANTMENTS = register<Enchantments>("enchantments")

        @JvmField
        val STORED_ENCHANTMENTS = register<Enchantments>("stored_enchantments")

        @JvmField
        val CUSTOM_NAME = register<Component>("custom_name")

        @JvmField
        val ITEM_LORE = register<ItemLore>("item_lore")

        @JvmField
        val CAN_BREAK = register<CanBreak>("can_break")

        @JvmField
        val CAN_PLACE_ON = register<CanPlaceOn>("can_place_on")

        @JvmField
        val DYED_COLOR = register<DyedColor>("dyed_color")

        @JvmField
        val ATTRIBUTE_MODIFIERS = register<AttributeModifiers>("attribute_modifiers")

        @JvmField
        val CHARGED_PROJECTILES = register<ChargedProjectiles>("charged_projectiles")

        @JvmField
        val INTANGIBLE_PROJECTILES = register<IntangibleProjectiles>("intangible_projectiles")

        @JvmField
        val MAP_COLOR = register<MapColor>("map_color")

        @JvmField
        val MAP_DECORATIONS = register<MapDecorations>("map_decorations")

        @JvmField
        val MAP_ID = register<Int>("map_id")

        @JvmField
        val MAP_INFO = register<MapInfo>("map_info")

        @JvmField
        val CUSTOM_MODEL_DATA = register<Int>("custom_model_data")

        @JvmField
        val POTION_CONTENTS = register<PotionContents>("potion_contents")

        @JvmField
        val WRITABLE_BOOK_CONTENTS = register<WriteableBookContents>("writable_book_contents")

        @JvmField
        val WRITTEN_BOOK_CONTENTS = register<WrittenBookContents>("written_book_contents")

        @JvmField
        val TRIM = register<Trim>("trim")

        @JvmField
        val SUSPICIOUS_STEW = register<SuspiciousStew>("suspicious_stew")

        @JvmField
        val HIDE_ADDITIONAL_TOOLTIP = register<HideAdditionalTooltip>("hide_additional_tooltip")

        @JvmField
        val DEBUG_STICK_STATE = register<DebugStickState>("debug_stick_state")

        @JvmField
        val ENTITY_DATA = register<EntityData>("entity_data")

        @JvmField
        val BUCKET_ENTITY_DATA = register<BucketEntityData>("bucket_entity_data")

        @JvmField
        val INSTRUMENT = register<NamespacedKey>("instrument")

        @JvmField
        val RECIPES = register<List<NamespacedKey>>("recipes")

        @JvmField
        val LODESTONE_TRACKER = register<LodestoneTracker>("lodestone_tracker")

        @JvmField
        val FIREWORKS_EXPLOSION = register<FireworkExplosion>("firework_explosion")

        @JvmField
        val FIREWORKS = register<Fireworks>("fireworks")

        @JvmField
        val PROFILE = register<Profile>("profile")

        @JvmField
        val NOTE_BLOCK_SOUND = register<NamespacedKey>("note_block_sound")

        @JvmField
        val BASE_COLOR = register<DyeColor>("base_color")

        @JvmField
        val BANNER_PATTERNS = register<BannerPatterns>("banner_patterns")

        @JvmField
        val POT_DECORATIONS = register<List<NamespacedKey>>("pot_decorations")

        @JvmField
        val CONTAINER = register<Container>("container")

        @JvmField
        val BEES = register<Bees>("bees")

        @JvmField
        val LOCK = register<String>("lock")

        @JvmField
        val CONTAINER_LOOT = register<ContainerLoot>("container_loot")

        @JvmField
        val BLOCK_ENTITY_DATA = register<BlockEntityData>("block_entity_data")

        @JvmField
        val BLOCK_STATE = register<BlockState>("block_state")

        @JvmField
        val ENCHANTMENT_GLINT_OVERRIDE = register<Boolean>("enchantment_glint_override")

        /****************
         * EXPERIMENTAL
         ****************/

        @JvmField
        val BUNDLE_CONTENTS = register<BundleContents>("bundle_contents")

        // 1.20.5+
        // TODO

        // 1.21+
        // TODO

        // ***********

        fun <T : Any> register(type: KClass<T>, key: NamespacedKey): DataKey<T, ItemStack> {
            return WolfyCore.instance().platform().items().dataKeyBuilderProvider.getKeyBuilder(type, key).build()
        }

        fun <T : Any> register(type: KClass<T>, key: String): DataKey<T, ItemStack> {
            return WolfyCore.instance().platform().items().dataKeyBuilderProvider
                .getKeyBuilder(type, WolfyCore.instance().wolfyUtils.identifiers.getNamespaced("minecraft", key))
                .build()
        }

        fun <T : Any> register(type: KClass<T>, namespace: String, key: String): DataKey<T, ItemStack> {
            return WolfyCore.instance().platform().items().dataKeyBuilderProvider
                .getKeyBuilder(type, WolfyCore.instance().wolfyUtils.identifiers.getNamespaced(namespace, key)).build()
        }

        inline fun <reified T : Any> register(key: String): DataKey<T, ItemStack> {
            return register(T::class, key)
        }
    }

}
