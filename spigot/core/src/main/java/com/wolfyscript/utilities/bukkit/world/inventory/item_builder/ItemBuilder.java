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

import com.wolfyscript.utilities.WolfyUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder extends AbstractItemBuilder<ItemBuilder> {

    private final ItemStack itemStack;

    public ItemBuilder(WolfyUtils wolfyUtils, ItemStack itemStack){
        super(wolfyUtils, ItemBuilder.class);
        this.itemStack = itemStack;
    }

    public ItemBuilder(WolfyUtils wolfyUtils, Material material){
        super(wolfyUtils, ItemBuilder.class);
        this.itemStack = new ItemStack(material);
    }

    @Override
    protected ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public ItemStack create() {
        return itemStack;
    }
}
