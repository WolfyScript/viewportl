package me.wolfyscript.utilities.api.utils.inventory.item_builder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.EncryptionUtils;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractItemBuilder<T extends AbstractItemBuilder> {

    abstract protected ItemStack getItemStack();

    abstract public ItemStack create();

    /**
     * @param itemMeta
     * @return
     */
    public T setItemMeta(ItemMeta itemMeta) {
        getItemStack().setItemMeta(itemMeta);
        return (T) this;
    }

    public ItemMeta getItemMeta() {
        return getItemStack().getItemMeta();
    }

    public boolean hasItemMeta(){
        return getItemStack().hasItemMeta();
    }

    public T addEnchantment(@Nonnull Enchantment ench, int level) {
        getItemStack().addEnchantment(ench, level);
        return (T) this;
    }

    public T removeEnchantment(@Nonnull Enchantment ench) {
        getItemStack().removeEnchantment(ench);
        return (T) this;
    }

    public T addUnsafeEnchantment(@Nonnull Enchantment ench, int level) {
        getItemStack().addUnsafeEnchantment(ench, level);
        return (T) this;
    }

    public T addEnchantments(@Nonnull Map<Enchantment, Integer> enchantments) {
        getItemStack().addEnchantments(enchantments);
        return (T) this;
    }

    public T addUnsafeEnchantments(@Nonnull Map<Enchantment, Integer> enchantments) {
        getItemStack().addUnsafeEnchantments(enchantments);
        return (T) this;
    }

    public T addItemFlags(ItemFlag... itemFlags) {
        ItemMeta itemMeta = getItemStack().getItemMeta();
        itemMeta.addItemFlags(itemFlags);
        return setItemMeta(itemMeta);
    }

    public T removeItemFlags(ItemFlag... itemFlags) {
        ItemMeta itemMeta = getItemStack().getItemMeta();
        itemMeta.removeItemFlags(itemFlags);
        return setItemMeta(itemMeta);
    }

    public T setDisplayName(String name) {
        ItemMeta itemMeta = getItemStack().getItemMeta();
        itemMeta.setDisplayName(name);
        return setItemMeta(itemMeta);
    }

    public T setType(Material type){
        getItemStack().setType(type);
        return (T) this;
    }

    public T setLore(List<String> lore) {
        ItemMeta itemMeta = getItemStack().getItemMeta();
        itemMeta.setLore(lore);
        return setItemMeta(itemMeta);
    }

    public T addLoreLine(String line) {
        ItemMeta itemMeta = getItemStack().getItemMeta();
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.add(line);
        return setLore(lore);
    }

    public T addLoreLine(int index, String line) {
        ItemMeta itemMeta = getItemStack().getItemMeta();
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.add(index, line);
        return setLore(lore);
    }

    /**
     * Checks if this item has Custom Durability set.
     */
    public boolean hasCustomDurability() {
        ItemMeta itemMeta = getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            return dataContainer.has(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.value"), PersistentDataType.INTEGER);
        }
        return false;
    }

    public T setCustomDamage(int damage) {
        ItemMeta itemMeta = getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.set(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"), PersistentDataType.INTEGER, damage);
            updateCustomDurabilityTag();
        }
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage((int) (getItemStack().getType().getMaxDurability() * ((double) damage / (double) getCustomDurability())));
        }
        return setItemMeta(itemMeta);
    }

    public int getCustomDamage() {
        ItemMeta itemMeta = getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (dataContainer.has(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"), PersistentDataType.INTEGER)) {
                return dataContainer.get(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"), PersistentDataType.INTEGER);
            }
        }
        return 0;
    }

    public T setCustomDurability(int durability) {
        ItemMeta itemMeta = getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.set(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.value"), PersistentDataType.INTEGER, durability);
            if (!dataContainer.has(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"), PersistentDataType.INTEGER)) {
                dataContainer.set(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"), PersistentDataType.INTEGER, 0);
            }
            updateCustomDurabilityTag();
        }
        return setItemMeta(itemMeta);
    }

    public int getCustomDurability() {
        ItemMeta itemMeta = getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (dataContainer.has(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.value"), PersistentDataType.INTEGER)) {
                return dataContainer.get(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.value"), PersistentDataType.INTEGER);
            }
        }
        return 0;
    }

    public T removeCustomDurability() {
        ItemMeta itemMeta = getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.remove(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.value"));
            dataContainer.remove(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.damage"));
            dataContainer.remove(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.tag"));
        }
        return setItemMeta(itemMeta);
    }

    public T setCustomDurabilityTag(String tag) {
        ItemMeta itemMeta = getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            dataContainer.set(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.tag"), PersistentDataType.STRING, tag);
            updateCustomDurabilityTag();
        }
        return setItemMeta(itemMeta);
    }

    public String getCustomDurabilityTag() {
        ItemMeta itemMeta = getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (dataContainer.has(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.tag"), PersistentDataType.STRING)) {
                return dataContainer.get(new org.bukkit.NamespacedKey(Main.getInstance(), "customDurability.tag"), PersistentDataType.STRING);
            }
        }
        return "";
    }

    public T updateCustomDurabilityTag() {
        ItemMeta itemMeta = getItemMeta();
        if (itemMeta != null) {
            String tag = WolfyUtilities.hideString("WU_Durability") + WolfyUtilities.translateColorCodes(getCustomDurabilityTag().replace("%dur%", String.valueOf(getCustomDurability() - getCustomDamage())).replace("%max_dur%", String.valueOf(getCustomDurability())));
            List<String> lore = itemMeta.getLore() != null ? itemMeta.getLore() : new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = WolfyUtilities.unhideString(lore.get(i));
                if (line.startsWith("WU_Durability")) {
                    lore.set(i, tag);
                    itemMeta.setLore(lore);
                    return setItemMeta(itemMeta);
                }
            }
            lore.add(tag);
            itemMeta.setLore(lore);
        }
        return setItemMeta(itemMeta);
    }

    public T setPlayerHeadValue(String value) {
        if (getItemMeta() instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) getItemMeta();
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
        return (T) this;
    }

    public T setPlayerHeadURL(String value){
        if (value.startsWith("http://textures.minecraft.net/texture/")) {
            return setPlayerHeadValue(value);
        }
        return setPlayerHeadValue("http://textures.minecraft.net/texture/"+value);
    }

    public T removePlayerHeadValue() {
        if (getItemMeta() instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) getItemMeta();

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
        return (T) this;
    }

    public String getPlayerHeadValue() {
        if (getItemMeta() instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) getItemMeta();
            GameProfile profile = null;
            Field profileField;
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profile = (GameProfile) profileField.get(skullMeta);
            } catch (NoSuchFieldException | SecurityException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
            if (profile != null) {
                if (!profile.getProperties().get("textures").isEmpty()) {
                    for (Property property : profile.getProperties().get("textures")) {
                        if (!property.getValue().isEmpty())
                            return property.getValue();
                    }
                }
            }
        }
        return "";
    }


}
