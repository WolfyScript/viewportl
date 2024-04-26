package com.wolfyscript.utilities.bukkit.world.items

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.eval.context.EvalContext
import com.wolfyscript.utilities.eval.operator.BoolOperatorConst
import com.wolfyscript.utilities.eval.value_provider.*
import com.wolfyscript.utilities.nbt.*
import com.wolfyscript.utilities.platform.adapters.ItemStack
import com.wolfyscript.utilities.versioning.MinecraftVersion
import com.wolfyscript.utilities.versioning.ServerVersion
import com.wolfyscript.utilities.world.items.ItemStackConfig
import de.tr7zw.changeme.nbtapi.NBTCompound
import de.tr7zw.changeme.nbtapi.NBTItem
import de.tr7zw.changeme.nbtapi.NBTList
import de.tr7zw.changeme.nbtapi.NBTType
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT
import de.tr7zw.changeme.nbtapi.iface.ReadableNBTList
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import java.util.function.BiFunction
import java.util.stream.Collectors

class BukkitItemStackConfig : ItemStackConfig {
    private val usePaperDisplayOptions: Boolean
    private val HANDLED_NBT_TAGS = setOf("display.Name", "display.Lore", "CustomModelData", "Damage", "Enchantments")

    @JsonCreator
    constructor(@JacksonInject wolfyUtils: WolfyUtils, @JsonProperty("itemId") itemId: String) : super(
        wolfyUtils,
        itemId
    ) {
        this.usePaperDisplayOptions = wolfyUtils.core.platform.type.isPaper() && ServerVersion.isAfterOrEq(
            MinecraftVersion.of(1, 18, 2)
        )
    }

    constructor(wolfyUtils: WolfyUtils, wrappedStack: ItemStack) : super(
        wolfyUtils,
        (wrappedStack as ItemStackImpl).bukkitRef!!.type.key.toString()
    ) {
        val stack = wrappedStack.bukkitRef

        this.usePaperDisplayOptions = wolfyUtils.core.platform.type.isPaper() && ServerVersion.isAfterOrEq(
            MinecraftVersion.of(1, 18, 2)
        )

        // Read from ItemStack
        this.amount = ValueProviderIntegerConst(stack!!.amount)
        val meta = stack.itemMeta
        if (meta != null) {
            val miniMsg = wolfyUtils.chat.miniMessage
            if (usePaperDisplayOptions) {
                if (meta.hasDisplayName()) {
                    this.name(miniMsg.serialize(meta.displayName()!!))
                }
                if (meta.hasLore()) {
                    this.lore = meta.lore()!!
                        .map<Component, ValueProvider<String>> { ValueProviderStringConst(miniMsg.serialize(it)) }
                        .toList()
                }
            } else {
                // First need to convert the Strings to Component and then back to mini message!
                if (meta.hasDisplayName()) {
                    this.name(miniMsg.serialize(BukkitComponentSerializer.legacy().deserialize(meta.displayName)))
                }
                if (meta.hasLore()) {
                    this.lore = meta.lore!!
                        .stream().map { s: String? ->
                            ValueProviderStringConst(
                                miniMsg.serialize(
                                    BukkitComponentSerializer.legacy().deserialize(
                                        s!!
                                    )
                                )
                            )
                        }.toList()
                }
            }
            this.unbreakable = BoolOperatorConst(wolfyUtils, meta.isUnbreakable)
            this.customModelData =
                if (meta.hasCustomModelData()) ValueProviderIntegerConst(meta.customModelData) else null
        }
        this.enchants = stack.enchantments.entries.stream().collect(
            Collectors.toMap<Map.Entry<Enchantment, Int?>, String, ValueProviderIntegerConst>(
                { entry: Map.Entry<Enchantment, Int?> -> entry.key.key.toString() },
                { entry: Map.Entry<Enchantment, Int?> ->
                    ValueProviderIntegerConst(
                        entry.value!!
                    )
                })
        )

        this.nbt = if (stack.type != Material.AIR && stack.amount > 0) readFromItemStack(
            NBTItem(stack),
            "",
            null
        ) else NBTTagConfigCompound(wolfyUtils, null)
    }

    override fun constructItemStack(): ItemStackImpl? {
        return constructItemStack(EvalContext())
    }

    override fun constructItemStack(context: EvalContext?): ItemStackImpl? {
        return constructItemStack(context, wolfyUtils.chat.miniMessage, TagResolver.empty())
    }

    override fun constructItemStack(
        context: EvalContext?,
        miniMsg: MiniMessage?,
        tagResolvers: TagResolver?
    ): ItemStackImpl? {
        val type = Material.matchMaterial(itemId)
        if (type != null) {
            var itemStack = org.bukkit.inventory.ItemStack(type)
            itemStack.amount = amount.getValue(context)

            // Apply the NBT of the stack
            if (type != Material.AIR && itemStack.amount > 0) {
                val nbtItem = NBTItem(itemStack)
                applyCompound(nbtItem, nbt, context)
                itemStack = nbtItem.item
            }

            // Apply ItemMeta afterwards to override possible NBT Tags
            val meta = itemStack.itemMeta
            if (meta != null) {
                // Apply Display options
                this.name?.apply {
                    val nameVal = getValue(context)
                    if (usePaperDisplayOptions) {
                        if (nameVal != null) {
                            meta.displayName(miniMsg!!.deserialize(nameVal, tagResolvers!!))
                        }
                    } else {
                        if (nameVal != null) {
                            meta.setDisplayName(
                                BukkitComponentSerializer.legacy().serialize(
                                    miniMsg!!.deserialize(nameVal, tagResolvers!!)
                                )
                            )
                        }
                    }
                }

                this.lore.apply {
                    if (isEmpty()) return@apply
                    if (usePaperDisplayOptions) {
                        if (isNotEmpty()) {
                            meta.lore(
                                lore.map { provider ->
                                    miniMsg!!.deserialize(provider.getValue(context), tagResolvers!!)
                                }
                            )
                        }
                    } else {
                        meta.lore = lore.map { provider ->
                            BukkitComponentSerializer.legacy().serialize(
                                miniMsg!!.deserialize(provider.getValue(context)!!, tagResolvers!!)
                            )
                        }
                    }
                }

                // Apply enchants
                for ((key, value) in enchants) {
                    val enchant = Enchantment.getByKey(
                        NamespacedKey.fromString(
                            key
                        )
                    )
                    if (enchant != null) {
                        meta.addEnchant(enchant, value.getValue(context)!!, true)
                    }
                }

                if (customModelData != null) {
                    meta.setCustomModelData(customModelData!!.getValue(context))
                }

                meta.isUnbreakable = unbreakable.evaluate(context)

                itemStack.setItemMeta(meta)
            }
            return ItemStackImpl((wolfyUtils as WolfyUtilsBukkit), itemStack)
        }
        return null
    }

    private fun readFromItemStack(
        currentCompound: ReadableNBT,
        path: String,
        parent: NBTTagConfig?
    ): NBTTagConfigCompound {
        val configCompound = NBTTagConfigCompound(wolfyUtils, parent)
        val children: MutableMap<String, NBTTagConfig> = HashMap()
        for (key in currentCompound.keys) {
            val childPath = if (path.isEmpty()) key else ("$path.$key")
            if (HANDLED_NBT_TAGS.contains(childPath)) {
                // Skip already handled NBT Tags, so they are not both in common and NBT settings!
                continue
            }
            val childConfig = when (currentCompound.getType(key)) {
                NBTType.NBTTagCompound -> {
                    val readConfigCompound =
                        readFromItemStack(currentCompound.getCompound(key), childPath, configCompound)
                    if (readConfigCompound.children.isEmpty()) {
                        null
                    }
                    readConfigCompound
                }

                NBTType.NBTTagList -> when (currentCompound.getListType(key)) {
                    NBTType.NBTTagCompound -> {
                        val compoundConfigList = NBTTagConfigListCompound(wolfyUtils, parent, listOf())
                        val compoundList = currentCompound.getCompoundList(key)
                        val elements: MutableList<NBTTagConfigCompound> = ArrayList()
                        for ((index, listCompound) in compoundList.withIndex()) {
                            elements.add(readFromItemStack(listCompound, "$childPath.$index", compoundConfigList))
                        }
                        compoundConfigList.values = elements
                        compoundConfigList
                    }

                    NBTType.NBTTagInt -> readPrimitiveList(
                        currentCompound.getIntegerList(key),
                        NBTTagConfigListInt(wolfyUtils, configCompound, ArrayList())
                    ) { listInt: NBTTagConfigListPrimitive<Int?, NBTTagConfigInt?>?, integer: Int? ->
                        NBTTagConfigInt(
                            wolfyUtils, listInt, ValueProviderIntegerConst(
                                integer!!
                            )
                        )
                    }

                    NBTType.NBTTagIntArray -> readPrimitiveList(
                        currentCompound.getIntArrayList(key),
                        NBTTagConfigListIntArray(wolfyUtils, configCompound, ArrayList())
                    ) { listIntArray: NBTTagConfigListPrimitive<IntArray?, NBTTagConfigIntArray?>?, intArray ->
                        NBTTagConfigIntArray(
                            wolfyUtils,
                            listIntArray,
                            ValueProviderIntArrayConst(intArray)
                        )
                    }

                    NBTType.NBTTagLong -> readPrimitiveList(
                        currentCompound.getLongList(key),
                        NBTTagConfigListLong(wolfyUtils, configCompound, ArrayList())
                    ) { listConfig: NBTTagConfigListPrimitive<Long?, NBTTagConfigLong?>?, aLong: Long? ->
                        NBTTagConfigLong(
                            wolfyUtils, listConfig, ValueProviderLongConst(
                                aLong!!
                            )
                        )
                    }

                    NBTType.NBTTagFloat -> readPrimitiveList(
                        currentCompound.getFloatList(key),
                        NBTTagConfigListFloat(wolfyUtils, configCompound, ArrayList())
                    ) { listConfig: NBTTagConfigListPrimitive<Float?, NBTTagConfigFloat?>?, aFloat: Float? ->
                        NBTTagConfigFloat(
                            wolfyUtils, listConfig, ValueProviderFloatConst(
                                aFloat!!
                            )
                        )
                    }

                    NBTType.NBTTagDouble -> readPrimitiveList(
                        currentCompound.getDoubleList(key),
                        NBTTagConfigListDouble(wolfyUtils, configCompound, ArrayList())
                    ) { listConfig: NBTTagConfigListPrimitive<Double?, NBTTagConfigDouble?>?, aDouble: Double? ->
                        NBTTagConfigDouble(
                            wolfyUtils, listConfig, ValueProviderDoubleConst(
                                aDouble!!
                            )
                        )
                    }

                    NBTType.NBTTagString -> readPrimitiveList(
                        currentCompound.getStringList(key),
                        NBTTagConfigListString(wolfyUtils, configCompound, ArrayList())
                    ) { listConfig: NBTTagConfigListPrimitive<String?, NBTTagConfigString?>?, aString: String? ->
                        NBTTagConfigString(
                            wolfyUtils,
                            listConfig,
                            ValueProviderStringConst(aString ?: "")
                        )
                    }

                    else -> null
                }

                NBTType.NBTTagByte -> NBTTagConfigByte(
                    wolfyUtils,
                    configCompound,
                    ValueProviderByteConst(currentCompound.getByte(key))
                )

                NBTType.NBTTagByteArray -> NBTTagConfigByteArray(
                    wolfyUtils,
                    configCompound,
                    ValueProviderByteArrayConst(currentCompound.getByteArray(key))
                )

                NBTType.NBTTagShort -> NBTTagConfigShort(
                    wolfyUtils,
                    configCompound,
                    ValueProviderShortConst(currentCompound.getShort(key))
                )

                NBTType.NBTTagInt -> NBTTagConfigInt(
                    wolfyUtils,
                    configCompound,
                    ValueProviderIntegerConst(currentCompound.getInteger(key))
                )

                NBTType.NBTTagIntArray -> NBTTagConfigIntArray(
                    wolfyUtils,
                    configCompound,
                    ValueProviderIntArrayConst(currentCompound.getIntArray(key))
                )

                NBTType.NBTTagLong -> NBTTagConfigLong(
                    wolfyUtils,
                    configCompound,
                    ValueProviderLongConst(currentCompound.getLong(key))
                )

                NBTType.NBTTagFloat -> NBTTagConfigFloat(
                    wolfyUtils,
                    configCompound,
                    ValueProviderFloatConst(currentCompound.getFloat(key))
                )

                NBTType.NBTTagDouble -> NBTTagConfigDouble(
                    wolfyUtils,
                    configCompound,
                    ValueProviderDoubleConst(currentCompound.getDouble(key))
                )

                NBTType.NBTTagString -> NBTTagConfigString(
                    wolfyUtils,
                    configCompound,
                    ValueProviderStringConst(currentCompound.getString(key))
                )

                else -> null
            }
            if (childConfig != null) {
                children[key] = childConfig
            }
        }
        configCompound.children = children
        return configCompound
    }

    /**
     * Reads the elements of a NBTList and converts them, using the given function, to the NBTTagConfig.
     *
     * @param nbtList            The NBTList from the NBTItemAPI
     * @param configList         The instance of the NBTTagConfigList to load the elements into.
     * @param elementConstructor This constructs each element of list.
     * @param <T>                The primitive data type.
     * @param <VAL>              The type of the Element config.
     * @return The configList instance with the new elements.
    </VAL></T> */
    private fun <T, VAL : NBTTagConfigPrimitive<T>?> readPrimitiveList(
        nbtList: ReadableNBTList<T>,
        configList: NBTTagConfigListPrimitive<T, VAL>,
        elementConstructor: BiFunction<NBTTagConfigListPrimitive<T, VAL>, T, VAL>
    ): NBTTagConfigListPrimitive<T, VAL> {
        configList.values =
            nbtList.toListCopy().stream().map { value: T -> elementConstructor.apply(configList, value) }
                .toList()
        return configList
    }

    private fun applyCompound(compound: NBTCompound, config: NBTTagConfigCompound, context: EvalContext?) {
        for ((tagName, tagConfig) in config.children) {
            if (tagConfig is NBTTagConfigCompound) {
                applyCompound(compound.addCompound(tagName), tagConfig, context)
            } else if (tagConfig is NBTTagConfigList<*>) {
                applyList(compound, tagName, tagConfig, context)
            } else if (tagConfig is NBTTagConfigByte) {
                compound.setByte(tagName, tagConfig.value.getValue(context))
            } else if (tagConfig is NBTTagConfigByteArray) {
                compound.setByteArray(tagName, tagConfig.value.getValue(context))
            } else if (tagConfig is NBTTagConfigShort) {
                compound.setShort(tagName, tagConfig.value.getValue(context))
            } else if (tagConfig is NBTTagConfigInt) {
                compound.setInteger(tagName, tagConfig.value.getValue(context))
            } else if (tagConfig is NBTTagConfigIntArray) {
                compound.setIntArray(tagName, tagConfig.value.getValue(context))
            } else if (tagConfig is NBTTagConfigLong) {
                compound.setLong(tagName, tagConfig.value.getValue(context))
            } else if (tagConfig is NBTTagConfigFloat) {
                compound.setFloat(tagName, tagConfig.value.getValue(context))
            } else if (tagConfig is NBTTagConfigDouble) {
                compound.setDouble(tagName, tagConfig.value.getValue(context))
            } else if (tagConfig is NBTTagConfigString) {
                compound.setString(tagName, tagConfig.value.getValue(context))
            } else if (tagConfig is NBTTagConfigBoolean) {
                compound.setBoolean(tagName, tagConfig.getValue(context))
            }
        }
    }

    private fun applyList(
        compound: NBTCompound,
        tagName: String,
        configList: NBTTagConfigList<*>,
        context: EvalContext?
    ) {
        if (configList is NBTTagConfigListCompound) {
            val list = compound.getCompoundList(tagName)
            for (element in configList.elements) {
                applyCompound(list.addCompound(), element.value, context)
            }
        } else if (configList is NBTTagConfigListInt) {
            applyPrimitiveList(compound.getIntegerList(tagName), configList, context)
        } else if (configList is NBTTagConfigListLong) {
            applyPrimitiveList(compound.getLongList(tagName), configList, context)
        } else if (configList is NBTTagConfigListFloat) {
            applyPrimitiveList(compound.getFloatList(tagName), configList, context)
        } else if (configList is NBTTagConfigListDouble) {
            applyPrimitiveList(compound.getDoubleList(tagName), configList, context)
        } else if (configList is NBTTagConfigListString) {
            applyPrimitiveList(compound.getStringList(tagName), configList, context)
        } else if (configList is NBTTagConfigListIntArray) {
            applyPrimitiveList(compound.getIntArrayList(tagName), configList, context)
        }
    }

    private fun <T> applyPrimitiveList(
        nbtList: NBTList<T>,
        configPrimitive: NBTTagConfigList<out NBTTagConfigPrimitive<T>?>,
        context: EvalContext?
    ) {
        for (element in configPrimitive.elements) {
            nbtList.add(element.value!!.value.getValue(context)) // This looks weird, but it will provide more options in the future.
        }
    }

    override fun toString(): String {
        return "BukkitItemStackConfig{" +
                "itemId='" + itemId + '\'' +
                ", name=" + name +
                ", lore=" + lore +
                ", amount=" + amount +
                ", repairCost=" + repairCost +
                ", damage=" + damage +
                ", unbreakable=" + unbreakable +
                ", customModelData=" + customModelData +
                ", enchants=" + enchants +
                ", nbt=" + nbt +
                "} "
    }
}
