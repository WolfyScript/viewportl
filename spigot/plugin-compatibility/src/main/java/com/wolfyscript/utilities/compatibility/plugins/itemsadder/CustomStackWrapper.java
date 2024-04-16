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

package com.wolfyscript.utilities.compatibility.plugins.itemsadder;

import com.wolfyscript.utilities.bukkit.compatibility.plugins.itemsadder.CustomStack;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomStackWrapper implements CustomStack {

    private final dev.lone.itemsadder.api.CustomStack item;

    private CustomStackWrapper(@NotNull dev.lone.itemsadder.api.CustomStack item) {
        this.item = item;
    }

    public static Optional<CustomStack> wrapStack(@Nullable dev.lone.itemsadder.api.CustomStack customStack) {
        return Optional.ofNullable(wrapNullableStack(customStack));
    }

    @Nullable
    private static CustomStackWrapper wrapNullableStack(@Nullable dev.lone.itemsadder.api.CustomStack customStack) {
        return customStack != null ? new CustomStackWrapper(customStack) : null;
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

    @Override
    public boolean isBlock() {
        return item.isBlock();
    }
}
