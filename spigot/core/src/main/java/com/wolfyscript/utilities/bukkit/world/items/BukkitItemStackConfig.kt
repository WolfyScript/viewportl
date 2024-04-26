package com.wolfyscript.utilities.bukkit.world.items;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.platform.adapters.ItemStack;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl;
import com.wolfyscript.utilities.world.items.ItemStackConfig;
import com.wolfyscript.utilities.nbt.NBTTagConfig;
import com.wolfyscript.utilities.nbt.NBTTagConfigBoolean;
import com.wolfyscript.utilities.nbt.NBTTagConfigByte;
import com.wolfyscript.utilities.nbt.NBTTagConfigByteArray;
import com.wolfyscript.utilities.nbt.NBTTagConfigCompound;
import com.wolfyscript.utilities.nbt.NBTTagConfigDouble;
import com.wolfyscript.utilities.nbt.NBTTagConfigFloat;
import com.wolfyscript.utilities.nbt.NBTTagConfigInt;
import com.wolfyscript.utilities.nbt.NBTTagConfigIntArray;
import com.wolfyscript.utilities.nbt.NBTTagConfigList;
import com.wolfyscript.utilities.nbt.NBTTagConfigListCompound;
import com.wolfyscript.utilities.nbt.NBTTagConfigListDouble;
import com.wolfyscript.utilities.nbt.NBTTagConfigListFloat;
import com.wolfyscript.utilities.nbt.NBTTagConfigListInt;
import com.wolfyscript.utilities.nbt.NBTTagConfigListIntArray;
import com.wolfyscript.utilities.nbt.NBTTagConfigListLong;
import com.wolfyscript.utilities.nbt.NBTTagConfigListPrimitive;
import com.wolfyscript.utilities.nbt.NBTTagConfigListString;
import com.wolfyscript.utilities.nbt.NBTTagConfigLong;
import com.wolfyscript.utilities.nbt.NBTTagConfigPrimitive;
import com.wolfyscript.utilities.nbt.NBTTagConfigShort;
import com.wolfyscript.utilities.nbt.NBTTagConfigString;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.eval.operator.BoolOperatorConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderByteArrayConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderByteConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderDoubleConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderFloatConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderIntArrayConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderIntegerConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderLongConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderShortConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringConst;
import com.wolfyscript.utilities.versioning.MinecraftVersion;
import com.wolfyscript.utilities.versioning.ServerVersion;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBTList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class BukkitItemStackConfig extends ItemStackConfig {

    private final boolean usePaperDisplayOptions;
    private final Set<String> HANDLED_NBT_TAGS = Set.of("display.Name", "display.Lore", "CustomModelData", "Damage", "Enchantments");

    @JsonCreator
    public BukkitItemStackConfig(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("itemId") String itemId) {
        super(wolfyUtils, itemId);
        this.usePaperDisplayOptions = wolfyUtils.getCore().getPlatform().getType().isPaper() && ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 18, 2));
    }

    public BukkitItemStackConfig(WolfyUtils wolfyUtils, ItemStack wrappedStack) {
        super(wolfyUtils, ((ItemStackImpl) wrappedStack).getBukkitRef().getType().getKey().toString());
        org.bukkit.inventory.ItemStack stack = ((ItemStackImpl) wrappedStack).getBukkitRef();

        this.usePaperDisplayOptions = wolfyUtils.getCore().getPlatform().getType().isPaper() && ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 18, 2));

        // Read from ItemStack
        this.amount = new ValueProviderIntegerConst(wolfyUtils, stack.getAmount());
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            MiniMessage miniMsg = wolfyUtils.getChat().getMiniMessage();
            if (usePaperDisplayOptions) {
                if (meta.hasDisplayName()) {
                    this.name = new ValueProviderStringConst(wolfyUtils, miniMsg.serialize(meta.displayName()));
                }
                if (meta.hasLore()) {
                    this.lore = meta.lore().stream().map(component -> new ValueProviderStringConst(wolfyUtils, miniMsg.serialize(component))).toList();
                }
            } else {
                // First need to convert the Strings to Component and then back to mini message!
                if (meta.hasDisplayName()) {
                    this.name = new ValueProviderStringConst(wolfyUtils, miniMsg.serialize(BukkitComponentSerializer.legacy().deserialize(meta.getDisplayName())));
                }
                if (meta.hasLore()) {
                    this.lore = meta.getLore().stream().map(s -> new ValueProviderStringConst(wolfyUtils, miniMsg.serialize(BukkitComponentSerializer.legacy().deserialize(s)))).toList();
                }
            }
            this.unbreakable = new BoolOperatorConst(wolfyUtils, meta.isUnbreakable());
            this.customModelData = meta.hasCustomModelData() ? new ValueProviderIntegerConst(wolfyUtils, meta.getCustomModelData()) : null;
        }
        this.enchants = stack.getEnchantments().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getKey().toString(), entry -> new ValueProviderIntegerConst(wolfyUtils, entry.getValue())));

        this.nbt = stack.getType() != Material.AIR && stack.getAmount() > 0 ? readFromItemStack(new NBTItem(stack), "", null) : new NBTTagConfigCompound(wolfyUtils, null);
    }

    @Override
    public ItemStackImpl constructItemStack() {
        return constructItemStack(new EvalContext());
    }

    @Override
    public ItemStackImpl constructItemStack(EvalContext context) {
        return constructItemStack(context, wolfyUtils.getChat().getMiniMessage(), TagResolver.empty());
    }

    @Override
    public ItemStackImpl constructItemStack(EvalContext context, MiniMessage miniMsg, TagResolver tagResolvers) {
        Material type = Material.matchMaterial(itemId);
        if (type != null) {
            var itemStack = new org.bukkit.inventory.ItemStack(type);
            itemStack.setAmount(amount.getValue(context));

            // Apply the NBT of the stack
            if (type != Material.AIR && itemStack.getAmount() > 0) {
                NBTItem nbtItem = new NBTItem(itemStack);
                applyCompound(nbtItem, getNbt(), context);
                itemStack = nbtItem.getItem();
            }

            // Apply ItemMeta afterwards to override possible NBT Tags
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                // Apply Display options
                String nameVal = this.name == null ? null : this.name.getValue(context);
                if (usePaperDisplayOptions) {
                    if (nameVal != null) {
                        meta.displayName(miniMsg.deserialize(nameVal, tagResolvers));
                    }
                    if (this.lore != null && !this.lore.isEmpty()) {
                        meta.lore(this.lore.stream().filter(Objects::nonNull).map(stringValueProvider -> miniMsg.deserialize(stringValueProvider.getValue(context), tagResolvers)).toList());
                    }
                } else {
                    if (nameVal != null) {
                        meta.setDisplayName(BukkitComponentSerializer.legacy().serialize(miniMsg.deserialize(nameVal, tagResolvers)));
                    }
                    if (lore != null) {
                        meta.setLore(lore.stream().map(line -> BukkitComponentSerializer.legacy().serialize(miniMsg.deserialize(line.getValue(context), tagResolvers))).toList());
                    }
                }
                // Apply enchants
                for (Map.Entry<String, ? extends ValueProvider<Integer>> entry : enchants.entrySet()) {
                    Enchantment enchant = Enchantment.getByKey(NamespacedKey.fromString(entry.getKey()));
                    if (enchant != null) {
                        meta.addEnchant(enchant, entry.getValue().getValue(context), true);
                    }
                }

                if (customModelData != null) {
                    meta.setCustomModelData(customModelData.getValue(context));
                }

                meta.setUnbreakable(unbreakable.evaluate(context));

                itemStack.setItemMeta(meta);
            }
            return new ItemStackImpl((WolfyUtilsBukkit) wolfyUtils, itemStack);
        }
        return null;
    }

    private NBTTagConfigCompound readFromItemStack(ReadableNBT currentCompound, String path, NBTTagConfig parent) {
        NBTTagConfigCompound configCompound = new NBTTagConfigCompound(wolfyUtils, parent);
        Map<String, NBTTagConfig> children = new HashMap<>();
        for (String key : currentCompound.getKeys()) {
            String childPath = path.isEmpty() ? key : (path + "." + key);
            if (HANDLED_NBT_TAGS.contains(childPath)) {
                // Skip already handled NBT Tags, so they are not both in common and NBT settings!
                continue;
            }
            NBTTagConfig childConfig = switch (currentCompound.getType(key)) {
                case NBTTagCompound -> {
                    NBTTagConfigCompound readConfigCompound = readFromItemStack(currentCompound.getCompound(key), childPath, configCompound);
                    if (readConfigCompound.getChildren().isEmpty()) {
                        yield null;
                    }
                    yield readConfigCompound;
                }
                case NBTTagList -> switch (currentCompound.getListType(key)) {
                    case NBTTagCompound -> {
                        NBTTagConfigListCompound compoundConfigList = new NBTTagConfigListCompound(wolfyUtils, parent, List.of());
                        ReadableNBTList<ReadWriteNBT> compoundList = currentCompound.getCompoundList(key);
                        List<NBTTagConfigCompound> elements = new ArrayList<>();
                        int index = 0;
                        for (ReadWriteNBT listCompound : compoundList) {
                            elements.add(readFromItemStack(listCompound, childPath + "." + index, compoundConfigList));
                            index++;
                        }
                        compoundConfigList.setValues(elements);
                        yield compoundConfigList;
                    }
                    case NBTTagInt ->
                            readPrimitiveList(currentCompound.getIntegerList(key), new NBTTagConfigListInt(wolfyUtils, configCompound, new ArrayList<>()), (listInt, integer) -> new NBTTagConfigInt(wolfyUtils, listInt, new ValueProviderIntegerConst(wolfyUtils, integer)));
                    case NBTTagIntArray ->
                            readPrimitiveList(currentCompound.getIntArrayList(key), new NBTTagConfigListIntArray(wolfyUtils, configCompound, new ArrayList<>()), (listIntArray, intArray) -> new NBTTagConfigIntArray(wolfyUtils, listIntArray, new ValueProviderIntArrayConst(wolfyUtils, intArray)));
                    case NBTTagLong ->
                            readPrimitiveList(currentCompound.getLongList(key), new NBTTagConfigListLong(wolfyUtils, configCompound, new ArrayList<>()), (listConfig, aLong) -> new NBTTagConfigLong(wolfyUtils, listConfig, new ValueProviderLongConst(wolfyUtils, aLong)));
                    case NBTTagFloat ->
                            readPrimitiveList(currentCompound.getFloatList(key), new NBTTagConfigListFloat(wolfyUtils, configCompound, new ArrayList<>()), (listConfig, aFloat) -> new NBTTagConfigFloat(wolfyUtils, listConfig, new ValueProviderFloatConst(wolfyUtils, aFloat)));
                    case NBTTagDouble ->
                            readPrimitiveList(currentCompound.getDoubleList(key), new NBTTagConfigListDouble(wolfyUtils, configCompound, new ArrayList<>()), (listConfig, aDouble) -> new NBTTagConfigDouble(wolfyUtils, listConfig, new ValueProviderDoubleConst(wolfyUtils, aDouble)));
                    case NBTTagString ->
                            readPrimitiveList(currentCompound.getStringList(key), new NBTTagConfigListString(wolfyUtils, configCompound, new ArrayList<>()), (listConfig, aString) -> new NBTTagConfigString(wolfyUtils, listConfig, new ValueProviderStringConst(wolfyUtils, aString)));
                    default -> null;
                };
                case NBTTagByte ->
                        new NBTTagConfigByte(wolfyUtils, configCompound, new ValueProviderByteConst(wolfyUtils, currentCompound.getByte(key)));
                case NBTTagByteArray ->
                        new NBTTagConfigByteArray(wolfyUtils, configCompound,new ValueProviderByteArrayConst(wolfyUtils, currentCompound.getByteArray(key)));
                case NBTTagShort ->
                        new NBTTagConfigShort(wolfyUtils, configCompound, new ValueProviderShortConst(wolfyUtils, currentCompound.getShort(key)));
                case NBTTagInt ->
                        new NBTTagConfigInt(wolfyUtils, configCompound, new ValueProviderIntegerConst(wolfyUtils, currentCompound.getInteger(key)));
                case NBTTagIntArray ->
                        new NBTTagConfigIntArray(wolfyUtils, configCompound, new ValueProviderIntArrayConst(wolfyUtils, currentCompound.getIntArray(key)));
                case NBTTagLong ->
                        new NBTTagConfigLong(wolfyUtils, configCompound, new ValueProviderLongConst(wolfyUtils, currentCompound.getLong(key)));
                case NBTTagFloat ->
                        new NBTTagConfigFloat(wolfyUtils, configCompound, new ValueProviderFloatConst(wolfyUtils, currentCompound.getFloat(key)));
                case NBTTagDouble ->
                        new NBTTagConfigDouble(wolfyUtils, configCompound, new ValueProviderDoubleConst(wolfyUtils, currentCompound.getDouble(key)));
                case NBTTagString ->
                        new NBTTagConfigString(wolfyUtils, configCompound, new ValueProviderStringConst(wolfyUtils, currentCompound.getString(key)));
                default -> null;
            };
            if (childConfig != null) {
                children.put(key, childConfig);
            }
        }
        configCompound.setChildren(children);
        return configCompound;
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
     */
    private <T, VAL extends NBTTagConfigPrimitive<T>> NBTTagConfigListPrimitive<T, VAL> readPrimitiveList(ReadableNBTList<T> nbtList, NBTTagConfigListPrimitive<T, VAL> configList, BiFunction<NBTTagConfigListPrimitive<T, VAL>, T, VAL> elementConstructor) {
        configList.setValues(nbtList.toListCopy().stream().map(value -> elementConstructor.apply(configList, value)).toList());
        return configList;
    }

    private void applyCompound(NBTCompound compound, NBTTagConfigCompound config, EvalContext context) {
        for (Map.Entry<String, NBTTagConfig> entry : config.getChildren().entrySet()) {
            var tagConfig = entry.getValue();
            var tagName = entry.getKey();
            if (tagConfig instanceof NBTTagConfigCompound configCompound) {
                applyCompound(compound.addCompound(entry.getKey()), configCompound, context);
            } else if (tagConfig instanceof NBTTagConfigList<?> configList) {
                applyList(compound, tagName, configList, context);
            } else if (tagConfig instanceof NBTTagConfigByte configByte) {
                compound.setByte(entry.getKey(), configByte.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigByteArray configByteArray) {
                compound.setByteArray(entry.getKey(), configByteArray.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigShort configShort) {
                compound.setShort(entry.getKey(), configShort.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigInt configInt) {
                compound.setInteger(entry.getKey(), configInt.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigIntArray configIntArray) {
                compound.setIntArray(entry.getKey(), configIntArray.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigLong configLong) {
                compound.setLong(entry.getKey(), configLong.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigFloat configFloat) {
                compound.setFloat(entry.getKey(), configFloat.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigDouble configDouble) {
                compound.setDouble(entry.getKey(), configDouble.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigString configString) {
                compound.setString(entry.getKey(), configString.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigBoolean configBoolean) {
                compound.setBoolean(entry.getKey(), configBoolean.getValue(context));
            }
        }
    }

    private void applyList(NBTCompound compound, String tagName, NBTTagConfigList<?> configList, EvalContext context) {
        if (configList instanceof NBTTagConfigListCompound configListCompound) {
            NBTCompoundList list = compound.getCompoundList(tagName);
            for (NBTTagConfigList.Element<NBTTagConfigCompound> element : configListCompound.getElements()) {
                applyCompound(list.addCompound(), element.getValue(), context);
            }
        } else if (configList instanceof NBTTagConfigListInt configListInt) {
            applyPrimitiveList(compound.getIntegerList(tagName), configListInt, context);
        } else if (configList instanceof NBTTagConfigListLong configListLong) {
            applyPrimitiveList(compound.getLongList(tagName), configListLong, context);
        } else if (configList instanceof NBTTagConfigListFloat configListFloat) {
            applyPrimitiveList(compound.getFloatList(tagName), configListFloat, context);
        } else if (configList instanceof NBTTagConfigListDouble configListDouble) {
            applyPrimitiveList(compound.getDoubleList(tagName), configListDouble, context);
        } else if (configList instanceof NBTTagConfigListString configListString) {
            applyPrimitiveList(compound.getStringList(tagName), configListString, context);
        } else if (configList instanceof NBTTagConfigListIntArray configListIntArray) {
            applyPrimitiveList(compound.getIntArrayList(tagName), configListIntArray, context);
        }
    }

    private <T> void applyPrimitiveList(NBTList<T> nbtList, NBTTagConfigList<? extends NBTTagConfigPrimitive<T>> configPrimitive, EvalContext context) {
        for (NBTTagConfigList.Element<? extends NBTTagConfigPrimitive<T>> element : configPrimitive.getElements()) {
            nbtList.add(element.getValue().getValue().getValue(context)); // This looks weird, but it will provide more options in the future.
        }
    }

    @Override
    public String toString() {
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
                "} ";
    }
}
