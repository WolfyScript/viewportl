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

package me.wolfyscript.utilities.compatibility.plugins.itemsadder;

import org.bukkit.inventory.ItemStack;
import dev.lone.itemsadder.api.CustomStack;

public class CustomStackWrapper implements me.wolfyscript.utilities.compatibility.plugins.itemsadder.CustomStack {

    private final CustomStack item;

    public CustomStackWrapper(CustomStack item) {
        this.item = item;
    }

    @Override
    public ItemStack getItemStack() {
        return item.getItemStack();
    }

    @Override
    public String getNamespace() {
        return item.getNamespace();
    }

    @Override
    public String getId() {
        return item.getId();
    }

    @Override
    public String getNamespacedID() {
        return item.getNamespacedID();
    }

    @Override
    public String getPermission() {
        return item.getPermission();
    }

    @Override
    public boolean hasPermission() {
        return item.hasPermission();
    }

    @Override
    public boolean isBlockAllEnchants() {
        return item.isBlockAllEnchants();
    }

    @Override
    public boolean hasUsagesAttribute() {
        return item.hasUsagesAttribute();
    }

    @Override
    public void setUsages(int amount) {
        item.setUsages(amount);
    }

    @Override
    public void reduceUsages(int amount) {
        item.reduceUsages(amount);
    }

    @Override
    public int getUsages() {
        return item.getUsages();
    }

    @Override
    public boolean hasCustomDurability() {
        return item.hasCustomDurability();
    }

    @Override
    public int getDurability() {
        return item.getDurability();
    }

    @Override
    public void setDurability(int durability) {
        item.setDurability(durability);
    }

    @Override
    public int getMaxDurability() {
        return item.getMaxDurability();
    }
}
