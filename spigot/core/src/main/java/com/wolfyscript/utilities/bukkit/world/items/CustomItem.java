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

package com.wolfyscript.utilities.bukkit.world.items;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Streams;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.ItemsAdderIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.itemsadder.CustomStack;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.itemsadder.ItemsAdderStackIdentifier;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.AbstractItemBuilder;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.meta.CustomItemTagMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.Meta;
import com.wolfyscript.utilities.bukkit.world.items.meta.MetaSettings;
import com.wolfyscript.utilities.bukkit.world.items.reference.*;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleLocation;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.config.jackson.JacksonUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * This Object acts as a wrapper for the {@link StackReference} with additional options that make it possible to manipulate the behaviour of the wrapped reference.
 * <br>
 * </p>
 * <p>
 * For most additional features the CustomItem has to be registered into the {@link BukkitRegistries#getCustomItems()}.
 * <br>
 * To make sure the CustomItem can be detected later on, it must be created via any of the {@link #create()} methods.
 * <br>
 * These methods will include an extra {@link PersistentDataContainer} entry to identify the item later on!
 * </p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class CustomItem extends AbstractItemBuilder<CustomItem> implements Keyed {

    public static final org.bukkit.NamespacedKey PERSISTENT_KEY_TAG = new org.bukkit.NamespacedKey(((WolfyUtilsBukkit) WolfyCore.getInstance().getWolfyUtils()).getPlugin(), "custom_item");

    @JsonIgnore
    private final Material type;

    /**
     * This namespacedKey can either be null or non-null.
     * <p>
     * If it's non-null, the item is saved and the variables of this Object will be persistent </p>
     * when converted to ItemStack via {@link #create()}.
     * <p>
     * If it is null, the item isn't saved and the variables of this Object will get lost when {@link #create()} is called!
     */
    @JsonIgnore
    private NamespacedKey namespacedKey;
    @JsonIgnore
    private final Material craftRemain;

    @JsonAlias({"api_reference", "apiReference", "item"})
    private final StackReference reference;

    @JsonIgnore
    private final Map<NamespacedKey, CustomItemData> indexedData = new HashMap<>();

    @JsonAlias("equipment_slots")
    private final List<EquipmentSlot> equipmentSlots;
    private boolean consumed;
    private boolean blockVanillaEquip;
    private boolean blockPlacement;
    private boolean blockVanillaRecipes;
    @JsonAlias("rarity_percentage")
    private double rarityPercentage;
    private String permission;
    private MetaSettings nbtChecks;
    private StackReference replacement;
    @JsonAlias("fuel")
    private FuelSettings fuelSettings;
    @JsonAlias("durability_cost")
    private int durabilityCost;
    @JsonAlias("particles")
    private ParticleContent particleContent;
    private final ActionSettings actionSettings;
    private final CustomBlockSettings blockSettings;

    @JsonIgnore
    private boolean checkOldMetaSettings = true;

    @JsonCreator
    public CustomItem(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("actionSettings") ActionSettings actionSettings, @JsonProperty("reference") @JsonAlias({"item", "api_reference", "apiReference"}) StackReference reference) {
        super(wolfyUtils, CustomItem.class);
        this.reference = reference;

        this.namespacedKey = null;
        this.fuelSettings = new FuelSettings();
        setMetaSettings(new MetaSettings());
        this.permission = "";
        this.rarityPercentage = reference.weight() > 0 ? reference.weight() : 1.0d;
        this.equipmentSlots = new ArrayList<>();
        this.particleContent = new ParticleContent();
        this.blockSettings = new CustomBlockSettings();
        this.blockPlacement = false;
        this.blockVanillaEquip = false;
        this.blockVanillaRecipes = false;
        this.actionSettings = actionSettings == null ? new ActionSettings(wolfyUtils) : actionSettings;
        this.consumed = true;
        this.replacement = null;
        this.durabilityCost = 0;
        this.type = getItemStack() != null ? getItemStack().getType() : Material.AIR;
        this.craftRemain = getCraftRemain();
    }

    /**
     * Creates a CustomItem with a Vanilla Reference to the itemstack
     *
     * @param itemStack the itemstack this CustomItem will be linked to
     */
    public CustomItem(WolfyUtils wolfyUtils, ItemStack itemStack) {
        this(wolfyUtils, new ActionSettings(wolfyUtils), new StackReference((WolfyCoreCommon) WolfyCoreSpigot.getInstance(), BukkitStackIdentifier.ID, 1, 1, itemStack));
    }

    /**
     * Creates a CustomItem with a Vanilla Reference to an itemstack of the material
     *
     * @param material the material of the itemstack this CustomItem will be linked to
     */
    public CustomItem(WolfyUtils wolfyUtils, Material material) {
        this(wolfyUtils, new ItemStack(material));
    }

    /**
     * @param customItem A new deep copy of the passed in CustomItem.
     */
    private CustomItem(CustomItem customItem) {
        super(customItem.wolfyUtils, CustomItem.class);
        this.reference = customItem.reference.copy();

        this.namespacedKey = customItem.key();
        this.fuelSettings = customItem.fuelSettings.clone();
        this.blockSettings = customItem.blockSettings.copy();
        this.nbtChecks = customItem.nbtChecks;
        this.permission = customItem.permission;
        this.rarityPercentage = customItem.rarityPercentage;
        this.indexedData.clear();
        for (Map.Entry<NamespacedKey, CustomItemData> entry : customItem.indexedData.entrySet()) {
            this.indexedData.put(entry.getKey(), entry.getValue().copy());
        }
        this.equipmentSlots = new ArrayList<>(customItem.equipmentSlots);
        this.particleContent = customItem.particleContent;
        this.blockPlacement = customItem.blockPlacement;
        this.blockVanillaEquip = customItem.blockVanillaEquip;
        this.blockVanillaRecipes = customItem.blockVanillaRecipes;
        this.actionSettings = customItem.actionSettings; // TODO: Properly copy
        this.consumed = customItem.consumed;
        this.replacement = customItem.replacement;
        this.durabilityCost = customItem.durabilityCost;
        this.type = getItemStack() != null ? getItemStack().getType() : Material.AIR;
        this.craftRemain = getCraftRemain();
    }

    @JsonAnySetter
    protected void setOldProperties(String fieldKey, JsonNode value) throws JsonProcessingException {
        if (fieldKey.equals("advanced")) {
            checkOldMetaSettings = value.asBoolean();
        } else if (fieldKey.equals("metaSettings") || fieldKey.equals("meta")) {
            //Since the new system has its new field we need to update old appearances to the new system.
            JsonNode node = value.isTextual() ? JacksonUtil.getObjectMapper().readTree(value.asText()) : value;
            final List<Meta> checks;
            if (!node.has(MetaSettings.CHECKS_KEY)) {
                checks = new ArrayList<>();
                if (checkOldMetaSettings) {
                    //Convert old meta to new format.
                    node.fields().forEachRemaining(entry -> {
                        if (entry.getValue() instanceof ObjectNode entryVal) {
                            String key = entry.getKey().toLowerCase(Locale.ROOT);
                            NamespacedKey namespacedKey = key.contains(":") ? BukkitNamespacedKey.of(key) : BukkitNamespacedKey.wolfyutilties(key);
                            if (namespacedKey != null) {
                                entryVal.put("key", String.valueOf(namespacedKey));
                                Meta meta = JacksonUtil.getObjectMapper().convertValue(entryVal, Meta.class);
                                if (meta != null && !meta.getOption().equals(MetaSettings.Option.IGNORE) && !meta.getOption().equals(MetaSettings.Option.EXACT)) {
                                    checks.add(meta);
                                }
                            }
                        }
                    });
                }
            } else {
                checks = JacksonUtil.getObjectMapper().convertValue(node.get(MetaSettings.CHECKS_KEY), new TypeReference<>() {
                });
            }
            var nbtChecks = new MetaSettings();
            checks.forEach(nbtChecks::addCheck);
            setMetaSettings(nbtChecks);
        }
    }

    /**
     * Clones the CustomItem and all the containing data.
     *
     * @return An exact deep copy of this CustomItem instance.
     */
    @Override
    public CustomItem clone() {
        return new CustomItem(this);
    }

    /**
     * <p>
     * This will create a <b>new</b> {@link CustomItem} that wraps the specified reference.
     * </p>
     * <p>
     * </p>
     *
     * @param reference The reference to wrap
     * @return A new CustomItem instance that wraps the specified reference
     */
    public static Optional<CustomItem> wrap(StackReference reference) {
        if (reference == null) return Optional.empty();
        return Optional.of(new CustomItem(WolfyCoreCommon.getInstance().getWolfyUtils(), null, reference));
    }

    /**
     * Get the CustomItem via ItemStack.
     * It checks for the PersistentData containing the NamespacedKey of WolfyUtilities.
     * When that isn't found it checks for ItemsAdder and Oraxen values saved in the Items NBT.
     *
     * @param itemStack the ItemStack to check
     * @return the CustomItem linked to the specific API this Item is from.
     */
    public static CustomItem getReferenceByItemStack(ItemStack itemStack) {
        if (itemStack != null) {
            WolfyCoreCommon core = (WolfyCoreCommon) WolfyCoreCommon.getInstance();
            StackReference reference = new StackReference(
                    core,
                    core.getRegistries().getStackIdentifierParsers().parseIdentifier(itemStack),
                    1d,
                    itemStack.getAmount(),
                    itemStack
            );
            return new CustomItem(core.getWolfyUtils(), null, reference);
        }
        return null;
    }

    /**
     * This method returns the original CustomItem from the ItemStack.
     * This only works if the itemStack contains a NamespacedKey corresponding to a CustomItem
     * that is saved!
     * <p>
     * If you need access to the original CustomItem variables use this method.
     * <p>
     * If you want to detect what plugin this ItemStack is from and use it's corresponding Reference use {@link #getReferenceByItemStack(ItemStack)} instead!
     *
     * @param itemStack
     * @return CustomItem the ItemStack is linked to, only if it is saved, else returns null
     */
    @Nullable
    public static CustomItem getByItemStack(ItemStack itemStack) {
        return ((WolfyCoreCommon) WolfyCoreSpigot.getInstance()).getRegistries().getCustomItems().getByItemStack(itemStack).orElse(null);
    }

    /**
     * @param itemMeta The ItemMeta to get the key from.
     * @return The CustomItems {@link BukkitNamespacedKey} from the ItemMeta; or null if the ItemMeta doesn't contain a key.
     */
    public static NamespacedKey getKeyOfItemMeta(ItemMeta itemMeta) {
        var container = itemMeta.getPersistentDataContainer();
        if (container.has(PERSISTENT_KEY_TAG, PersistentDataType.STRING)) {
            return BukkitNamespacedKey.of(container.get(PERSISTENT_KEY_TAG, PersistentDataType.STRING));
        }
        return null;
    }

    public boolean hasNamespacedKey() {
        return namespacedKey != null;
    }

    @Nullable
    @Override
    public NamespacedKey key() {
        return namespacedKey;
    }

    public void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public int getDurabilityCost() {
        return durabilityCost;
    }

    public void setDurabilityCost(int durabilityCost) {
        this.durabilityCost = durabilityCost;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public MetaSettings getMetaSettings() {
        return nbtChecks;
    }

    public void setMetaSettings(MetaSettings nbtChecks) {
        if (nbtChecks.isEmpty()) {
            //Add the CustomItemTag check, so the item will be checked correctly, but only if the item hasn't got any other checks already.
            nbtChecks.addCheck(new CustomItemTagMeta());
        }
        this.nbtChecks = nbtChecks;
    }

    /**
     * @return The EquipmentSlots this item can be equipped to.
     */
    public List<EquipmentSlot> getEquipmentSlots() {
        return equipmentSlots;
    }

    /**
     * @return True if the item has a custom {@link EquipmentSlot} it can be equipped to.
     */
    public boolean hasEquipmentSlot() {
        return !getEquipmentSlots().isEmpty();
    }

    public boolean hasEquipmentSlot(EquipmentSlot slot) {
        return hasEquipmentSlot() && getEquipmentSlots().contains(slot);
    }

    public void addEquipmentSlots(EquipmentSlot... slots) {
        for (EquipmentSlot slot : slots) {
            if (!equipmentSlots.contains(slot)) {
                equipmentSlots.add(slot);
            }
        }
    }

    public void removeEquipmentSlots(EquipmentSlot... slots) {
        equipmentSlots.removeAll(Arrays.asList(slots));
    }

    /**
     * Returns if this item is blocked to be equipped.
     * If true the item cannot be equipped even if it is a chestplate or other equipment.
     *
     * @return true if the item is blocked from equipping, else false
     */
    public boolean isBlockVanillaEquip() {
        return blockVanillaEquip;
    }

    public void setBlockVanillaEquip(boolean blockVanillaEquip) {
        this.blockVanillaEquip = blockVanillaEquip;
    }

    /**
     * Returns if this item is blocked in vanilla recipes.
     * This requires CustomCrafting to work.
     *
     * @return true if this item is blocked in vanilla recipes, else false
     */
    public boolean isBlockVanillaRecipes() {
        return blockVanillaRecipes;
    }

    public void setBlockVanillaRecipes(boolean blockVanillaRecipes) {
        this.blockVanillaRecipes = blockVanillaRecipes;
    }

    /**
     * BlockPlacement indicates if the item can be placed by a player or not.
     * If true the placement is blocked and the item cannot be placed.
     * If false the item can be placed.
     *
     * @return true if the placement is blocked, false otherwise
     */
    public boolean isBlockPlacement() {
        return blockPlacement;
    }

    public void setBlockPlacement(boolean blockPlacement) {
        this.blockPlacement = blockPlacement;
    }

    public FuelSettings getFuelSettings() {
        return fuelSettings;
    }

    public void setFuelSettings(FuelSettings fuelSettings) {
        this.fuelSettings = fuelSettings;
    }

    /**
     * Checks if the ItemStack is a similar to this CustomItem.
     * This method checks all the available ItemMeta on similarity and uses the meta options
     * when they are available.
     * Use {@link #isSimilar(ItemStack, boolean)} to only check for Material and Amount!
     *
     * @param otherItem the ItemStack that should be checked
     * @return true if the ItemStack is exactly the same as this CustomItem's ItemStack
     */
    public boolean isSimilar(ItemStack otherItem) {
        return isSimilar(otherItem, true);
    }

    /**
     * Checks if the ItemStack is similar to this CustomItem.
     * <p>If exactMeta is false it only checks for Material and amount.
     * <p>If exactMeta is true it checks all the available ItemMeta and uses the meta options
     * when they are available.
     *
     * @param otherItem the ItemStack that should be checked
     * @param exactMeta if the ItemMeta should be checked. If false only checks Material and Amount!
     * @return true if the ItemStack is equal to this CustomItems ItemStack
     */
    public boolean isSimilar(ItemStack otherItem, boolean exactMeta) {
        return isSimilar(otherItem, exactMeta, false);
    }

    /**
     * Checks if the ItemStack is similar to this CustomItem.
     *
     * <p>
     * <strong>Exact Meta:</strong>
     *     <ul>
     *         <li>false - Only checks for Material and amount (if ignoreAmount isn't enabled!).</li>
     *         <li>true - Checks all the available ItemMeta and uses the meta options when they are available.</li>
     *     </ul>
     * </p>
     *
     * <br>
     *
     * @param otherItem    the ItemStack that should be checked
     * @param exactMeta    if the ItemMeta should be checked. If false only checks Material and Amount!
     * @param ignoreAmount If true ignores the amount check.
     * @return true if the ItemStack is equal to this CustomItems ItemStack
     */
    public boolean isSimilar(ItemStack otherItem, boolean exactMeta, boolean ignoreAmount) {
        if (otherItem != null && otherItem.getType().equals(this.type) && (ignoreAmount || otherItem.getAmount() >= getAmount())) {
            if (hasNamespacedKey()) {
                return getMetaSettings().check(this, new ItemBuilder(wolfyUtils, otherItem));
            } else if (stackReference().identifier() instanceof BukkitStackIdentifier && (!hasItemMeta() && !exactMeta)) {
                return true;
            }
            return stackReference().matches(otherItem, exactMeta, ignoreAmount);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomItem that)) return false;
        return Double.compare(that.rarityPercentage, rarityPercentage) == 0 &&
                durabilityCost == that.durabilityCost &&
                consumed == that.consumed &&
                blockPlacement == that.blockPlacement &&
                blockVanillaEquip == that.blockVanillaEquip &&
                blockVanillaRecipes == that.blockVanillaRecipes &&
                Objects.equals(namespacedKey, that.namespacedKey) &&
                Objects.equals(replacement, that.replacement) &&
                Objects.equals(fuelSettings, that.fuelSettings) &&
                Objects.equals(permission, that.permission) &&
                Objects.equals(equipmentSlots, that.equipmentSlots) &&
                Objects.equals(reference, that.reference) &&
                Objects.equals(particleContent, that.particleContent) &&
                Objects.equals(nbtChecks, that.nbtChecks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key(), replacement, getPermission(), getWeight(), getFuelSettings(), getBlockSettings(), getDurabilityCost(), isConsumed(), blockPlacement, isBlockVanillaEquip(), isBlockVanillaRecipes(), getEquipmentSlots(), stackReference(), getParticleContent(), getMetaSettings());
    }

    /**
     * Other than {@link #create()} it returns the real item and no copy!
     * Any changes made to this item may change the source Item!
     *
     * @return the linked item of the API reference
     */
    @Override
    public ItemStack getItemStack() {
        return reference.identifier().stack(ItemCreateContext.empty(getAmount()));
    }

    /**
     * Gets a copy of the current version of the external linked item.<br>
     * The item can be linked to Vanilla, WolfyUtilities, ItemsAdder, Oraxen, MythicMobs or MMOItems
     *
     * @return the item from the external API that is linked to this object
     */
    public ItemStack create() {
        return create(getAmount());
    }

    /**
     * Gets a copy of the current version of the external linked item.<br>
     * The item can be linked to Vanilla, WolfyUtilities, ItemsAdder, Oraxen, MythicMobs or MMOItems.
     * <p>
     * If this CustomItem has an NamespacedKey it will include it in the NBT of the returned item!
     * </p>
     *
     * @param amount Modifies the amount of the returned ItemStack.
     * @return the item from the external API that is linked to this object
     */
    public ItemStack create(int amount) {
        var itemStack = reference.identifier().stack(ItemCreateContext.empty(amount)).clone();
        if (this.hasNamespacedKey()) {
            var itemMeta = itemStack.getItemMeta();
            var container = itemMeta.getPersistentDataContainer();
            synchronized (container.getClass()) { // The container has a thread-unsafe map usage, so we need to synchronise it
                container.set(new org.bukkit.NamespacedKey(((WolfyCoreCommon) WolfyCoreCommon.getInstance()).plugin, "custom_item"), PersistentDataType.STRING, namespacedKey.toString());
            }
            itemStack.setItemMeta(itemMeta);
        }
        if (amount > 0) {
            itemStack.setAmount(amount);
        }
        return itemStack;
    }

    @JsonGetter("reference")
    public StackReference stackReference() {
        return reference;
    }

    /**
     * Gets the replacement of this CustomItem.
     *
     * @return Optional containing the current replacement, or empty if unset
     */
    public Optional<StackReference> replacement() {
        return Optional.ofNullable(replacement);
    }

    /**
     * Sets the new replacement of this CustomItem.
     * The reference may be null, in which case it unsets any existing replacement.
     *
     * @param replacement The new replacement, or null to unset it
     */
    public void replacement(StackReference replacement) {
        if(replacement != null && replacement.identifier() != null && !ItemUtils.isAirOrNull(replacement.identifier().stack(ItemCreateContext.empty(getAmount())))) {
            this.replacement = replacement;
        } else {
            this.replacement = null;
        }
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <p>
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or stack count > 1)<b>:</b><br>
     * The stack is shrunk by the specified amount (<strong><code>{@link #getAmount()} * totalAmount</code></strong>)
     * <p>
     * If this CustomItem has a custom replacement:<br>
     * This calls the stackReplacement function with the shrunken stack and this CustomItem.
     * It is meant for applying the stackable replacement items.<br>
     * For default behaviour see {@link #shrink(ItemStack, int, boolean, Inventory, Player, Location)} and {@link #shrinkUnstackableItem(ItemStack, boolean)}
     * </p>
     * </p>
     * <p>
     * <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and stack count == 1)<b>:</b><br>
     * Redirects to {@link #shrinkUnstackableItem(ItemStack, boolean)}<br>
     * </p>
     * </p>
     * <br>
     *
     * @param stack            The input ItemStack, that is also going to be edited.
     * @param count            The amount of this custom item that should be removed from the input.
     * @param useRemains       If the Item should be replaced by the default craft remains.
     * @param stackReplacement Behaviour of how to apply the replacements of stackable items.
     * @return  The manipulated stack, default remain, or custom remains.
     */
    public ItemStack shrink(@NotNull ItemStack stack, int count, boolean useRemains, @NotNull BiFunction<StackIdentifier, ItemStack, ItemStack> stackReplacement) {
        return reference.shrink(stack, count, useRemains, stackReplacement);
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or stack count > 1)<b>:</b><br>
     * The stack is shrunk by the specified amount (<strong><code>{@link #getAmount()} * totalAmount</code></strong>)
     * <p>
     * If this CustomItem has a custom replacement:<br>
     * <ul>
     *   <li><b>Location: </b>Used as the drop location for remaining items. <br>May be overridden by options below.</li>
     *   <li>
     *     <b>Player: </b>Adds items to the players inventory.
     *     <br>Remaining items are still in the pool for the next options below.
     *     <br>Player location is used as the drop location for remaining items.</li>
     *   <li>
     *     <b>Inventory:</b> Adds items to the inventory.
     *     <br>Remaining items are still in the pool for the next options below.
     *     <br>If location not available yet: uses inventory location as drop location for remaining items.
     *   </li>
     * </ul>
     * All remaining items that cannot be added to player or the other inventory are dropped at the specified location.<br>
     * <b>Warning! If you do not provide a location via <code>player</code>, <code>inventory</code>, or <code>inventory</code>, then the remaining items are discarded!</b><br>
     * For custom behaviour see {@link #shrink(ItemStack, int, boolean, BiFunction)}.
     *
     * </p>
     * </p>
     * <p>
     * <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and stack count == 1)<b>:</b><br>
     * Redirects to {@link #shrinkUnstackableItem(ItemStack, boolean)}<br>
     * </p>
     * </p>
     * <br>
     *
     * @param stack      The input ItemStack, that is also going to be edited.
     * @param count      The amount of this custom item that should be removed from the input.
     * @param useRemains If the Item should be replaced by the default craft remains.
     * @param inventory  The optional inventory to add the replacements to. (Only for stackable items)
     * @param player     The player to give the items to. If the players' inventory has space the craft remains are added. (Only for stackable items)
     * @param location   The location where the replacements should be dropped. (Only for stackable items)
     * @return  The manipulated stack, default remain, or custom remains.
     */
    public ItemStack shrink(ItemStack stack, int count, boolean useRemains, @Nullable final Inventory inventory, @Nullable final Player player, @Nullable final Location location) {
        return shrink(stack, count, useRemains, (customItem, resultStack) -> {
            ItemStack replacement = replacement()
                    .map(StackReference::originalStack)
                    .orElseGet(() -> isConsumed() && useRemains && craftRemain != null ? new ItemStack(craftRemain) : null);
            if (!ItemUtils.isAirOrNull(replacement)) {
                int replacementAmount = replacement.getAmount() * count;
                if (ItemUtils.isAirOrNull(resultStack)) {
                    int returnableAmount = Math.min(replacement.getMaxStackSize(), replacementAmount);
                    replacementAmount -= returnableAmount;
                    resultStack = replacement.clone();
                    resultStack.setAmount(replacementAmount);
                }
                if (replacementAmount > 0) {
                    replacement.setAmount(replacementAmount);
                    Location loc = location;
                    if (player != null) {
                        replacement = player.getInventory().addItem(replacement).get(0);
                        loc = player.getLocation();
                    }
                    if (inventory != null && replacement != null) {
                        replacement = inventory.addItem(replacement).get(0);
                        if (loc == null) loc = inventory.getLocation();
                    }
                    if (loc != null && replacement != null && loc.getWorld() != null) {
                        loc.getWorld().dropItemNaturally(loc.add(0.5, 1.0, 0.5), replacement);
                    }
                }
            }
            return resultStack;
        });
    }

    /**
     * Shrinks the specified stack and returns the manipulated or replaced item!
     * <p>
     *     This firstly checks for custom replacements (remains) and sets it as the result.<br>
     *     Then handles damaging of the stack, if there is a specified durability cost.<br>
     *     In case the stack breaks due damage it is replaced by the result, specified earlier.
     * </p>
     *
     * @param stack         The stack to shrink
     * @param useRemains    If the Item should be replaced by the default craft remains.
     * @return The manipulated (damaged) stack, default remain, or custom remains.
     */
    public ItemStack shrinkUnstackableItem(ItemStack stack, boolean useRemains) {
        ItemStack result = replacement()
                .map(StackReference::originalStack)
                .orElseGet(() -> {
                    if (this.isConsumed() && craftRemain != null && useRemains) {
                        return new ItemStack(craftRemain);
                    }
                    return new ItemStack(Material.AIR);
                });
        if (this.getDurabilityCost() != 0) {
            // handle custom durability
            var itemBuilder = new ItemBuilder(wolfyUtils, stack);
            if (itemBuilder.hasCustomDurability()) {
                int damage = itemBuilder.getCustomDamage() + this.getDurabilityCost();
                if (damage > itemBuilder.getCustomDurability()) {
                    return result;
                }
                itemBuilder.setCustomDamage(damage);
                return itemBuilder.create();
            }
            // handle vanilla durability
            if (stack.getItemMeta() instanceof Damageable itemMeta) {
                int damage = itemMeta.getDamage() + this.getDurabilityCost();
                if (damage > type.getMaxDurability()) {
                    return result;
                }
                itemMeta.setDamage(damage);
                stack.setItemMeta(itemMeta);
                return stack;
            }
        }
        return result;
    }

    public static Optional<Material> craftRemain(ItemStack stack) {
        if (!ItemUtils.isAirOrNull(stack) && stack.getType().isItem()) {
            Material replaceType = stack.getType().getCraftingRemainingItem();
            if (replaceType != null) return Optional.of(replaceType);
            return switch (stack.getType().name()) {
                case "LAVA_BUCKET", "MILK_BUCKET", "WATER_BUCKET", "COD_BUCKET", "SALMON_BUCKET", "PUFFERFISH_BUCKET", "TROPICAL_FISH_BUCKET" ->
                        Optional.of(Material.BUCKET);
                case "POTION" -> Optional.of(Material.GLASS_BOTTLE);
                case "BEETROOT_SOUP", "MUSHROOM_STEW", "RABBIT_STEW" -> Optional.of(Material.BOWL);
                default -> Optional.empty();
            };
        }
        return Optional.empty();
    }

    private Material getCraftRemain() {
        return CustomItem.craftRemain(getItemStack()).orElse(null);
    }

    /**
     * @return True if this item requires permission to be used, else false.
     */
    public boolean hasPermission() {
        return !permission.isEmpty();
    }

    /**
     * Gets the permission string of this CustomItem.
     *
     * @return The permission string of this item
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Sets the permission String.
     *
     * @param permission The new permission string
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * @return The weight of the item.
     */
    public double getWeight() {
        return rarityPercentage;
    }

    /**
     * @param weight The weight of the item.
     */
    public void setWeight(double weight) {
        this.rarityPercentage = rarityPercentage;
    }

    @JsonSetter("data")
    private void setDataList(List<CustomItemData> data) {
        for (CustomItemData itemData : data) {
            addOrReplaceData(itemData);
        }
    }

    @JsonGetter("data")
    private List<CustomItemData> getDataList() {
        return indexedData.values().stream().toList();
    }

    public <T extends CustomItemData> T addOrReplaceData(T data) {
        Class<T> type = (Class<T>) data.getClass();
        return type.cast(indexedData.put(getKeyForData(type), data));
    }

    public CustomItemData addDataIfAbsent(CustomItemData data) {
        return indexedData.putIfAbsent(data.key(), data);
    }

    public CustomItemData computeDataIfAbsent(NamespacedKey id, Function<NamespacedKey, CustomItemData> mappingFunction) {
        return indexedData.computeIfAbsent(id, mappingFunction);
    }

    public <T extends CustomItemData> T computeDataIfAbsent(Class<T> type, Function<NamespacedKey, T> mappingFunction) {
        return type.cast(indexedData.computeIfAbsent(getKeyForData(type), mappingFunction));
    }

    public CustomItemData computeDataIfPresent(NamespacedKey id, BiFunction<NamespacedKey, CustomItemData, CustomItemData> remappingFunction) {
        return indexedData.computeIfPresent(id, remappingFunction);
    }

    public <T extends CustomItemData> T computeDataIfPresent(Class<T> type, BiFunction<NamespacedKey, T, T> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        NamespacedKey key = getKeyForData(type);
        T oldValue = type.cast(indexedData.get(key));
        if (oldValue != null) {
            T newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                indexedData.put(key, newValue);
                return newValue;
            } else {
                indexedData.remove(key);
                return null;
            }
        }
        return null;
    }

    public Optional<CustomItemData> getData(NamespacedKey id) {
        return Optional.ofNullable(indexedData.get(id));
    }

    public <T extends CustomItemData> Optional<T> getData(Class<T> type) {
        return getData(getKeyForData(type)).map(type::cast);
    }

    private static NamespacedKey getKeyForData(Class<? extends CustomItemData> type) {
        return ((WolfyCoreCommon) WolfyCoreSpigot.getInstance()).getRegistries().getCustomItemDataTypeRegistry().getKey(type);
    }

    @JsonGetter
    @NotNull
    public ParticleContent getParticleContent() {
        return particleContent;
    }

    @JsonAlias("particles")
    @JsonSetter
    public void setParticleContent(ObjectNode particlesNode) {
        if (particlesNode == null) {
            this.particleContent = new ParticleContent();
            return;
        }
        this.particleContent = Objects.requireNonNullElse(JacksonUtil.getObjectMapper().convertValue(particlesNode, ParticleContent.class), new ParticleContent());

        if (Streams.stream(particlesNode.fieldNames()).anyMatch(s -> ParticleLocation.valueOf(s.toUpperCase(Locale.ROOT)).isDeprecated())) { //Old version. Conversion required.
            var playerSettings = Objects.requireNonNullElse(this.particleContent.getPlayer(), new ParticleContent.PlayerSettings(wolfyUtils)); //Create or get player settings
            particlesNode.fields().forEachRemaining(entry -> {
                ParticleLocation loc = ParticleLocation.valueOf(entry.getKey());
                JsonNode value = entry.getValue();
                if (value.isObject() && value.has("effect")) {
                    var animation = ((BukkitRegistries) wolfyUtils.getRegistries()).getParticleAnimations().get(JacksonUtil.getObjectMapper().convertValue(value.get("effect"), BukkitNamespacedKey.class));
                    if (animation != null) {
                        loc.applyOldPlayerAnimation(playerSettings, animation);
                    }
                }
            });
        }
    }

    @JsonGetter
    public ActionSettings getActionSettings() {
        return actionSettings;
    }

    public CustomBlockSettings getBlockSettings() {
        return blockSettings;
    }

    /**
     * Gets the amount of the linked ItemStack or if the custom amount
     * is bigger than 0 gets the custom amount.
     *
     * @return actual amount of CustomItem
     */
    public int getAmount() {
        return reference.amount();
    }

    /**
     * Sets the amount of the linked item.
     *
     * @param amount The new amount of the item.
     */
    public void setAmount(int amount) {

    }

    public boolean isBlock() {
        return type.isBlock() || (reference.identifier() instanceof ItemsAdderStackIdentifier iaRef && ((WolfyCoreCommon) WolfyCoreSpigot.getInstance()).getCompatibilityManager().getPlugins()
                .evaluateIfAvailable("ItemsAdder", ItemsAdderIntegration.class, ia -> ia.getStackInstance(iaRef.itemId()).map(CustomStack::isBlock).orElse(false)));
    }

    @Override
    public String toString() {
        return "CustomItem{" +
                "namespacedKey=" + namespacedKey +
                ", craftRemain=" + craftRemain +
                ", consumed=" + consumed +
                ", replacement=" + replacement +
                ", durabilityCost=" + durabilityCost +
                ", permission='" + permission + '\'' +
                ", rarityPercentage=" + rarityPercentage +
                ", fuelSettings=" + fuelSettings +
                ", blockPlacement=" + blockPlacement +
                ", blockVanillaEquip=" + blockVanillaEquip +
                ", blockVanillaRecipes=" + blockVanillaRecipes +
                ", equipmentSlots=" + equipmentSlots +
                ", reference=" + reference +
                ", particleContent=" + particleContent +
                ", metaSettings=" + nbtChecks +
                "} " + super.toString();
    }
}