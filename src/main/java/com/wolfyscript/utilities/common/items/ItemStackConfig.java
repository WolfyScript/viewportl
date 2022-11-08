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

package com.wolfyscript.utilities.common.items;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class ItemStackConfig<I> {

    /**
     * The id of the item in the <code>&lt;namespace&gt;:&lt;item_key&gt;</code> format.
     */
    protected final String itemId;

    /* ********************
     * Common NBT Settings
     * ********************/

    /**
     * The display name of the stack.
     * Direct support for Adventure tags.
     */
    protected String name = null;
    /**
     * The lore of the stack. Direct support for Adventure tags.
     */
    protected List<String> lore = new ArrayList<>();

    protected int amount = 1;
    protected int repairCost = 0;
    protected int damage = 0;
    protected boolean unbreakable = false;
    protected int customModelData = 0;
    protected Map<String, Integer> enchants = new HashMap<>();

    public ItemStackConfig(String itemId) {
        this.itemId = itemId;
    }

    /**
     * Constructs the implementation specific ItemStack from the settings.
     *
     * @return The constructed ItemStack.
     */
    public abstract I constructItemStack();

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public Map<String, Integer> getEnchants() {
        return enchants;
    }

    public void setEnchants(Map<String, Integer> enchants) {
        this.enchants = enchants;
    }
}
