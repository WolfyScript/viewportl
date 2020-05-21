package me.wolfyscript.utilities.api.custom_items;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.custom_data.CustomData;
import me.wolfyscript.utilities.api.utils.InventoryUtils;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.main.Main;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CustomItem extends ItemStack implements Cloneable {

    //This HashMap contains all the available CustomData objects, which then can be saved and loaded.
    //If the config contains CustomData that is not available in this HashMap, then it won't be loaded!
    private static final HashMap<String, CustomData> availableCustomData = new HashMap<>();

    /*
    Other than the availableCustomData, this Map is only available for the specific CustomItem instance!
    All registered CustomData is added to this item and cannot be removed!
    Only the single CustomData objects can be edit in it's values.
     */
    private HashMap<String, CustomData> customDataMap = new HashMap<>();
    private final ItemConfig config;
    private final NamespacedKey namespacedKey;
    private CustomItem replacement;
    private final String id;
    private final ArrayList<Material> allowedBlocks;
    private String permission;
    private double rarityPercentage;
    private int burnTime;
    private int durabilityCost;
    private boolean consumed;
    private boolean canBePlaced;
    private boolean blockVanillaEquip;
    private final List<EquipmentSlot> equipmentSlots;
    /**
     * Upcoming change to CustomItem will include an APIReference to link it
     * to other external APIs.
     *
     * @see APIReference
     */
    private APIReference apiReference;
    private ParticleContent particleContent;
    private MetaSettings metaSettings;

    public CustomItem(ItemConfig config, boolean replace) {
        super(config.getCustomItem(replace));
        this.config = config;
        this.namespacedKey = config.getNamespacedKey();
        this.id = config.getId();
        this.burnTime = config.getBurnTime();
        this.allowedBlocks = config.getAllowedBlocks();
        this.replacement = config.getReplacementItem();
        this.durabilityCost = config.getDurabilityCost();
        this.consumed = config.isConsumed();
        this.metaSettings = config.getMetaSettings();
        this.permission = config.getPermission();
        this.rarityPercentage = config.getRarityPercentage();
        this.customDataMap = config.getCustomData();
        this.equipmentSlots = config.getEquipmentSlots();
        this.particleContent = config.getParticleData();
        this.blockVanillaEquip = false;
    }

    public CustomItem(ItemConfig config) {
        this(config, false);
    }

    public CustomItem(ItemStack itemStack) {
        super(itemStack);
        this.config = null;
        this.id = "";
        this.namespacedKey = null;
        this.burnTime = 0;
        this.allowedBlocks = new ArrayList<>();
        this.replacement = null;
        this.durabilityCost = 0;
        this.consumed = true;
        this.metaSettings = new MetaSettings();
        this.permission = "";
        this.rarityPercentage = 1.0d;
        for (CustomData customData : CustomItem.getAvailableCustomData().values()) {
            this.customDataMap.put(customData.getId(), customData.getDefaultCopy());
        }
        this.equipmentSlots = new ArrayList<>();
        this.particleContent = new ParticleContent();
        this.blockVanillaEquip = false;
        this.canBePlaced = true;
    }

    public CustomItem(Material material) {
        this(new ItemStack(material));
    }

    /*
    CustomItem static methods
     */
    public static CustomItem getByItemStack(ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                CustomItem customItem = null;
                if (WolfyUtilities.hasVillagePillageUpdate()) {
                    if (itemMeta.getPersistentDataContainer().has(new org.bukkit.NamespacedKey(Main.getInstance(), "custom_item"), PersistentDataType.STRING)) {
                        customItem = CustomItems.getCustomItem(itemMeta.getPersistentDataContainer().get(new org.bukkit.NamespacedKey(Main.getInstance(), "custom_item"), PersistentDataType.STRING));
                    }
                } else {
                    if (ItemUtils.isInItemSettings(itemMeta, "custom_item")) {
                        customItem = CustomItems.getCustomItem((String) ItemUtils.getFromItemSettings(itemMeta, "custom_item"));
                    }
                }
                if (customItem != null) {
                    customItem.setAmount(itemStack.getAmount());
                    return customItem;
                }
                return new CustomItem(itemStack);
            }
            return new CustomItem(itemStack);
        }
        return null;
    }

    /*
        Checks if the itemmeta has Custom Durability set.
    */
    public static boolean hasCustomDurability(ItemMeta itemMeta) {
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            return dataContainer.has(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.value"), PersistentDataType.INTEGER);
        }
        return false;
    }

    public boolean hasReplacement() {
        return replacement != null && !replacement.getType().equals(Material.AIR);
    }

    @Nullable
    public CustomItem getReplacement() {
        return hasReplacement() ? replacement.getRealItem() : null;
    }

    public void setReplacement(@Nullable CustomItem replacement) {
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

    public static void setCustomDurability(ItemMeta itemMeta, int durability) {
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.set(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.value"), PersistentDataType.INTEGER, durability);
            if (!dataContainer.has(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"), PersistentDataType.INTEGER)) {
                dataContainer.set(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"), PersistentDataType.INTEGER, 0);
            }
            setDurabilityTag(itemMeta);
        }
    }

    public static void removeCustomDurability(ItemMeta itemMeta) {
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.remove(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.value"));
            dataContainer.remove(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"));
            dataContainer.remove(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.tag"));
        }
    }


    public boolean hasConfig() {
        return config != null;
    }

    public ItemConfig getConfig() {
        return config;
    }


    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public ArrayList<Material> getAllowedBlocks() {
        return allowedBlocks;
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

    public boolean isBlockVanillaEquip() {
        return blockVanillaEquip;
    }

    public void setBlockVanillaEquip(boolean blockVanillaEquip) {
        this.blockVanillaEquip = blockVanillaEquip;
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

    @Override
    public boolean isSimilar(ItemStack stack) {
        return isSimilar(stack, true);
    }

    public boolean isSimilar(ItemStack stack, boolean exactMeta) {
        if (stack == null) {
            return false;
        } else if (stack == this) {
            return true;
        } else if (stack.getType().equals(this.getType()) && stack.getAmount() >= this.getAmount()) {
            if (exactMeta || this.hasItemMeta() || this.hasConfig()) {
                ItemMeta stackMeta = stack.getItemMeta();
                ItemMeta currentMeta = this.getItemMeta();
                if (!getMetaSettings().checkMeta(stackMeta, currentMeta)) {
                    return false;
                }
                return stackMeta.equals(currentMeta) || currentMeta.equals(stackMeta);
            }
            return true;
        }
        return false;
    }


    /**
     * If linked to config it will create a new instance with the values of the config,
     * else copys this instance ItemStack.
     *
     * @return exact copy of this instance
     */
    @Override
    public CustomItem clone() {
        CustomItem customItem;
        if (hasConfig()) {
            customItem = new CustomItem(getConfig());
        } else {
            customItem = new CustomItem(this);
        }
        customItem.setAmount(getAmount());
        return customItem;
    }

    /**
     * This will call the super.clone() method to get the ItemStack.
     * All CustomItem variables will get lost!
     *
     * @deprecated This will not provide the ItemStack with an NBT Tag which stores the CustomItem id!
     * So it would be impossible to check to which CustomItem this ItemStack belongs to.
     * {@link #getItemStack()} or {@link #getRealItem()} should be used instead!
     */
    @Deprecated
    public ItemStack getAsItemStack() {
        return super.clone();
    }

    /**
     * This just refers to the getRealItem() and only returns an ItemStack Object, but the name might be more useful.
     *
     * @see #getRealItem()
     */
    public ItemStack getItemStack() {
        return getRealItem();
    }


    /**
     * If this CustomItem instance is linked to a config it creates a new instance of the CustomItem and translates language keys
     * and adds a NBT Tag containing the namespacekey of the item,
     * else invokes the {@link #clone()} method.
     *
     * @return CustomItem including a NBT Tag with the namespacekey or {@link #clone()}
     */
    public CustomItem getRealItem() {
        if (hasConfig()) {
            CustomItem customItem = new CustomItem(config, true);
            if (customItem.getType().equals(this.getType())) {
                customItem.setAmount(this.getAmount());
            }
            ItemMeta itemMeta = customItem.getItemMeta();
            if (WolfyUtilities.hasVillagePillageUpdate()) {
                itemMeta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(Main.getInstance(), "custom_item"), PersistentDataType.STRING, customItem.getNamespacedKey().toString());
            } else {
                ItemUtils.setToItemSettings(itemMeta, "custom_item", customItem.getNamespacedKey().toString());
            }
            customItem.setItemMeta(itemMeta);
            return customItem;
        }
        return clone();
    }

    /**
     * This item should only be used to visulize the namespacekey!
     * It doesn't include a NBT Tag with the namspacekey and non of the WU features!
     *
     * @param amount The stacksize of the item
     * @return ItemStack that visually represents the namespacekey
     */
    public ItemStack getIDItem(int amount) {
        if (getType().equals(Material.AIR)) {
            return new ItemStack(Material.AIR);
        }
        ItemStack idItem = new ItemStack(this.clone());
        if (!this.id.isEmpty()) {
            ItemMeta idItemMeta = idItem.getItemMeta();
            if (idItemMeta.hasDisplayName() && !WolfyUtilities.unhideString(idItemMeta.getDisplayName()).endsWith(":id_item")) {
                idItemMeta.setDisplayName(idItemMeta.getDisplayName() + WolfyUtilities.hideString(":id_item"));
            } else {
                idItemMeta.setDisplayName(WolfyUtilities.hideString("%NO_NAME%") + "§r" + WordUtils.capitalizeFully(idItem.getType().name().replace("_", " ")) + WolfyUtilities.hideString(":id_item"));
            }
            List<String> lore = idItemMeta.hasLore() ? idItemMeta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add("§7[§3§lID_ITEM§r§7]");
            lore.add("§3" + this.id);
            idItemMeta.setLore(lore);
            idItem.setItemMeta(idItemMeta);
        }
        idItem.setAmount(amount);
        return idItem;
    }

    /**
     * Returns the id item with the default stacksize of this instance!
     *
     * @return ItemStack that visually represents the namespacekey
     * @see #getIDItem(int)
     */
    public ItemStack getIDItem() {
        return getIDItem(this.getAmount());
    }

    public static int getCustomDurability(ItemMeta itemMeta) {
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (dataContainer.has(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.value"), PersistentDataType.INTEGER)) {
                return dataContainer.get(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.value"), PersistentDataType.INTEGER);
            }
        }
        return 0;
    }

    public static void setCustomDamage(ItemMeta itemMeta, int damage) {
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.set(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"), PersistentDataType.INTEGER, damage);
            setDurabilityTag(itemMeta);
        }
    }

    private static boolean isIDItem(ItemStack itemStack) {
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            String name = WolfyUtilities.unhideString(itemStack.getItemMeta().getDisplayName());
            return name.endsWith(":id_item");
        }
        return false;
    }

    public void consumeItem(ItemStack input, int totalAmount, Inventory inventory, Location location) {
        if (this.getMaxStackSize() > 1) {
            int amount = input.getAmount() - this.getAmount() * totalAmount;
            if (this.isConsumed()) {
                input.setAmount(amount);
            }
            if (this.hasReplacement()) {
                ItemStack replacement = this.getReplacement();
                replacement.setAmount(replacement.getAmount() * totalAmount);
                if (location == null) {
                    if (InventoryUtils.hasInventorySpace(inventory, replacement)) {
                        inventory.addItem(replacement);
                    } else {
                        inventory.getLocation().getWorld().dropItemNaturally(inventory.getLocation().add(0.5, 1.0, 0.5), replacement);
                    }
                } else {
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

    public void consumeUnstackableItem(ItemStack input) {
        if (this.hasConfig()) {
            if (this.isConsumed()) {
                input.setAmount(0);
            } else {
                switch (input.getType()) {
                    case LAVA_BUCKET:
                    case MILK_BUCKET:
                    case WATER_BUCKET:
                    case COD_BUCKET:
                    case SALMON_BUCKET:
                    case PUFFERFISH_BUCKET:
                    case TROPICAL_FISH_BUCKET:
                        input.setType(Material.BUCKET);
                        break;
                    case POTION:
                        input.setType(Material.GLASS_BOTTLE);
                        break;
                    case BEETROOT_SOUP:
                    case MUSHROOM_STEW:
                    case RABBIT_STEW:
                        input.setType(Material.BOWL);
                }
                if (WolfyUtilities.hasBuzzyBeesUpdate()) {
                    if (input.getType().equals(Material.HONEY_BOTTLE)) {
                        input.setType(Material.GLASS_BOTTLE);
                    }
                }
            }
            if (this.hasReplacement()) {
                ItemStack replace = this.getReplacement();
                input.setType(replace.getType());
                input.setItemMeta(replace.getItemMeta());
                input.setData(replace.getData());
                input.setAmount(replace.getAmount());
                return;
            } else if (this.getDurabilityCost() != 0) {
                if (WolfyUtilities.hasVillagePillageUpdate()) {
                    if (CustomItem.hasCustomDurability(input)) {
                        CustomItem.setCustomDamage(input, CustomItem.getCustomDamage(input) + this.getDurabilityCost());
                        return;
                    }
                } else {
                    if (ItemUtils.hasCustomDurability(input)) {
                        ItemUtils.setDamage(input, ItemUtils.getDamage(input) + this.getDurabilityCost());
                        return;
                    }
                }
                ItemMeta itemMeta = input.getItemMeta();
                if (itemMeta instanceof Damageable) {
                    int damage = ((Damageable) itemMeta).getDamage() + this.getDurabilityCost();
                    if (damage > getType().getMaxDurability()) {
                        input.setAmount(0);
                    } else {
                        ((Damageable) itemMeta).setDamage(damage);
                    }
                }
                input.setItemMeta(itemMeta);
            }
        } else {
            switch (input.getType()) {
                case LAVA_BUCKET:
                case MILK_BUCKET:
                case WATER_BUCKET:
                case COD_BUCKET:
                case SALMON_BUCKET:
                case PUFFERFISH_BUCKET:
                case TROPICAL_FISH_BUCKET:
                    input.setType(Material.BUCKET);
                    return;
                case POTION:
                    input.setType(Material.GLASS_BOTTLE);
                    return;
                case BEETROOT_SOUP:
                case MUSHROOM_STEW:
                case RABBIT_STEW:
                    input.setType(Material.BOWL);
                    return;
            }
            if (WolfyUtilities.hasBuzzyBeesUpdate()) {
                if (input.getType().equals(Material.HONEY_BOTTLE)) {
                    input.setType(Material.GLASS_BOTTLE);
                    return;
                }
            }
            input.setAmount(0);
        }
    }

    public boolean hasPermission() {
        return !permission.isEmpty();
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public double getRarityPercentage() {
        return rarityPercentage;
    }

    public void setRarityPercentage(double rarityPercentage) {
        this.rarityPercentage = rarityPercentage;
    }

    public CustomData getCustomData(String id) {
        return customDataMap.get(id);
    }

    public HashMap<String, CustomData> getCustomDataMap() {
        return customDataMap;
    }

    public void addCustomData(String id, CustomData customData) {
        this.customDataMap.put(id, customData);
    }

    public static HashMap<String, CustomData> getAvailableCustomData() {
        return availableCustomData;
    }

    public static void registerCustomData(CustomData customData) {
        availableCustomData.put(customData.getId(), customData);
    }
    /*
    Checks if the itemstack has Custom Durability set.
     */

    public static boolean hasCustomDurability(ItemStack itemStack) {
        return hasCustomDurability(itemStack.getItemMeta());
    }

    public static int getCustomDamage(ItemMeta itemMeta) {
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (dataContainer.has(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"), PersistentDataType.INTEGER)) {
                return dataContainer.get(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"), PersistentDataType.INTEGER);
            }
        }
        return 0;
    }

    public static void setCustomDurability(ItemStack itemStack, int durability) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            setCustomDurability(itemMeta, durability);
        }
        itemStack.setItemMeta(itemMeta);
    }

    public static void setCustomDurabilityTag(ItemMeta itemMeta, String tag) {
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.set(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.tag"), PersistentDataType.STRING, tag);
            setDurabilityTag(itemMeta);
        }
    }

    public static void removeCustomDurability(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        removeCustomDurability(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    public static String getCustomDurabilityTag(ItemMeta itemMeta) {
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (dataContainer.has(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.tag"), PersistentDataType.STRING)) {
                return dataContainer.get(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.tag"), PersistentDataType.STRING);
            }
        }
        return "";
    }

    public static int getCustomDurability(ItemStack itemStack) {
        return getCustomDurability(itemStack.getItemMeta());
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public static void setCustomDamage(ItemStack itemStack, int damage) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        setCustomDamage(itemMeta, damage);
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage((int) (itemStack.getType().getMaxDurability() * ((double) damage / (double) getCustomDurability(itemStack))));
        }
        itemStack.setItemMeta(itemMeta);
    }

    @Deprecated
    public String getId() {
        return id;
    }

    public static int getCustomDamage(ItemStack itemStack) {
        return getCustomDamage(itemStack.getItemMeta());
    }

    @Deprecated
    public boolean hasID() {
        return !id.isEmpty();
    }

    public static void setCustomDurabilityTag(ItemStack itemStack, String tag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        setCustomDurabilityTag(itemMeta, tag);
        itemStack.setItemMeta(itemMeta);
    }

    public boolean hasNamespacedKey() {
        return namespacedKey != null;
    }

    public static String getCustomDurabilityTag(ItemStack itemStack) {
        return getCustomDurabilityTag(itemStack.getItemMeta());
    }

    public static void setDurabilityTag(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        setDurabilityTag(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    public static void setDurabilityTag(ItemMeta itemMeta) {
        if (itemMeta != null) {
            String tag = WolfyUtilities.hideString("WU_Durability") + WolfyUtilities.translateColorCodes(getCustomDurabilityTag(itemMeta).replace("%dur%", String.valueOf(getCustomDurability(itemMeta) - getCustomDamage(itemMeta))).replace("%max_dur%", String.valueOf(getCustomDurability(itemMeta))));
            List<String> lore = itemMeta.getLore() != null ? itemMeta.getLore() : new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = WolfyUtilities.unhideString(lore.get(i));
                if (line.startsWith("WU_Durability")) {
                    lore.set(i, tag);
                    itemMeta.setLore(lore);
                    return;
                }
            }
            lore.add(tag);
            itemMeta.setLore(lore);
        }
    }

    public ParticleContent getParticleContent() {
        return particleContent;
    }

    public void setParticleContent(ParticleContent particleContent) {
        this.particleContent = particleContent;
    }

    /**
     * These enums are used to link the CustomItem to other external API!
     * <p>
     * CustomItems with DEFAULT can use the internal features of WU like customData, Equipment, etc.
     *
     * @deprecated This feature is not yet implemented and might change completely!
     */
    @Deprecated
    public enum APIReference {
        DEFAULT, ORAXEN, ITEMSADDER, MYTHICMOBS, MMOITEMS
    }

}
