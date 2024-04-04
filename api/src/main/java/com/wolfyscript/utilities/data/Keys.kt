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
        val DAMAGE = register<Int>("damage")
        val REPAIR_COST = register<Int>("repair_cost")
        val UNBREAKABLE = register<Unbreakable>("unbreakable")
        val ENCHANTMENTS = register<Enchantments>("enchantments")
        val STORED_ENCHANTMENTS = register<Enchantments>("stored_enchantments")
        val CUSTOM_NAME = register<Component>("custom_name")
        val ITEM_LORE = register<ItemLore>("item_lore")
        val CAN_BREAK = register<CanBreak>("can_break")
        val CAN_PLACE_ON = register<CanPlaceOn>("can_place_on")
        val DYED_COLOR = register<DyedColor>("dyed_color")
        val ATTRIBUTE_MODIFIERS = register<AttributeModifiers>("attribute_modifiers")
        val CHARGED_PROJECTILES = register<ChargedProjectiles>("charged_projectiles")
        val INTANGIBLE_PROJECTILES = register<IntangibleProjectiles>("intangible_projectiles")
        val MAP_COLOR = register<MapColor>("map_color")
        val MAP_DECORATIONS = register<MapDecorations>("map_decorations")
        val MAP_ID = register<Int>("map_id")
        val MAP_INFO = register<MapInfo>("map_info")
        val CUSTOM_MODEL_DATA = register<Int>("custom_model_data")
        val POTION_CONTENTS = register<PotionContents>("potion_contents")
        val WRITABLE_BOOK_CONTENTS = register<WriteableBookContents>("writable_book_contents")
        val WRITTEN_BOOK_CONTENTS = register<WrittenBookContents>("written_book_contents")
        val TRIM = register<Trim>("trim")
        val SUSPICIOUS_STEW = register<SuspiciousStew>("suspicious_stew")
        val HIDE_ADDITIONAL_TOOLTIP = register<HideAdditionalTooltip>("hide_additional_tooltip")
        val DEBUG_STICK_STATE = register<DebugStickState>("debug_stick_state")
        val ENTITY_DATA = register<EntityData>("entity_data")
        val BUCKET_ENTITY_DATA = register<BucketEntityData>("bucket_entity_data")
        val INSTRUMENT = register<NamespacedKey>("instrument")
        val RECIPES = register<List<NamespacedKey>>("recipes")
        val LODESTONE_TRACKER = register<LodestoneTracker>("lodestone_tracker")
        val FIREWORKS_EXPLOSION = register<FireworkExplosion>("firework_explosion")
        val FIREWORKS = register<Fireworks>("fireworks")
        val PROFILE = register<Profile>("profile")
        val NOTE_BLOCK_SOUND = register<NamespacedKey>("note_block_sound")
        val BASE_COLOR = register<DyeColor>("base_color")
        val BANNER_PATTERNS = register<BannerPatterns>("banner_patterns")
        val POT_DECORATIONS = register<List<NamespacedKey>>("pot_decorations")
        val CONTAINER = register<Container>("container")
        val BEES = register<Bees>("bees")
        val LOCK = register<String>("lock")
        val CONTAINER_LOOT = register<ContainerLoot>("container_loot")
        val BLOCK_ENTITY_DATA = register<BlockEntityData>("block_entity_data")
        val BLOCK_STATE = register<BlockState>("block_state")
        val ENCHANTMENT_GLINT_OVERRIDE = register<Boolean>("enchantment_glint_override")

        /****************
         * EXPERIMENTAL
         ****************/

        val BUNDLE_CONTENTS = register<BundleContents>("bundle_contents")

        // 1.20.5+
        // TODO

        // 1.21+
        // TODO

        // ***********

        private val ids = WolfyCore.instance().wolfyUtils.identifiers

        fun <T : Any> register(type: KClass<T>, key: NamespacedKey): DataKey<T, ItemStack> {
            return WolfyCore.instance().platform().items().dataKeyProvider().getKeyBuilder(type, key).build()
        }

        fun <T : Any> register(type: KClass<T>, key: String): DataKey<T, ItemStack> {
            return WolfyCore.instance().platform().items().dataKeyProvider()
                .getKeyBuilder(type, ids.getNamespaced("minecraft", key)).build()
        }

        fun <T : Any> register(type: KClass<T>, namespace: String, key: String): DataKey<T, ItemStack> {
            return WolfyCore.instance().platform().items().dataKeyProvider()
                .getKeyBuilder(type, ids.getNamespaced(namespace, key)).build()
        }

        inline fun <reified T : Any> register(key: String): DataKey<T, ItemStack> {
            return register(T::class, key)
        }
    }

}
