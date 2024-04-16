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

package com.wolfyscript.utilities.bukkit.world.inventory.item_builder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.WolfyUtils;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class AbstractItemBuilder<T extends AbstractItemBuilder<?>> {

    private static final NamespacedKey CUSTOM_DURABILITY_VALUE = new NamespacedKey("wolfyutilities", "custom_durability.value");
    private static final NamespacedKey CUSTOM_DURABILITY_DAMAGE = new NamespacedKey("wolfyutilities", "custom_durability.damage");
    private static final NamespacedKey CUSTOM_DURABILITY_INDEX = new NamespacedKey("wolfyutilities", "custom_durability.index");
    private static final NamespacedKey CUSTOM_DURABILITY_TAG = new NamespacedKey("wolfyutilities", "custom_durability.tag");
    private static final NamespacedKey CUSTOM_DURABILITY_TAG_CONTENT = new NamespacedKey("wolfyutilities", "content");
    private static final NamespacedKey CUSTOM_DURABILITY_TAG_MINIMSG = new NamespacedKey("wolfyutilities", "mini_msg");

    @JsonIgnore
    protected final WolfyUtilsBukkit wolfyUtils;
    @JsonIgnore
    private final Class<T> typeClass;
    @JsonIgnore
    private final MiniMessage miniMessage;

    protected AbstractItemBuilder(WolfyUtils wolfyUtils, Class<T> typeClass) {
        this.typeClass = typeClass;
        this.wolfyUtils = (WolfyUtilsBukkit) wolfyUtils;
        this.miniMessage = wolfyUtils.getChat().getMiniMessage();
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

    @JsonIgnore
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

    @Deprecated
    public T setDisplayName(String name) {
        var itemMeta = getItemStack().getItemMeta();
        itemMeta.setDisplayName(name);
        return setItemMeta(itemMeta);
    }

    public T displayName(Component name) {
        var itemMeta = getItemStack().getItemMeta();
        if (wolfyUtils.getCore().getPlatform().getType().isPaper()) {
            itemMeta.displayName(name);
        } else {
            itemMeta.setDisplayName(BukkitComponentSerializer.legacy().serialize(name));
        }
        return setItemMeta(itemMeta);
    }

    public T setType(Material type) {
        getItemStack().setType(type);
        return get();
    }

    @Deprecated
    public T setLore(List<String> lore) {
        var itemMeta = getItemStack().getItemMeta();
        itemMeta.setLore(lore);
        return setItemMeta(itemMeta);
    }

    public T lore(List<Component> lore) {
        var itemMeta = getItemStack().getItemMeta();
        if (wolfyUtils.getCore().getPlatform().getType().isPaper()) {
            itemMeta.lore(lore);
        } else {
            itemMeta.setLore(lore.stream().map(line -> BukkitComponentSerializer.legacy().serialize(line)).toList());
        }
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
            return itemMeta.getPersistentDataContainer().has(CUSTOM_DURABILITY_VALUE, PersistentDataType.INTEGER);
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
            return itemMeta.getPersistentDataContainer().getOrDefault(CUSTOM_DURABILITY_DAMAGE, PersistentDataType.INTEGER, 0);
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
            return itemMeta.getPersistentDataContainer().getOrDefault(CUSTOM_DURABILITY_VALUE, PersistentDataType.INTEGER, 0);
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
            var tagContainer = dataContainer.getAdapterContext().newPersistentDataContainer();
            tagContainer.set(CUSTOM_DURABILITY_TAG_CONTENT, PersistentDataType.STRING, tag);
            tagContainer.set(CUSTOM_DURABILITY_TAG_MINIMSG, PersistentDataType.BYTE, (byte) 1);
            dataContainer.set(CUSTOM_DURABILITY_TAG, PersistentDataType.TAG_CONTAINER, tagContainer);
            updateCustomDurabilityTag();
        }
        return setItemMeta(itemMeta);
    }

    /**
     * Returns the raw tag content that is specified for this ItemMeta.<br>
     *
     * @return The raw content (text) of the durability tag.
     */
    public String getCustomDurabilityTag() {
        return getCustomDurabilityTag(getItemMeta());
    }

    /**
     * Returns the raw tag content that is specified for this ItemMeta.<br>
     *
     * @param itemMeta The ItemMeta, from which to get the tag.
     * @return The raw content (text) of the durability tag.
     */
    public String getCustomDurabilityTag(ItemMeta itemMeta) {
        if (itemMeta != null) {
            var dataContainer = itemMeta.getPersistentDataContainer();
            var miniMsg = false;
            var content = "";
            if (dataContainer.has(CUSTOM_DURABILITY_TAG, PersistentDataType.TAG_CONTAINER)) {
                var tagContainer = dataContainer.get(CUSTOM_DURABILITY_TAG, PersistentDataType.TAG_CONTAINER);
                miniMsg = tagContainer.getOrDefault(CUSTOM_DURABILITY_TAG_MINIMSG, PersistentDataType.BYTE, (byte) 1) == 1;
                content = tagContainer.getOrDefault(CUSTOM_DURABILITY_TAG_CONTENT, PersistentDataType.STRING, "");
            } else if (dataContainer.has(CUSTOM_DURABILITY_TAG, PersistentDataType.STRING)) {
                //Using old tag version
                content = dataContainer.getOrDefault(CUSTOM_DURABILITY_TAG, PersistentDataType.STRING, "").replace("%dur%", "<dur>").replace("%max_dur%", "<max_dur>");
            }
            return miniMsg ? content : miniMessage.serialize(BukkitComponentSerializer.legacy().deserialize(content));
        }
        return "";
    }

    public Component getCustomDurabilityTagComponent() {
        return getCustomDurabilityTagComponent(getItemMeta());
    }

    public Component getCustomDurabilityTagComponent(ItemMeta itemMeta) {
        return miniMessage.deserialize(getCustomDurabilityTag(itemMeta), Placeholder.parsed("dur", String.valueOf(getCustomDurability(itemMeta) - getCustomDamage(itemMeta))), Placeholder.parsed("max_dur", String.valueOf(getCustomDurability(itemMeta))));
    }

    public T updateCustomDurabilityTag() {
        var itemMeta = getItemMeta();
        updateCustomDurabilityTag(itemMeta);
        return setItemMeta(itemMeta);
    }

    public void updateCustomDurabilityTag(ItemMeta itemMeta) {
        if (itemMeta != null) {
            String tag = BukkitComponentSerializer.legacy().serialize(getCustomDurabilityTagComponent(itemMeta));
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

    public T setPlayerHeadURL(String value, String name, UUID uuid) {
        if (value.startsWith("http://textures.minecraft.net/texture/")) {
            return setPlayerHeadValue(value, name, uuid);
        }
        return setPlayerHeadValue("http://textures.minecraft.net/texture/" + value, name, uuid);
    }

    public String getPlayerHeadValue() {
        if (getItemMeta() instanceof SkullMeta) {
            NBTItem nbtItem = new NBTItem(getItemStack());
            NBTCompound skull = nbtItem.getCompound("SkullOwner");
            if (skull != null) {
                if(skull.hasKey("Properties")) {
                    NBTCompound properties = skull.getCompound("Properties");
                    if (properties.hasKey("textures")) {
                        NBTCompoundList textures = properties.getCompoundList("textures");
                        if (textures.size() > 0) {
                            NBTCompound object = textures.get(0);
                            String value = object.getString("Value");
                            return value != null ? value : "";
                        }
                    }
                }
            }
        }
        return "";
    }

    public T setPlayerHeadValue(String value, String name, UUID uuid) {
        Preconditions.checkArgument(!name.isEmpty(), "Name of Skull cannot be empty!");
        String textureValue = value;
        if (value.startsWith("https://") || value.startsWith("http://")) {
            textureValue = Base64.getEncoder().encodeToString(String.format("{textures:{SKIN:{url:\"%s\"}}}", value).getBytes(StandardCharsets.UTF_8));
        }
        NBTItem nbtItem = new NBTItem(getItemStack(), true);
        NBTCompound skull = nbtItem.addCompound("SkullOwner");
        skull.setString("Name", name);
        skull.setString("Id", uuid.toString());
        // The UUID, note that skulls with the same UUID but different textures will misbehave and only one texture will load
        // (They'll share it), if skulls have different UUIDs and same textures they won't stack. See UUID.randomUUID();
        NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
        texture.setString("Value",  textureValue);
        return get();
    }

    public T setPlayerHeadValue(String value) {
        return setPlayerHeadValue(value, "none", UUID.randomUUID());
    }


}
