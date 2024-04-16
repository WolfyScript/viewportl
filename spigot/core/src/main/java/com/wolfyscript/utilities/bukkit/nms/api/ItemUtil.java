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

package com.wolfyscript.utilities.bukkit.nms.api;

import java.io.IOException;

@Deprecated(since = "4.16.2.0")
public abstract class ItemUtil extends UtilComponent {

    protected ItemUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    /**
     * Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
     * for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
     * Or to save it in the vanilla style Json String.
     *
     * @param itemStack the item to convert
     * @return the Json string representation of the item in NMS style.
     */
    public abstract String getItemStackJson(org.bukkit.inventory.ItemStack itemStack);

    /**
     * Converts the NMS Json Sting to an {@link org.bukkit.inventory.ItemStack}.
     *
     * @param json the NMS json to convert
     * @return the ItemStack representation of the Json String
     */
    public abstract org.bukkit.inventory.ItemStack getJsonItemStack(String json);

    public abstract String getItemStackBase64(org.bukkit.inventory.ItemStack itemStack) throws IOException;

    public abstract org.bukkit.inventory.ItemStack getBase64ItemStack(String data) throws IOException;

    public abstract org.bukkit.inventory.ItemStack getBase64ItemStack(byte[] bytes) throws IOException;
}
