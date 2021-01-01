package me.wolfyscript.utilities.api.inventory.custom_items;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.lone.itemsadder.api.ItemsAdder;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.util.jnbt.CompoundTag;
import io.th0rgal.oraxen.items.OraxenItems;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.MetaSettings;
import me.wolfyscript.utilities.api.inventory.custom_items.references.*;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.AbstractItemBuilder;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

@JsonSerialize(using = CustomItem.Serializer.class)
@JsonDeserialize(using = CustomItem.Deserializer.class)
public class CustomItem extends AbstractItemBuilder<CustomItem> implements Cloneable {

    /**
     * This HashMap contains all the available CustomData providers, which then can be saved and loaded.
     * If the config contains CustomData that is not available in this HashMap, then it won't be loaded!
     */
    private static final HashMap<me.wolfyscript.utilities.util.NamespacedKey, CustomData.Provider<?>> availableCustomData = new HashMap<>();
    /**
     * Other than the availableCustomData, this Map is only available for the specific CustomItem instance!
     * All registered CustomData is added to this item and cannot be removed!
     * Only the single CustomData objects can be edit in it's values.
     */
    private final HashMap<me.wolfyscript.utilities.util.NamespacedKey, CustomData> customDataMap = new HashMap<>();

    public CustomItem(APIReference apiReference) {
        super(CustomItem.class);
        this.apiReference = apiReference;

        this.namespacedKey = null;
        this.burnTime = 0;
        this.allowedBlocks = new ArrayList<>();
        this.replacement = null;
        this.durabilityCost = 0;
        this.consumed = true;
        this.metaSettings = new MetaSettings();
        this.permission = "";
        this.rarityPercentage = 1.0d;
        for (CustomData.Provider<?> customData : CustomItem.getAvailableCustomData().values()) {
            addCustomData(customData.getNamespacedKey(), customData.createData());
        }
        this.equipmentSlots = new ArrayList<>();
        this.particleContent = new ParticleContent();
        this.blockPlacement = false;
        this.blockVanillaEquip = false;
        this.blockVanillaRecipes = false;
        this.advanced = true;
    }

    public static HashMap<me.wolfyscript.utilities.util.NamespacedKey, CustomData.Provider<?>> getAvailableCustomData() {
        return availableCustomData;
    }

    /**
     * This namespacedKey can either be null or non-null.
     * <p>
     * If it's non-null the item is saved and the variables of this Object will be persistent </p>
     * when converted to ItemStack via {@link #create()}.
     * <p>
     * If it is null the item isn't saved and the variables of this Object will get lost when {@link #create()} is called!
     */
    private me.wolfyscript.utilities.util.NamespacedKey namespacedKey;

    private APIReference replacement;
    private List<Material> allowedBlocks;
    private String permission;
    private double rarityPercentage;
    private int burnTime;
    private int durabilityCost;
    private boolean consumed;
    private boolean blockPlacement;
    private boolean blockVanillaEquip;
    private boolean blockVanillaRecipes;
    private final List<EquipmentSlot> equipmentSlots;

    private boolean advanced;

    /**
     * Upcoming change to CustomItem will include an APIReference to link it
     * to other external APIs.
     *
     * @see APIReference
     */
    private final APIReference apiReference;

    private ParticleContent particleContent;
    private MetaSettings metaSettings;

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
     * Register a new {@link CustomData.Provider} object that can be used in any Custom Item from the point of registration.
     * <br/>
     * You can register any CustomData you might want to add to your CustomItems and then save and load it from config too.
     * <br/>
     * It allows you to save and load custom data into a CustomItem and makes things a lot easier if you have some items that perform specific actions with the data etc.
     * <br/>
     * For example CustomCrafting registers it's own CustomData, that isn't in this base API, for it's Elite Workbenches that open up custom GUIs dependent on their CustomData.
     * And also the Recipe Book uses a CustomData object to store some data.
     *
     * @param customData The CustomData object to register.
     */
    public static void registerCustomData(CustomData.Provider<?> customData) {
        availableCustomData.put(customData.getNamespacedKey(), customData);
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
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                APIReference apiReference = null;
                NamespacedKey namespacedKey = new NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");
                if (container.has(namespacedKey, PersistentDataType.STRING)) {
                    apiReference = new WolfyUtilitiesRef(me.wolfyscript.utilities.util.NamespacedKey.getByString(container.get(namespacedKey, PersistentDataType.STRING)));
                }
                if (apiReference == null) {
                    if (WolfyUtilities.hasPlugin("ItemsAdder")) {
                        if (ItemsAdder.isCustomItem(itemStack)) {
                            apiReference = new ItemsAdderRef(ItemsAdder.getCustomItemName(itemStack));
                        }
                    } else if (WolfyUtilities.hasPlugin("Oraxen")) {
                        String itemId = OraxenItems.getIdByItem(itemStack);
                        if (itemId != null && !itemId.isEmpty()) {
                            apiReference = new OraxenRef(itemId);
                        }
                    } else if (WolfyUtilities.hasPlugin("MythicMobs")) {
                        if (MythicMobs.inst().getVolatileCodeHandler().getItemHandler() != null) {
                            CompoundTag compoundTag = MythicMobs.inst().getVolatileCodeHandler().getItemHandler().getNBTData(itemStack);
                            String name = compoundTag.getString("MYTHIC_TYPE");
                            if (MythicMobs.inst().getItemManager().getItem(name).isPresent()) {
                                apiReference = new MythicMobsRef(name);
                            }
                        }
                    }
                }
                if (apiReference != null) {
                    apiReference.setAmount(itemStack.getAmount());
                    return new CustomItem(apiReference);
                }
                return new CustomItem(itemStack);
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
     * @return CustomItem the ItemStack is linked, only if it is saved, else returns null
     */
    @org.jetbrains.annotations.Nullable
    public static CustomItem getByItemStack(ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                NamespacedKey namespacedKey = new NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");
                if (container.has(namespacedKey, PersistentDataType.STRING)) {
                    return Registry.CUSTOM_ITEMS.get(me.wolfyscript.utilities.util.NamespacedKey.getByString(container.get(namespacedKey, PersistentDataType.STRING)));
                }
            }
        }
        return null;
    }

    public boolean hasNamespacedKey() {
        return namespacedKey != null;
    }

    public me.wolfyscript.utilities.util.NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public void setNamespacedKey(me.wolfyscript.utilities.util.NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public boolean hasReplacement() {
        return replacement != null && !replacement.getLinkedItem().getType().equals(Material.AIR);
    }

    @Nullable
    public APIReference getReplacement() {
        return hasReplacement() ? replacement : null;
    }

    public void setReplacement(@Nullable APIReference replacement) {
        this.replacement = replacement;
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
        return metaSettings;
    }

    public void setMetaSettings(MetaSettings metaSettings) {
        this.metaSettings = metaSettings;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public List<Material> getAllowedBlocks() {
        return allowedBlocks;
    }

    public void setAllowedBlocks(List<Material> allowedBlocks) {
        this.allowedBlocks = allowedBlocks;
    }

    public List<EquipmentSlot> getEquipmentSlots() {
        return equipmentSlots;
    }

    public boolean hasEquipmentSlot() {
        return !getEquipmentSlots().isEmpty();
    }

    public boolean hasEquipmentSlot(EquipmentSlot slot) {
        return hasEquipmentSlot() && getEquipmentSlots().contains(slot);
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
     * Checks if the ItemStack is a similar to this CustomItem.
     * This method checks all the available ItemMeta on similarity and uses the meta options
     * when they are available. <p><b>
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
        ItemStack currentItem = create();
        if (otherItem == null) return false;
        if (otherItem == currentItem) return true;
        if (otherItem.getType().equals(currentItem.getType()) && otherItem.getAmount() >= currentItem.getAmount()) {
            if (!isAdvanced() && hasNamespacedKey()) {
                CustomItem otherCustomItem = CustomItem.getByItemStack(otherItem);
                if (ItemUtils.isAirOrNull(otherCustomItem) || !otherCustomItem.hasNamespacedKey()) return false;
                return getNamespacedKey().equals(otherCustomItem.getNamespacedKey());
            }
            if (!exactMeta && !currentItem.hasItemMeta()) return true;
            ItemBuilder customItem = new ItemBuilder(currentItem);
            ItemBuilder customItemOther = new ItemBuilder(otherItem.clone());
            boolean meta = getMetaSettings().checkMeta(customItemOther, customItem);
                /*
                ItemMeta itemMeta = customItem.getItemMeta();
                ItemMeta itemMetaOther = customItemOther.getItemMeta();
                itemMeta.setVersion(2580);
                itemMetaOther.setVersion(2580);

                This can be used to bypass different versions of saved and current input item.
                However I am not sure what could happen by forcing the version to change.
                Best way to handle this issue is to re-save all the items and the load them again. That will update them including the version number.
                CustomCrafting handles it with "/recipes save" where all items are re-saved!
                */
            return meta && Bukkit.getItemFactory().equals(customItem.getItemMeta(), customItemOther.getItemMeta());
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomItem that = (CustomItem) o;
        return Double.compare(that.rarityPercentage, rarityPercentage) == 0 &&
                burnTime == that.burnTime &&
                durabilityCost == that.durabilityCost &&
                consumed == that.consumed &&
                blockPlacement == that.blockPlacement &&
                blockVanillaEquip == that.blockVanillaEquip &&
                blockVanillaRecipes == that.blockVanillaRecipes &&
                advanced == that.advanced &&
                Objects.equals(customDataMap, that.customDataMap) &&
                Objects.equals(namespacedKey, that.namespacedKey) &&
                Objects.equals(replacement, that.replacement) &&
                Objects.equals(allowedBlocks, that.allowedBlocks) &&
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
     * If linked to config it will create a new instance with the values from the config,
     * else copys this instance of the ItemStack.
     *
     * @return exact copy of this instance
     */
    @Override
    public CustomItem clone() {
        CustomItem customItem = new CustomItem(getApiReference());
        if (hasNamespacedKey()) {
            customItem.setNamespacedKey(getNamespacedKey());
            customItem.setBlockVanillaRecipes(isBlockVanillaRecipes());
            customItem.setBlockVanillaEquip(isBlockVanillaEquip());
            customItem.setAllowedBlocks(getAllowedBlocks());
            customItem.setBurnTime(getBurnTime());
            customItem.setConsumed(isConsumed());
            customItem.setDurabilityCost(getDurabilityCost());
            customItem.setMetaSettings(getMetaSettings());
            customItem.setParticleContent(getParticleContent());
            customItem.setPermission(getPermission());
            customItem.setRarityPercentage(getRarityPercentage());
            customItem.setReplacement(getReplacement());
            customItem.setAdvanced(isAdvanced());
        }
        customItem.setAmount(getAmount());
        return customItem;
        //CustomItem customItem = new CustomItem(getApiReference());
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
     * Gets a copy of the current version of the external linked item.
     * The item can be linked to Vanilla, WolfyUtilities, ItemsAdder, Oraxen, MythicMobs or MMOItems
     *
     * @return the item from the external API that is linked to this object
     */
    public ItemStack create() {
        return create(getAmount());
    }

    /**
     * Gets a copy of the current version of the external linked item.
     * The item can be linked to Vanilla, WolfyUtilities, ItemsAdder, Oraxen, MythicMobs or MMOItems
     *
     * @return the item from the external API that is linked to this object
     */
    public ItemStack create(int amount) {
        ItemStack itemStack = apiReference.getLinkedItem().clone();
        if (this.hasNamespacedKey()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
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
     * This item should only be used to visulize the namespacekey!
     * It doesn't include a NBT Tag with the namspacekey and non of the WU features!
     *
     * @param amount The stacksize of the item
     * @return ItemStack that visually represents the namespacekey
     */
    public ItemStack getIDItem(int amount) {
        ItemStack itemStack = apiReference.getIdItem();
        if (amount > 0) {
            itemStack.setAmount(amount);
        }
        return itemStack;
    }

    public APIReference getApiReference() {
        return apiReference;
    }

    /**
     * Consumes the totalAmount of the input ItemStack!
     * <p>
     * The totalAmount is multiplied with the value from {@link #getAmount()} and then this amount is removed from the input.
     * <br/>
     * This method will directly edit the input ItemStack and won't return a result value.
     * </p>
     * <p>
     * If this item is an un-stackable item then this method will use the {@link #consumeUnstackableItem(ItemStack)} method.
     * </p>
     * <p>
     * Else if the item is stackable, then there are couple of settings for the replacement.
     * <br/>
     * If the custom item has a replacement:
     *     <ul>
     *         <li><b>If location is null and inventory is not null,</b> then it will try to add the item to the inventory. When inventory is full it will try to get the location from the inventory and if valid drops the items at that location instead.</li>
     *         <li><b>If location is not null,</b> then it will drop the items at that location.</li>
     *         <li><b>If location and inventory are null,</b> then the replacement items are neither dropped nor added to the inventory!</li>
     *     </ul>
     * </p>
     * <br/>
     *
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item should be removed from the input.
     * @param inventory   The optional inventory to add the replacements to. (Only for stackable items)
     * @param location    The location where the replacements should be dropped. (Only for stackable items)
     */
    public void consumeItem(ItemStack input, int totalAmount, Inventory inventory, Location location) {
        if (this.create().getMaxStackSize() > 1) {
            int amount = input.getAmount() - getAmount() * totalAmount;
            if (this.isConsumed()) {
                input.setAmount(amount);
            }
            if (this.hasReplacement()) {
                ItemStack replacement = new CustomItem(this.getReplacement()).create();
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
        } else {
            consumeUnstackableItem(input);
        }
    }

    public void consumeItem(ItemStack input, int totalAmount, Inventory inventory) {
        consumeItem(input, totalAmount, inventory, null);
    }

    public ItemStack consumeItem(ItemStack input, int totalAmount, Location location) {
        consumeItem(input, totalAmount, null, location);
        return input;
    }

    /**
     * This method consumes the input as it is a un-stackable item.
     * Default items will be replaced e.g. buckets, potions, stew/soup.
     * If it has a replacement then the input will be replaced with the replacement.
     *
     * @param input The input ItemStack, that is going to be edited.
     */
    public void consumeUnstackableItem(ItemStack input) {
        if (this.hasNamespacedKey()) {
            if (this.isConsumed()) {
                input.setAmount(0);
            } else {
                Material replaceType = getCraftReplaceMaterial(input);
                if (replaceType != null) {
                    input.setType(replaceType);
                }
            }
            if (this.hasReplacement()) {
                ItemStack replace = new CustomItem(this.getReplacement()).create();
                input.setType(replace.getType());
                input.setItemMeta(replace.getItemMeta());
                input.setData(replace.getData());
                input.setAmount(replace.getAmount());
            } else if (this.getDurabilityCost() != 0) {
                CustomItem customInput = new CustomItem(input);
                if (customInput.hasCustomDurability()) {
                    customInput.setCustomDamage(customInput.getCustomDamage() + this.getDurabilityCost());
                    return;
                }
                ItemMeta itemMeta = input.getItemMeta();
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
        } else {
            Material replaceType = getCraftReplaceMaterial(input);
            if (replaceType != null) {
                input.setType(replaceType);
            }
            input.setAmount(0);
        }
    }

    private Material getCraftReplaceMaterial(ItemStack input) {
        if (WolfyUtilities.hasVillagePillageUpdate()) {
            if (input.getType().isItem()) {
                Material replaceType = input.getType().getCraftingRemainingItem();
                if (replaceType != null) return replaceType;
            }
        } else switch (input.getType()) {
            case LAVA_BUCKET:
            case MILK_BUCKET:
            case WATER_BUCKET:
                return Material.BUCKET;
        }
        switch (input.getType()) {
            case COD_BUCKET:
            case SALMON_BUCKET:
            case PUFFERFISH_BUCKET:
            case TROPICAL_FISH_BUCKET:
                return Material.BUCKET;
            case POTION:
                return Material.GLASS_BOTTLE;
            case BEETROOT_SOUP:
            case MUSHROOM_STEW:
            case RABBIT_STEW:
                return Material.BOWL;
        }
        return null;
    }

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

    public CustomData getCustomData(me.wolfyscript.utilities.util.NamespacedKey namespacedKey) {
        return customDataMap.get(namespacedKey);
    }

    public HashMap<me.wolfyscript.utilities.util.NamespacedKey, CustomData> getCustomDataMap() {
        return customDataMap;
    }

    public void addCustomData(me.wolfyscript.utilities.util.NamespacedKey namespacedKey, CustomData customData) {
        this.customDataMap.put(namespacedKey, customData);
    }

    public ParticleContent getParticleContent() {
        return particleContent;
    }

    public void setParticleContent(ParticleContent particleContent) {
        if (particleContent == null) {
            this.particleContent = new ParticleContent();
        } else {
            this.particleContent = particleContent;
        }
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

    static class Serializer extends StdSerializer<CustomItem> {

        public Serializer() {
            this(CustomItem.class);
        }

        protected Serializer(Class<CustomItem> t) {
            super(t);
        }

        @Override
        public void serialize(CustomItem customItem, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeObjectField("api_reference", customItem.getApiReference());
            gen.writeObjectField("advanced", customItem.isAdvanced());
            gen.writeBooleanField("consumed", customItem.isConsumed());
            gen.writeBooleanField("blockVanillaEquip", customItem.isBlockVanillaEquip());
            gen.writeBooleanField("blockPlacement", customItem.isBlockPlacement());
            gen.writeBooleanField("blockVanillaRecipes", customItem.isBlockVanillaRecipes());
            gen.writeNumberField("rarity_percentage", customItem.getRarityPercentage());
            gen.writeStringField("permission", customItem.getPermission());
            gen.writeObjectField("meta", customItem.getMetaSettings());
            gen.writeObjectFieldStart("fuel");
            {
                gen.writeNumberField("burntime", customItem.getBurnTime());
                gen.writeArrayFieldStart("allowed_blocks");
                for (Material material : customItem.getAllowedBlocks()) {
                    gen.writeString(material.toString());
                }
                gen.writeEndArray();
            }
            gen.writeEndObject();
            gen.writeObjectFieldStart("custom_data");
            {
                for (Map.Entry<me.wolfyscript.utilities.util.NamespacedKey, CustomData> value : customItem.getCustomDataMap().entrySet()) {
                    gen.writeObjectFieldStart(value.getKey().toString());
                    value.getValue().writeToJson(customItem, gen, provider);
                    gen.writeEndObject();
                }
            }
            gen.writeEndObject();
            gen.writeObjectField("replacement", customItem.getReplacement());
            gen.writeNumberField("durability_cost", customItem.getDurabilityCost());
            gen.writeArrayFieldStart("equipment_slots");
            {
                for (EquipmentSlot equipmentSlot : customItem.getEquipmentSlots()) {
                    gen.writeString(equipmentSlot.toString());
                }
            }
            gen.writeEndArray();
            gen.writeObjectField("particles", customItem.getParticleContent());
            gen.writeEndObject();
        }
    }

    static class Deserializer extends StdDeserializer<CustomItem> {

        public Deserializer() {
            this(CustomItem.class);
        }

        protected Deserializer(Class<CustomItem> t) {
            super(t);
        }

        @Override
        public CustomItem deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = JacksonUtil.getObjectMapper();
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                CustomItem customItem = new CustomItem(mapper.convertValue(node.path(node.has("api_reference") ? "api_reference" : "item"), APIReference.class));
                customItem.setAdvanced(node.path("advanced").asBoolean(true));
                customItem.setConsumed(node.path("consumed").asBoolean());
                customItem.setBlockVanillaEquip(node.path("blockVanillaEquip").asBoolean());
                customItem.setBlockPlacement(node.path("blockPlacement").asBoolean());
                customItem.setBlockVanillaRecipes(node.path("blockVanillaRecipes").asBoolean());
                customItem.setRarityPercentage(node.path("rarity_percentage").asDouble(1.0));
                customItem.setPermission(node.path("permission").asText());
                customItem.setMetaSettings(mapper.convertValue(node.path("meta"), MetaSettings.class));
                JsonNode fuelNode = node.path("fuel");
                {
                    customItem.setBurnTime(fuelNode.path("burntime").asInt());
                    List<Material> allowedBlocks = new ArrayList<>();
                    fuelNode.path("allowed_blocks").elements().forEachRemaining(n -> allowedBlocks.add(Material.matchMaterial(n.asText())));
                    customItem.setAllowedBlocks(allowedBlocks);
                }
                JsonNode customDataNode = node.path("custom_data");
                {
                    customDataNode.fields().forEachRemaining(entry -> {
                        me.wolfyscript.utilities.util.NamespacedKey namespacedKey = entry.getKey().contains(":") ? me.wolfyscript.utilities.util.NamespacedKey.getByString(entry.getKey()) : /* This is only for backwards compatibility! Might be removed in the future */ availableCustomData.keySet().parallelStream().filter(namespacedKey1 -> namespacedKey1.getKey().equals(entry.getKey())).findFirst().orElse(null);
                        if (namespacedKey != null && availableCustomData.containsKey(namespacedKey)) {
                            availableCustomData.get(namespacedKey).addData(customItem, entry.getValue(), ctxt);
                        }
                    });
                }
                customItem.setReplacement(mapper.convertValue(node.path("replacement"), APIReference.class));
                customItem.setDurabilityCost(node.path("durability_cost").asInt());
                node.path("equipment_slots").elements().forEachRemaining(n -> customItem.addEquipmentSlots(EquipmentSlot.valueOf(n.asText())));
                customItem.setParticleContent(mapper.convertValue(node.path("particles"), ParticleContent.class));
                return customItem;
            }
            return new CustomItem(Material.AIR);
        }
    }
}
