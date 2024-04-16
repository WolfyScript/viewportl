package com.wolfyscript.utilities.bukkit.data

import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyCore
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.bukkit.world.items.data.*
import com.wolfyscript.utilities.bukkit.world.items.toBukkit
import com.wolfyscript.utilities.bukkit.world.items.toWrapper
import com.wolfyscript.utilities.data.DataKey
import com.wolfyscript.utilities.data.DataKeyProvider
import com.wolfyscript.utilities.gui.functions.ReceiverBiConsumer
import com.wolfyscript.utilities.gui.functions.ReceiverFunction
import com.wolfyscript.utilities.platform.adapters.ItemStack
import com.wolfyscript.utilities.versioning.MinecraftVersion
import com.wolfyscript.utilities.versioning.ServerVersion
import com.wolfyscript.utilities.world.items.DyeColor
import com.wolfyscript.utilities.world.items.data.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Registry
import org.bukkit.block.Banner
import org.bukkit.block.DecoratedPot
import org.bukkit.block.Lockable
import org.bukkit.inventory.meta.*
import kotlin.reflect.KClass

class SpigotDataKeyProvider(private val wolfyCore: WolfyCore) : DataKeyProvider {

    private val map: MutableMap<NamespacedKey, DataKey<*, ItemStack>> = mutableMapOf()

    init {
        register("damage", {
            if (this !is Damageable) return@register null
            return@register damage
        }, {
            if (this !is Damageable) return@register
            damage = it
        })
        register<Int>("repair_cost", {
            if (this !is Repairable) return@register null
            this.repairCost
        }, {
            if (this !is Repairable) return@register
            repairCost = it
        })
        register<Unbreakable>("unbreakable", UnbreakableImpl.ITEM_META_CONVERTER)
        register<Enchantments>("enchantments", EnchantmentsImpl.ITEM_META_CONVERTER)
        register<Enchantments>("stored_enchantments", EnchantmentsImpl.ITEM_META_CONVERTER)
        register<Component>("custom_name", { displayName() }, { data -> displayName(data) })
        register<ItemLore>("item_lore", ItemLoreImpl.ITEM_META_CONVERTER)
        register<CanBreak>("can_break", CanBreakImpl.ITEM_META_CONVERTER)
        register<CanPlaceOn>("can_place_on", CanPlaceOnImpl.ITEM_META_CONVERTER)
        register<DyedColor>("dyed_color", DyedColorImpl.ITEM_META_CONVERTER)
        register<AttributeModifiers>("attribute_modifiers", AttributeModifiersImpl.ITEM_META_CONVERTER)
        register<ChargedProjectiles>("charged_projectiles", ChargedProjectilesImpl.ITEM_META_CONVERTER)
        register<IntangibleProjectiles>("intangible_projectiles", IntangibleProjectilesImpl.ITEM_META_CONVERTER)
        // TODO: Map Color
        // TODO: map Decorations
        register<Int>("map_id", {
            if (this !is MapMeta || !hasMapId()) return@register null
            mapId
        }, {
            if (this is MapMeta) {
                val map = Bukkit.getMap(it)
                if (map != null) {
                    mapView = map
                }
            }
        })
        // TODO: Map Info
        register("custom_model_data", {
            return@register if (hasCustomModelData()) {
                customModelData
            } else null
        }, {
            setCustomModelData(it)
        })
        register<PotionContents>("potion_contents", PotionContentsImpl.ITEM_META_CONVERTER)
        // TODO: Writable Book Contents
        // TODO: Written Book Contents
        // TODO: Trim
        // TODO: Suspicious Stew
        // TODO: Hide Additional Tooltip
        // TODO: Debug Stick State
        // TODO: Entity Data
        // TODO: Bucket Entity Data
        register<NamespacedKey>("instrument", {
            if (this is MusicInstrumentMeta) {
                return@register instrument?.key?.let { BukkitNamespacedKey.fromBukkit(it) }
            }
            null
        }, {
            if (this is MusicInstrumentMeta) {
                instrument = Registry.INSTRUMENT.get((it as BukkitNamespacedKey).bukkit())
            }
        })
        register<List<NamespacedKey>>("recipes", {
            if (this is KnowledgeBookMeta) {
                return@register recipes.map { BukkitNamespacedKey.fromBukkit(it) }
            }
            null
        }, { keys ->
            if (this is KnowledgeBookMeta) {
                recipes = keys.map { (it as BukkitNamespacedKey).bukkit() }
            }
        })
        // TODO: Lodestone Tracker
        // TODO: Fireworks Explosion
        // TODO: Fireworks
        register<Profile>("profile", ProfileImpl.ITEM_META_CONVERTER)
        register<NamespacedKey>("note_block_sound", {
            if (this is BlockStateMeta) {
                val state = blockState
                if (state is SkullMeta) {
                    return@register BukkitNamespacedKey.fromBukkit(state.noteBlockSound)
                }
            }
            null
        }, {
            if (this is BlockStateMeta) {
                val state = blockState
                if (state is SkullMeta) {
                    state.noteBlockSound = (it as BukkitNamespacedKey).bukkit()
                }
            }
        })
        register<DyeColor>("base_color", {
            if (this is BlockStateMeta) {
                val state = this.blockState
                if (state is Banner) {
                    return@register state.baseColor.toWrapper()
                }
            }
            null
        }, {
            if (this is BlockStateMeta) {
                val state = blockState
                if (state is Banner) {
                    state.baseColor = it.toBukkit()
                }
                blockState = state
            }
        })
        register<BannerPatterns>("banner_patterns", BannerPatternsImpl.ITEM_META_CONVERTER)
        register<List<NamespacedKey>>("pot_decorations", {
            if (this is BlockStateMeta) {
                val state = blockState
                if (state is DecoratedPot) {
                    val sherds = state.shards
                    return@register sherds.map {
                        BukkitNamespacedKey.fromBukkit(it.key)
                    }
                    /*
                buildList<NamespacedKey> {
                    // TODO: What is the order of shards here?
                    add(BukkitNamespacedKey.fromBukkit(sherds[DecoratedPot.Side.FRONT]!!.key))
                    add(BukkitNamespacedKey.fromBukkit(sherds[DecoratedPot.Side.LEFT]!!.key))
                    add(BukkitNamespacedKey.fromBukkit(sherds[DecoratedPot.Side.BACK]!!.key))
                    add(BukkitNamespacedKey.fromBukkit(sherds[DecoratedPot.Side.RIGHT]!!.key))
                }
                     */
                }
            }
            null
        }, {

        })
        register<Container>("container", ContainerImpl.ITEM_META_CONVERTER)
        register<Bees>("bees", BeesImpl.ITEM_META_CONVERTER)
        register<String>("lock", {
            if (this is BlockStateMeta) {
                val state = blockState
                if (state is Lockable) {
                    state.lock
                }
            }
            null
        }, {
            if (this !is BlockStateMeta) return@register
            val state = blockState
            if (state !is Lockable) return@register
            state.setLock(it)
            blockState = state
        })
        register<ContainerLoot>("container_loot", ContainerLootImpl.ITEM_META_CONVERTER)
        register<BlockEntityData>("block_entity_data", BlockEntityDataImpl.ITEM_META_CONVERTER)
        register<BlockState>("block_state", BlockStateImpl.ITEM_META_CONVERTER)
        register<Boolean>("enchantment_glint_override",
            {
                if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 5))) {
                    TODO("Not yet implemented")
                }
                null
            },
            { TODO("Not yet implemented") }
        )
        register<List<ItemStack>>("bundle_contents", {
            if (this is BundleMeta) {
                return@register items.map { ItemStackImpl(WolfyCoreCommon.instance.wolfyUtils, it) }
            }
            return@register emptyList()
        }, {
            if (this !is BundleMeta) return@register
            setItems(it.map { stack -> (stack as ItemStackImpl).bukkitRef })
        })

    }

    private inline fun <reified T : Any> register(
        key: String,
        converter: ItemMetaDataKeyConverter<T>
    ) {
        register(BukkitNamespacedKey("minecraft", key), converter.fetcher, converter.applier)
    }

    private inline fun <reified T : Any> register(
        key: String,
        fetcher: ReceiverFunction<ItemMeta, T?>,
        applier: ReceiverBiConsumer<ItemMeta, T>
    ) {
        register(BukkitNamespacedKey("minecraft", key), fetcher, applier)
    }

    private inline fun <reified T : Any> register(
        key: NamespacedKey,
        fetcher: ReceiverFunction<ItemMeta, T?>,
        applier: ReceiverBiConsumer<ItemMeta, T>
    ) {
        map[key] = DataKey(T::class, key,
            fetcher = {
                if (this is ItemStackImpl) {
                    return@DataKey bukkitRef?.itemMeta?.let { meta ->
                        with(fetcher) {
                            meta.apply()
                        }
                    }
                }
                null
            },
            applier = { data ->
                if (this is ItemStackImpl) {
                    val meta = bukkitRef?.itemMeta
                    if (meta != null) {
                        with(applier) { meta.consume(data) }
                        bukkitRef?.let {
                            bukkitRef!!.setItemMeta(meta)
                        }
                    }
                }
                this
            })
    }

    override fun <T : Any> getDataKey(
        type: KClass<T>,
        key: NamespacedKey
    ): DataKey<T, ItemStack> {
        val builder = map[key]
        if (builder != null) {
            if (builder.type != type) {
                throw IllegalArgumentException("Cannot create Builder $key! Invalid value type: Registered Builder contains value of type ${builder.type}, but requested value type was $type")
            }
            @Suppress("UNCHECKED_CAST") // We checked that the key and type is the same, so we can cast it here
            return builder as DataKey<T, ItemStack>
        }

        wolfyCore.wolfyUtils.logger.warning("Cannot create Builder $key! Builder was not registered! Falling back to empty DataKey!")
        return DataKey(type, key, fetcher = { null }, applier = { this })
    }

}