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

package com.wolfyscript.utilities.bukkit.world.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerHeadUtils {

    /**
     * Gets the Player Head ItemStack via a URL or Base64 encoded string.
     * <p>This method uses the {@link ItemBuilder}!
     *
     * @see ItemBuilder#setPlayerHeadValue(String)
     * @param value Skin URL or Base64 encoded value of textures object
     * @return the Player Head ItemStack with the corresponding Texture
     */
    public static ItemStack getViaValue(String value) {
        return new ItemBuilder(WolfyCoreSpigot.getInstance().getWolfyUtils(), Material.PLAYER_HEAD).setPlayerHeadValue(value).create();
    }

    /**
     * Gets the Player Head ItemStack via a URL or Base64 encoded string.
     * <p>This method uses the {@link ItemBuilder}!
     *
     * @see ItemBuilder#setPlayerHeadValue(String)
     * @param value Skin URL or Base64 encoded value of textures object
     * @param name The name of the skull owner
     * @param uuid The uuid of the skull owner
     * @return the Player Head ItemStack with the corresponding Texture
     */
    public static ItemStack getViaValue(String value, String name, UUID uuid) {
        return new ItemBuilder(WolfyCoreSpigot.getInstance().getWolfyUtils(), Material.PLAYER_HEAD).setPlayerHeadValue(value, name, uuid).create();
    }

    /**
     * Get the Player Head via URL value
     * <p>This method uses the {@link ItemBuilder}!
     *
     * @see ItemBuilder#setPlayerHeadURL(String)
     * @param value the Base64 value at the end of the textures url.
     *              <p>e.g. http://textures.minecraft.net/texture/{value}
     * @return the Player Head ItemStack with the corresponding Texture
     */
    public static ItemStack getViaURL(String value) {
        return new ItemBuilder(WolfyCoreSpigot.getInstance().getWolfyUtils(), Material.PLAYER_HEAD).setPlayerHeadURL(value).create();
    }

    /**
     * Get the Player Head via URL value
     * <p>This method uses the {@link ItemBuilder}!
     *
     * @see ItemBuilder#setPlayerHeadURL(String)
     * @param value the Base64 value at the end of the textures url.
     *              <p>e.g. http://textures.minecraft.net/texture/{value}
     * @param name The name of the skull owner
     * @param uuid The uuid of the skull owner
     *
     * @return the Player Head ItemStack with the corresponding Texture
     */
    public static ItemStack getViaURL(String value, String name, UUID uuid) {
        return new ItemBuilder(WolfyCoreSpigot.getInstance().getWolfyUtils(), Material.PLAYER_HEAD).setPlayerHeadURL(value, name, uuid).create();
    }

    /**
     * Gets the SkullMeta for this Texture value.
     *
     *
     * @param value the texture value. Base64 or texture link
     * @param skullMeta the {@link SkullMeta} the texture value should be added to
     * @return the original skullMeta with the new texture value
     */
    @Deprecated
    public static SkullMeta getSkullMeta(String value, SkullMeta skullMeta) {
        if (value != null && !value.isEmpty()) {
            String texture = value;
            if (value.startsWith("https://") || value.startsWith("http://")) {
                texture = Base64.getEncoder().encodeToString(String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", value).getBytes());
            }
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", texture));
            Field profileField = null;
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            try {
                profileField.set(skullMeta, profile);
                return skullMeta;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return skullMeta;
    }

    @Deprecated
    public static String getTextureValue(SkullMeta skullMeta) {
        GameProfile profile = null;
        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            try {
                profile = (GameProfile) profileField.get(skullMeta);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException | SecurityException ex) {
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
        return null;
    }

    /**
     * Both items Material must be equal to PLAYER_HEAD!
     * <p>
     * Returns result with the texture from the input
     * <p>
     * Returns result when one of the items is not Player Head
     */
    public static ItemStack migrateTexture(ItemStack input, ItemStack result) {
        if (input.getType().equals(Material.PLAYER_HEAD) && result.getType().equals(Material.PLAYER_HEAD)) {
            SkullMeta inputMeta = (SkullMeta) input.getItemMeta();
            String value = getTextureValue(inputMeta);
            if (value != null && !value.isEmpty()) {
                result.setItemMeta(getSkullMeta(value, (SkullMeta) result.getItemMeta()));
            }
        }
        return result;
    }

    /**
     * Result's Material must be equal to PLAYER_HEAD!
     * <p>
     * Returns ItemMeta with the texture from the input
     */
    public static ItemMeta migrateTexture(SkullMeta input, ItemStack result) {
        if (result.getType().equals(Material.PLAYER_HEAD)) {
            String value = getTextureValue(input);
            if (value != null && !value.isEmpty()) {
                return getSkullMeta(value, (SkullMeta) result.getItemMeta());
            }
        }
        return result.getItemMeta();
    }
}
