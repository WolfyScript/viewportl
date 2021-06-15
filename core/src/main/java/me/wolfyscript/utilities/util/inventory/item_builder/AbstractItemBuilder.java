package me.wolfyscript.utilities.util.inventory.item_builder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.wolfyscript.utilities.util.EncryptionUtils;
import me.wolfyscript.utilities.util.chat.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractItemBuilder<T extends AbstractItemBuilder<?>> {

    private static final NamespacedKey CUSTOM_DURABILITY_VALUE = new NamespacedKey("wolfyutilities", "custom_durability.value");
    private static final NamespacedKey CUSTOM_DURABILITY_DAMAGE = new NamespacedKey("wolfyutilities", "custom_durability.damage");
    private static final NamespacedKey CUSTOM_DURABILITY_INDEX = new NamespacedKey("wolfyutilities", "custom_durability.index");
    private static final NamespacedKey CUSTOM_DURABILITY_TAG = new NamespacedKey("wolfyutilities", "custom_durability.tag");

    private final Class<T> typeClass;

    protected AbstractItemBuilder(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    protected abstract ItemStack getItemStack();

    public abstract ItemStack create();

    private T get() {
        return typeClass.cast(this);
    }

    /**
     * @param itemMeta The ItemMeta to add to the ItemStack.
     * @return This {@link AbstractItemBuilder} instance. Used for chaining of methods.
     */
    public T setItemMeta(ItemMeta itemMeta) {
        getItemStack().setItemMeta(itemMeta);
        return get();
    }

    public ItemMeta getItemMeta() {
        return getItemStack().getItemMeta();
    }

    public boolean hasItemMeta() {
        return getItemStack().hasItemMeta();
    }

    public T addEnchantment(@NotNull Enchantment ench, int level) {
        getItemStack().addEnchantment(ench, level);
        return get();
    }

    public T removeEnchantment(@NotNull Enchantment ench) {
        getItemStack().removeEnchantment(ench);
        return get();
    }

    public T addUnsafeEnchantment(@NotNull Enchantment ench, int level) {
        getItemStack().addUnsafeEnchantment(ench, level);
        return get();
    }

    public T addEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        getItemStack().addEnchantments(enchantments);
        return get();
    }

    public T addUnsafeEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        getItemStack().addUnsafeEnchantments(enchantments);
        return get();
    }

    public T addItemFlags(ItemFlag... itemFlags) {
        var itemMeta = getItemStack().getItemMeta();
        itemMeta.addItemFlags(itemFlags);
        return setItemMeta(itemMeta);
    }

    public T removeItemFlags(ItemFlag... itemFlags) {
        var itemMeta = getItemStack().getItemMeta();
        itemMeta.removeItemFlags(itemFlags);
        return setItemMeta(itemMeta);
    }

    public T setDisplayName(String name) {
        var itemMeta = getItemStack().getItemMeta();
        itemMeta.setDisplayName(name);
        return setItemMeta(itemMeta);
    }

    public T setType(Material type) {
        getItemStack().setType(type);
        return get();
    }

    public T setLore(List<String> lore) {
        var itemMeta = getItemStack().getItemMeta();
        itemMeta.setLore(lore);
        return setItemMeta(itemMeta);
    }

    public T addLoreLine(String line) {
        var itemMeta = getItemStack().getItemMeta();
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.add(line);
        return setLore(lore);
    }

    public T addLoreLine(int index, String line) {
        var itemMeta = getItemStack().getItemMeta();
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.add(index, line);
        return setLore(lore);
    }

    /**
     * Checks if this item has Custom Durability set.
     */
    public boolean hasCustomDurability() {
        var itemMeta = getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            return dataContainer.has(CUSTOM_DURABILITY_VALUE, PersistentDataType.INTEGER);
        }
        return false;
    }

    public T setCustomDamage(int damage) {
        var itemMeta = getItemMeta();
        if (itemMeta != null) {
            var dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.set(CUSTOM_DURABILITY_DAMAGE, PersistentDataType.INTEGER, damage);
            updateCustomDurabilityTag(itemMeta);
        }
        if (itemMeta instanceof Damageable) {
            int value = (int) (getItemStack().getType().getMaxDurability() * ((double) damage / (double) getCustomDurability(itemMeta)));
            if (damage > 0 && value <= 0) {
                value = damage;
            }
            ((Damageable) itemMeta).setDamage(value);
        }
        return setItemMeta(itemMeta);
    }

    public int getCustomDamage() {
        return getCustomDamage(getItemMeta());
    }

    public int getCustomDamage(ItemMeta itemMeta) {
        if (itemMeta != null) {
            var dataContainer = itemMeta.getPersistentDataContainer();
            if (dataContainer.has(CUSTOM_DURABILITY_DAMAGE, PersistentDataType.INTEGER)) {
                return dataContainer.get(CUSTOM_DURABILITY_DAMAGE, PersistentDataType.INTEGER);
            }
        }
        return 0;
    }

    public T setCustomDurability(int durability) {
        var itemMeta = getItemMeta();
        if (itemMeta != null) {
            var dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.set(CUSTOM_DURABILITY_VALUE, PersistentDataType.INTEGER, durability);
            if (!dataContainer.has(CUSTOM_DURABILITY_DAMAGE, PersistentDataType.INTEGER)) {
                dataContainer.set(CUSTOM_DURABILITY_DAMAGE, PersistentDataType.INTEGER, 0);
            }
            updateCustomDurabilityTag();
        }
        return setItemMeta(itemMeta);
    }

    public int getCustomDurability() {
        return getCustomDurability(getItemMeta());
    }

    public int getCustomDurability(ItemMeta itemMeta) {
        if (itemMeta != null) {
            var dataContainer = itemMeta.getPersistentDataContainer();
            if (dataContainer.has(CUSTOM_DURABILITY_VALUE, PersistentDataType.INTEGER)) {
                return dataContainer.get(CUSTOM_DURABILITY_VALUE, PersistentDataType.INTEGER);
            }
        }
        return 0;
    }

    public T removeCustomDurability() {
        var itemMeta = getItemMeta();
        if (itemMeta != null) {
            var dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.remove(CUSTOM_DURABILITY_VALUE);
            dataContainer.remove(CUSTOM_DURABILITY_DAMAGE);
            dataContainer.remove(CUSTOM_DURABILITY_TAG);
        }
        return setItemMeta(itemMeta);
    }

    public T setCustomDurabilityTag(String tag) {
        var itemMeta = getItemMeta();
        if (itemMeta != null) {
            var dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.set(CUSTOM_DURABILITY_TAG, PersistentDataType.STRING, tag);
            updateCustomDurabilityTag();
        }
        return setItemMeta(itemMeta);
    }

    public String getCustomDurabilityTag() {
        return getCustomDurabilityTag(getItemMeta());
    }

    public String getCustomDurabilityTag(ItemMeta itemMeta) {
        if (itemMeta != null) {
            var dataContainer = itemMeta.getPersistentDataContainer();
            if (dataContainer.has(CUSTOM_DURABILITY_TAG, PersistentDataType.STRING)) {
                return dataContainer.get(CUSTOM_DURABILITY_TAG, PersistentDataType.STRING);
            }
        }
        return "";
    }

    public T updateCustomDurabilityTag() {
        var itemMeta = getItemMeta();
        updateCustomDurabilityTag(itemMeta);
        return setItemMeta(itemMeta);
    }

    public void updateCustomDurabilityTag(ItemMeta itemMeta) {
        if (itemMeta != null) {
            String tag = ChatColor.convert(getCustomDurabilityTag().replace("%dur%", String.valueOf(getCustomDurability(itemMeta) - getCustomDamage(itemMeta))).replace("%max_dur%", String.valueOf(getCustomDurability(itemMeta))));
            var dataContainer = itemMeta.getPersistentDataContainer();
            List<String> lore = itemMeta.getLore() != null ? itemMeta.getLore() : new ArrayList<>();
            if (dataContainer.has(CUSTOM_DURABILITY_INDEX, PersistentDataType.INTEGER)) {
                int index = dataContainer.get(CUSTOM_DURABILITY_INDEX, PersistentDataType.INTEGER);
                if (index < lore.size()) {
                    lore.set(index, tag);
                    itemMeta.setLore(lore);
                    return;
                }
            } else {
                dataContainer.set(CUSTOM_DURABILITY_INDEX, PersistentDataType.INTEGER, lore.size());
            }
            lore.add(tag);
            itemMeta.setLore(lore);
        }
    }

    public T removePlayerHeadValue() {
        if (getItemMeta() instanceof SkullMeta skullMeta) {
            //GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            //profile.getProperties().put("textures", new Property("textures", null));
            Field profileField = null;
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, null);
            } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return setItemMeta(skullMeta);
        }
        return get();
    }

    public T setPlayerHeadURL(String value) {
        if (value.startsWith("http://textures.minecraft.net/texture/")) {
            return setPlayerHeadValue(value);
        }
        return setPlayerHeadValue("http://textures.minecraft.net/texture/" + value);
    }

    public String getPlayerHeadValue() {
        if (getItemMeta() instanceof SkullMeta skullMeta) {
            GameProfile profile = null;
            Field profileField;
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profile = (GameProfile) profileField.get(skullMeta);
            } catch (NoSuchFieldException | SecurityException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
            if (profile != null && !profile.getProperties().get("textures").isEmpty()) {
                for (Property property : profile.getProperties().get("textures")) {
                    if (!property.getValue().isEmpty())
                        return property.getValue();
                }
            }
        }
        return "";
    }

    public T setPlayerHeadValue(String value) {
        if (getItemMeta() instanceof SkullMeta skullMeta) {
            if (value != null && !value.isEmpty()) {
                String texture = value;
                if (value.startsWith("https://") || value.startsWith("http://")) {
                    texture = EncryptionUtils.getBase64EncodedString(String.format("{textures:{SKIN:{url:\"%s\"}}}", value));
                }
                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", texture));
                Field profileField = null;
                try {
                    profileField = skullMeta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(skullMeta, profile);
                } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return setItemMeta(skullMeta);
        }
        return get();
    }


}
