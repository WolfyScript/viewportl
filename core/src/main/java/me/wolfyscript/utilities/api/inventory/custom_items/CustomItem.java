package me.wolfyscript.utilities.api.inventory.custom_items;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.MetaSettings;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.VanillaRef;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.AbstractItemBuilder;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * <p>
 * This Object acts as a wrapper for the {@link APIReference} with additional options that make it possible to manipulate the behaviour of the wrapped reference.
 * <br>
 * The {@link APIReference} can be any kind of reference, a simple {@link ItemStack} ({@link VanillaRef}) or an item from another API.
 * </p>
 * <p>
 * For most additional features the CustomItem has to be registered into the {@link Registry#CUSTOM_ITEMS}.
 * <br>
 * To make sure the CustomItem can be detected later on, it must be created via any of the {@link #create()} methods.
 * <br>
 * These methods will include an extra {@link PersistentDataContainer} entry to identify the item later on!
 * </p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class CustomItem extends AbstractItemBuilder<CustomItem> implements Keyed {

    private static final Map<String, APIReference.Parser<?>> API_REFERENCE_PARSER = new HashMap<>();

    @Nullable
    public static APIReference.Parser<?> getApiReferenceParser(String id) {
        return API_REFERENCE_PARSER.get(id);
    }

    @JsonIgnore
    private final Material type;

    /**
     * Register a new {@link APIReference.Parser} that can parse ItemStacks and keys from another plugin to a usable {@link APIReference}
     *
     * @param parser an {@link APIReference.Parser} instance.
     */
    public static void registerAPIReferenceParser(APIReference.Parser<?> parser) {
        if (!(parser instanceof APIReference.PluginParser) || WolfyUtilities.hasPlugin(((APIReference.PluginParser<?>) parser).getPluginName())) {
            API_REFERENCE_PARSER.put(parser.getId(), parser);
            if (!parser.getAliases().isEmpty()) {
                parser.getAliases().forEach(s -> API_REFERENCE_PARSER.putIfAbsent(s, parser));
            }
        }
    }

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

    @JsonAlias("api_reference")
    private final APIReference apiReference;
    @JsonAlias("custom_data")
    @JsonDeserialize(using = CustomData.Deserializer.class)
    @JsonSerialize(using = CustomData.Serializer.class)
    private final Map<NamespacedKey, CustomData> customDataMap = new HashMap<>();
    @JsonAlias("equipment_slots")
    private final List<EquipmentSlot> equipmentSlots;
    private boolean consumed;
    private boolean advanced;
    private boolean blockVanillaEquip;
    private boolean blockPlacement;
    private boolean blockVanillaRecipes;
    @JsonAlias("rarity_percentage")
    private double rarityPercentage;
    private String permission;
    @JsonAlias("meta")
    private MetaSettings metaSettings;
    private APIReference replacement;
    @JsonAlias("fuel")
    private FuelSettings fuelSettings;
    @JsonAlias("durability_cost")
    private int durabilityCost;
    @JsonAlias("particles")
    private ParticleContent particleContent;

    @JsonCreator
    public CustomItem(@JsonProperty("apiReference") @JsonAlias({"item", "api_reference"}) APIReference apiReference) {
        super(CustomItem.class);
        this.apiReference = apiReference;

        this.namespacedKey = null;
        this.fuelSettings = new FuelSettings();
        this.metaSettings = new MetaSettings();
        this.permission = "";
        this.rarityPercentage = 1.0d;
        for (CustomData.Provider<?> customData : Registry.CUSTOM_ITEM_DATA.values()) {
            addCustomData(customData.getNamespacedKey(), customData.createData());
        }
        this.equipmentSlots = new ArrayList<>();
        this.particleContent = new ParticleContent();
        this.blockPlacement = false;
        this.blockVanillaEquip = false;
        this.blockVanillaRecipes = false;
        this.advanced = false;

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
    public CustomItem(ItemStack itemStack) {
        this(new VanillaRef(itemStack));
    }

    /**
     * Creates a CustomItem with a Vanilla Reference to an itemstack of the material
     *
     * @param material the material of the itemstack this CustomItem will be linked to
     */
    public CustomItem(Material material) {
        this(new ItemStack(material));
    }

    /**
     * @param customItem A new deep copy of the passed in CustomItem.
     */
    private CustomItem(CustomItem customItem) {
        super(CustomItem.class);
        this.apiReference = customItem.apiReference.clone();

        this.namespacedKey = customItem.getNamespacedKey();
        this.fuelSettings = customItem.fuelSettings.clone();
        this.metaSettings = customItem.metaSettings;
        this.permission = customItem.permission;
        this.rarityPercentage = customItem.rarityPercentage;
        this.customDataMap.clear();
        for (Map.Entry<NamespacedKey, CustomData> entry : customItem.customDataMap.entrySet()) {
            this.customDataMap.put(entry.getKey(), entry.getValue().clone());
        }
        this.equipmentSlots = new ArrayList<>(customItem.equipmentSlots);
        this.particleContent = customItem.particleContent;
        this.blockPlacement = customItem.blockPlacement;
        this.blockVanillaEquip = customItem.blockVanillaEquip;
        this.blockVanillaRecipes = customItem.blockVanillaRecipes;
        this.advanced = customItem.advanced;

        this.consumed = customItem.consumed;
        this.replacement = customItem.replacement;
        this.durabilityCost = customItem.durabilityCost;
        this.type = getItemStack() != null ? getItemStack().getType() : Material.AIR;
        this.craftRemain = getCraftRemain();
    }

    /**
     * Clones the CustomItem and all the containing data.
     *
     * @return A exact deep copy of this CustomItem instance.
     */
    @Override
    public CustomItem clone() {
        return new CustomItem(this);
    }

    /**
     * <p>
     * This will create a <b>new</b> {@link CustomItem} instance with the specified APIReference.
     * </p>
     * <p>
     * This means:
     * If the reference points to an actual CustomItem, the returned CustomItem will override it's custom values and can then be registered.
     * They are then dependent on the linked CustomItem.
     * If the reference points to any other API such as Oraxen, MMOItems, etc. it is linked to these APIs and can be registered.
     * They are then dependent on the linked API.
     * </p>
     *
     * @param reference The reference to link the item to.
     * @return A new CustomItem instance with the specified APIReference.
     */
    public static CustomItem with(APIReference reference) {
        if (reference == null) return null;
        return new CustomItem(reference);
    }

    /**
     * <p>
     * This method tries to get the actual {@link CustomItem} of the {@link APIReference}!
     * </p>
     * <p>
     * If the reference points to an actual registered CustomItem ({@link WolfyUtilitiesRef}) then that item is returned.<br>
     * If the reference points to any other API such as Oraxen, MMOItems, etc. it redirects to the {@link #with(APIReference)} method.<br>
     * </p>
     * <p>
     * <b>
     * !Warning: If you want to create a CustomItem that is linked to another CustomItem and overrides it use {@link #with(APIReference)} instead!
     * </b>
     * </p>
     *
     * @param reference The reference that points to an API Item.
     * @return The actual CustomItem of the APIReference.
     */
    @Nullable
    public static CustomItem of(APIReference reference) {
        if (reference == null) return null;
        return reference instanceof WolfyUtilitiesRef ? Registry.CUSTOM_ITEMS.get(((WolfyUtilitiesRef) reference).getNamespacedKey()) : with(reference);
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
            APIReference apiReference = API_REFERENCE_PARSER.values().stream().sorted(APIReference.Parser::compareTo).map(parser -> parser.construct(itemStack)).filter(Objects::nonNull).findFirst().orElse(null);
            if (apiReference != null) {
                apiReference.setAmount(itemStack.getAmount());
                return new CustomItem(apiReference);
            }
            return new CustomItem(itemStack);
        }
        return null;
    }

    /**
     * This method return the original CustomItem from the ItemStack.
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
        if (itemStack != null) {
            var itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                var container = itemMeta.getPersistentDataContainer();
                var namespacedKey = new org.bukkit.NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");
                if (container.has(namespacedKey, PersistentDataType.STRING)) {
                    return Registry.CUSTOM_ITEMS.get(NamespacedKey.of(container.get(namespacedKey, PersistentDataType.STRING)));
                }
            }
        }
        return null;
    }

    public boolean hasNamespacedKey() {
        return namespacedKey != null;
    }

    @Nullable
    @Override
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    /**
     * The replacement can be any of {@link APIReference} and it will replace this item when it is removed from the inventory using {@link #remove(ItemStack, int, Inventory, Location, boolean)}.
     *
     * @return True if this item has an replacement that is not AIR, else false.
     */
    public boolean hasReplacement() {
        return replacement != null && !replacement.getLinkedItem().getType().equals(Material.AIR);
    }

    /**
     * The replacement can be any of {@link APIReference} and it will replace this item when it is removed from the inventory using {@link #remove(ItemStack, int, Inventory, Location, boolean)}.
     *
     * @return The {@link APIReference} of the custom replacement.
     */
    @Nullable
    public APIReference getReplacement() {
        return hasReplacement() ? replacement : null;
    }

    /**
     * The replacement can be any of {@link APIReference} and it will replace this item when it is removed from the inventory using {@link #remove(ItemStack, int, Inventory, Location, boolean)}.
     *
     * @param replacement The replacement for this item.
     */
    public void setReplacement(@Nullable APIReference replacement) {
        this.replacement = replacement;
    }

    /**
     * @return The durability that is removed from the item when removed from an inventory using {@link #remove(ItemStack, int, Inventory, Location, boolean)}
     */
    public int getDurabilityCost() {
        return durabilityCost;
    }

    public void setDurabilityCost(int durabilityCost) {
        this.durabilityCost = durabilityCost;
    }

    /**
     * @return True if the item is removed by calling {@link #remove(ItemStack, int, Inventory, Location, boolean)}.
     */
    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public MetaSettings getMetaSettings() {
        return metaSettings;
    }

    public void setMetaSettings(MetaSettings metaSettings) {
        this.metaSettings = metaSettings;
        this.advanced = metaSettings.values().parallelStream().anyMatch(meta -> !meta.getOption().equals(MetaSettings.Option.EXACT) && !meta.getOption().equals(MetaSettings.Option.IGNORE));
    }

    /**
     * @return The burn time of the item inside the furnace.
     * @deprecated Use {@link #getFuelSettings()} and {@link FuelSettings#getBurnTime()}
     */
    @Deprecated
    @JsonIgnore
    public int getBurnTime() {
        return fuelSettings.getBurnTime();
    }

    /**
     * @param burnTime The burn time in ticks.
     * @deprecated Use {@link #getFuelSettings()} and {@link FuelSettings#setBurnTime(int)}
     */
    @Deprecated
    @JsonIgnore
    public void setBurnTime(int burnTime) {
        fuelSettings.setBurnTime(burnTime);
    }

    /**
     * @return The blocks in which the item can be used as fuel
     * @deprecated Use {@link #getFuelSettings()} and {@link FuelSettings#getAllowedBlocks()}
     */
    @Deprecated
    @JsonIgnore
    public List<Material> getAllowedBlocks() {
        return fuelSettings.getAllowedBlocks();
    }

    /**
     * @param allowedBlocks The blocks in which the item can be used as fuel
     * @deprecated Use {@link #getFuelSettings()} and {@link FuelSettings#setAllowedBlocks(List)}
     */
    @Deprecated
    @JsonIgnore
    public void setAllowedBlocks(List<Material> allowedBlocks) {
        fuelSettings.setAllowedBlocks(allowedBlocks);
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
     * Checks if the ItemStack is a similar to this CustomItem.
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
     * Checks if the ItemStack is a similar to this CustomItem.
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
                var other = CustomItem.getByItemStack(otherItem);
                if (ItemUtils.isAirOrNull(other) || !other.hasNamespacedKey() || !getNamespacedKey().equals(other.getNamespacedKey())) {
                    return false;
                }
            } else if (!getApiReference().isValidItem(otherItem)) {
                return false;
            }
            if ((exactMeta || hasItemMeta()) && (isAdvanced() || (getApiReference() instanceof VanillaRef && !hasNamespacedKey()))) {
                var customItem = new ItemBuilder(getItemStack().clone());
                var customItemOther = new ItemBuilder(otherItem.clone());
                return getMetaSettings().check(customItemOther, customItem) && Bukkit.getItemFactory().equals(customItem.getItemMeta(), customItemOther.getItemMeta());
            }
            return true;
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
                advanced == that.advanced &&
                Objects.equals(customDataMap, that.customDataMap) &&
                Objects.equals(namespacedKey, that.namespacedKey) &&
                Objects.equals(replacement, that.replacement) &&
                Objects.equals(fuelSettings, that.fuelSettings) &&
                Objects.equals(permission, that.permission) &&
                Objects.equals(equipmentSlots, that.equipmentSlots) &&
                Objects.equals(apiReference, that.apiReference) &&
                Objects.equals(particleContent, that.particleContent) &&
                Objects.equals(metaSettings, that.metaSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCustomDataMap(), getNamespacedKey(), getReplacement(), getAllowedBlocks(), getPermission(), getRarityPercentage(), getBurnTime(), getDurabilityCost(), isConsumed(), blockPlacement, isAdvanced(), isBlockVanillaEquip(), isBlockVanillaRecipes(), getEquipmentSlots(), getApiReference(), getParticleContent(), getMetaSettings());
    }

    /**
     * Other than {@link #create()} it returns the real item and no copy!
     * Any changes made to this item may change the source Item!
     *
     * @return the linked item of the API reference
     */
    @Override
    public ItemStack getItemStack() {
        return apiReference.getLinkedItem();
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
        var itemStack = apiReference.getLinkedItem().clone();
        if (this.hasNamespacedKey()) {
            var itemMeta = itemStack.getItemMeta();
            itemMeta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item"), PersistentDataType.STRING, namespacedKey.toString());
            itemStack.setItemMeta(itemMeta);
        }
        if (amount > 0) {
            itemStack.setAmount(amount);
        }
        return itemStack;
    }

    /**
     * Returns the id item with the default stacksize of this instance!
     *
     * @return ItemStack that visually represents the namespacekey
     * @see #getIDItem(int)
     */
    public ItemStack getIDItem() {
        return getIDItem(getAmount());
    }

    /**
     * This item should only be used to visualize the namespacedkey!
     * It doesn't include a NBT Tag with the namspacekey and non of the WU features!
     *
     * @param amount The stacksize of the item
     * @return ItemStack that visually represents the namespacekey
     */
    public ItemStack getIDItem(int amount) {
        var itemStack = apiReference.getIdItem();
        if (amount > 0) {
            itemStack.setAmount(amount);
        }
        return itemStack;
    }

    public APIReference getApiReference() {
        return apiReference;
    }

    /**
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param inventory   The optional inventory to add the replacements to. (Only for stackable items)
     * @param location    The location where the replacements should be dropped. (Only for stackable items)
     * @deprecated Renamed to {@link #remove(ItemStack, int, Inventory, Location)} to better show it's functionality.
     */
    @Deprecated
    public void consumeItem(ItemStack input, int totalAmount, Inventory inventory, Location location) {
        remove(input, totalAmount, inventory, location);
    }

    /**
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param inventory   The optional inventory to add the replacements to. (Only for stackable items)
     * @deprecated Renamed to {@link #remove(ItemStack, int, Inventory)} to better show it's functionality.
     */
    @Deprecated
    public void consumeItem(ItemStack input, int totalAmount, Inventory inventory) {
        remove(input, totalAmount, inventory);
    }

    /**
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param location    The location where the replacements should be dropped. (Only for stackable items)
     * @deprecated Renamed to {@link #remove(ItemStack, int, Location)} to better show it's functionality.
     */
    @Deprecated
    public ItemStack consumeItem(ItemStack input, int totalAmount, Location location) {
        return remove(input, totalAmount, location);
    }

    /**
     * Removes the specified amount from the input ItemStack inside a inventory!
     * <p>
     * This method will directly edit the input ItemStack and won't return a result value.
     *
     * <p>
     * <strong>Stackable:</strong><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement:
     * <ul>
     *     <li><b>If location is null and inventory is not null,</b> then it will try to add the item to the inventory. When inventory is full it will try to get the location from the inventory and if valid drops the items at that location instead.</li>
     *     <li><b>If location is not null,</b> then it will drop the items at that location.</li>
     *     <li><b>If location and inventory are null,</b> then the replacement items are neither dropped nor added to the inventory!</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <strong>Un-stackable:</strong><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack, boolean)} method.<br>
     *     </p>
     * </p>
     * <br>
     *
     * @param input              The input ItemStack, that is also going to be edited.
     * @param totalAmount        The amount of this custom item that should be removed from the input.
     * @param inventory          The optional inventory to add the replacements to. (Only for stackable items)
     * @param location           The location where the replacements should be dropped. (Only for stackable items)
     * @param replaceWithRemains If the Item should be replaced by the default craft remains (Only for un-stackable items).
     */
    public void remove(ItemStack input, int totalAmount, Inventory inventory, Location location, boolean replaceWithRemains) {
        if (this.type.getMaxStackSize() > 1) {
            int amount = input.getAmount() - getAmount() * totalAmount;
            if (this.isConsumed()) {
                input.setAmount(amount);
            }
            applyStackableReplacement(totalAmount, inventory, location);
        } else {
            removeUnStackableItem(input, replaceWithRemains);
        }
    }

    /**
     * Removes the specified amount from the input ItemStack inside a inventory!
     * <p>
     * This method will directly edit the input ItemStack and won't return a result value.
     *
     * <p>
     * <strong>Stackable:</strong><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement:
     * <ul>
     *     <li><b>If location is null and inventory is not null,</b> then it will try to add the item to the inventory. When inventory is full it will try to get the location from the inventory and if valid drops the items at that location instead.</li>
     *     <li><b>If location is not null,</b> then it will drop the items at that location.</li>
     *     <li><b>If location and inventory are null,</b> then the replacement items are neither dropped nor added to the inventory!</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <strong>Un-stackable:</strong><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack)} method and replaces the item with it's craft remains if available.
     *     </p>
     * </p>
     * <br>
     *
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param inventory   The optional inventory to add the replacements to. (Only for stackable items)
     * @param location    The location where the replacements should be dropped. (Only for stackable items)
     * @see #remove(ItemStack, int, Inventory, Location, boolean)
     */
    public void remove(ItemStack input, int totalAmount, Inventory inventory, Location location) {
        remove(input, totalAmount, inventory, location, true);
    }

    /**
     * Removes the specified amount from the input ItemStack inside a inventory!
     * <p>
     * This method will directly edit the input ItemStack and won't return a result value.
     *
     * <p>
     * <strong>Stackable:</strong><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement:
     * <ul>
     *     <li><b>If the inventory is not null,</b> then it will try to add the item to the inventory. When inventory is full it will try to get the location from the inventory and if valid drops the items at that location instead.</li>
     *     <li><b>If the inventory is null,</b> then the replacement items are neither dropped nor added to the inventory!</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <strong>Un-stackable:</strong><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack)} method and replaces the item with it's craft remains if available.
     *     </p>
     * </p>
     * <br>
     *
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param inventory   The optional inventory to add the replacements to. (Only for stackable items)
     * @see #remove(ItemStack, int, Inventory, Location, boolean)
     */
    public void remove(ItemStack input, int totalAmount, Inventory inventory) {
        remove(input, totalAmount, inventory, null);
    }

    /**
     * Removes the specified amount from the input ItemStack inside a inventory!
     * <p>
     * This method will directly edit the input ItemStack and won't return a result value.
     *
     * <p>
     * <strong>Stackable:</strong><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement:
     * <ul>
     *     <li><b>If location is not null,</b> then it will drop the items at that location.</li>
     *     <li><b>If location is null,</b> then the replacement items are neither dropped nor added to the inventory!</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <strong>Un-stackable:</strong><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack)} method and replaces the item with it's craft remains if available.
     *     </p>
     * </p>
     * <br>
     *
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param location    The location where the replacements should be dropped. (Only for stackable items)
     * @return The original input {@link ItemStack} that was directly edited by the method.
     * @see #remove(ItemStack, int, Inventory, Location, boolean)
     */
    public ItemStack remove(ItemStack input, int totalAmount, Location location) {
        remove(input, totalAmount, null, location);
        return input;
    }

    private void applyStackableReplacement(int totalAmount, Inventory inventory, Location location) {
        if (this.hasReplacement()) {
            var replacement = new CustomItem(getReplacement()).create();
            replacement.setAmount(replacement.getAmount() * totalAmount);
            if (location == null) {
                if (inventory == null) return;
                if (InventoryUtils.hasInventorySpace(inventory, replacement)) {
                    inventory.addItem(replacement);
                    return;
                }
                location = inventory.getLocation();
            }
            if (location != null && location.getWorld() != null) {
                location.getWorld().dropItemNaturally(location.add(0.5, 1.0, 0.5), replacement);
            }
        }
    }

    /**
     * Removes the input as an un-stackable item.
     * <p>
     * Items that have craft remains by default will be replaced with the according {@link Material} <br>
     * Like Buckets, Potions, Stew/Soup.
     * </p>
     * <p>
     * If this CustomItem has a custom replacement then the input will be replaced with that.
     * </p>
     * <br>
     *
     * @param input The input ItemStack, that is going to be edited.
     */
    public void removeUnStackableItem(ItemStack input) {
        removeUnStackableItem(input, true);
    }

    /**
     * Removes the input as an un-stackable item.
     * <p>
     * Items that have craft remains by default will be replaced with the according {@link Material} <br>
     * Like Buckets, Potions, Stew/Soup.
     * </p>
     * <p>
     * If this CustomItem has a custom replacement then the input will be replaced with that.
     * </p>
     * <br>
     *
     * @param input              The input ItemStack, that is going to be edited.
     * @param replaceWithRemains If the item should be replaced by it's remains if removed. Not including custom replacement options!
     */
    public void removeUnStackableItem(ItemStack input, boolean replaceWithRemains) {
        if (this.isConsumed()) {
            if (craftRemain != null && replaceWithRemains) {
                input.setType(craftRemain);
                input.setItemMeta(Bukkit.getItemFactory().getItemMeta(craftRemain));
            } else {
                input.setAmount(0);
            }
        }
        if (this.hasReplacement()) {
            ItemStack replace = new CustomItem(this.getReplacement()).create();
            input.setType(replace.getType());
            input.setItemMeta(replace.getItemMeta());
            input.setData(replace.getData());
            input.setAmount(replace.getAmount());
        } else if (this.getDurabilityCost() != 0) {
            var itemBuilder = new ItemBuilder(input);
            if (itemBuilder.hasCustomDurability()) {
                itemBuilder.setCustomDamage(itemBuilder.getCustomDamage() + this.getDurabilityCost());
                return;
            }
            var itemMeta = input.getItemMeta();
            if (itemMeta instanceof Damageable) {
                int damage = ((Damageable) itemMeta).getDamage() + this.getDurabilityCost();
                if (damage > create().getType().getMaxDurability()) {
                    input.setAmount(0);
                } else {
                    ((Damageable) itemMeta).setDamage(damage);
                }
            }
            input.setItemMeta(itemMeta);
        }
    }

    /**
     * @param input The input ItemStack, that is going to be edited.
     * @deprecated Replaced by {@link #removeUnStackableItem(ItemStack)}
     */
    @Deprecated
    public void consumeUnstackableItem(ItemStack input) {
        removeUnStackableItem(input);
    }

    private Material getCraftRemain() {
        var item = getItemStack();
        if (!ItemUtils.isAirOrNull(item) && item.getType().isItem()) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_15)) {
                Material replaceType = item.getType().getCraftingRemainingItem();
                if (replaceType != null) return replaceType;
            }
            return switch (item.getType()) {
                case LAVA_BUCKET, MILK_BUCKET, WATER_BUCKET, COD_BUCKET, SALMON_BUCKET, PUFFERFISH_BUCKET, TROPICAL_FISH_BUCKET -> Material.BUCKET;
                case POTION -> Material.GLASS_BOTTLE;
                case BEETROOT_SOUP, MUSHROOM_STEW, RABBIT_STEW -> Material.BOWL;
                default -> null;
            };
        }
        return null;
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

    public double getRarityPercentage() {
        return rarityPercentage;
    }

    public void setRarityPercentage(double rarityPercentage) {
        this.rarityPercentage = rarityPercentage;
    }

    public CustomData getCustomData(NamespacedKey namespacedKey) {
        return customDataMap.get(namespacedKey);
    }

    public Map<NamespacedKey, CustomData> getCustomDataMap() {
        return customDataMap;
    }

    public void addCustomData(NamespacedKey namespacedKey, CustomData customData) {
        this.customDataMap.put(namespacedKey, customData);
    }

    public ParticleContent getParticleContent() {
        return particleContent;
    }

    public void setParticleContent(ParticleContent particleContent) {
        this.particleContent = Objects.requireNonNullElseGet(particleContent, ParticleContent::new);
    }

    /**
     * This value is used for simple check in recipes.
     * Default is true.
     * If this value is false CustomCrafting will ignore every single meta and NBT data and will only compare the NamespacedKeys.
     *
     * @return If this CustomItem is in advanced mode.
     * @deprecated This feature is still under development and might change.
     */
    @Deprecated
    public boolean isAdvanced() {
        return advanced;
    }

    /**
     * Set the advanced value. Default is true.
     * If this value is false CustomCrafting will ignore every single meta and NBT data and will only compare the NamespacedKeys.
     *
     * @param advanced The new advanced value.
     * @deprecated This feature is still under development and might change.
     */
    @Deprecated
    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }

    /**
     * Gets the amount of the linked ItemStack or if the custom amount
     * is bigger than 0 gets the custom amount.
     *
     * @return actual amount of CustomItem
     */
    public int getAmount() {
        return getApiReference().getAmount();
    }

    /**
     * Sets the amount of the linked item.
     *
     * @param amount The new amount of the item.
     */
    public void setAmount(int amount) {
        getApiReference().setAmount(amount);
    }

    @Override
    public String toString() {
        return "CustomItem{" +
                "customDataMap=" + customDataMap +
                ", namespacedKey=" + namespacedKey +
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
                ", advanced=" + advanced +
                ", apiReference=" + apiReference +
                ", particleContent=" + particleContent +
                ", metaSettings=" + metaSettings +
                "} " + super.toString();
    }
}