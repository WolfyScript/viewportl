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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.nbt.NBTTagConfig;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigCompound;
import com.wolfyscript.utilities.eval.operator.BoolOperator;
import com.wolfyscript.utilities.eval.operator.BoolOperatorConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderIntegerConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringConst;
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

    protected final WolfyUtils wolfyUtils;

    /* ********************
     * Common NBT Settings
     * ********************/

    /**
     * The display name of the stack.
     * Direct support for Adventure tags.
     */
    protected ValueProvider<String> name;
    /**
     * The lore of the stack. Direct support for Adventure tags.
     */
    protected List<ValueProvider<String>> lore = new ArrayList<>();

    protected ValueProvider<Integer> amount;
    protected ValueProvider<Integer> repairCost;
    protected ValueProvider<Integer> damage;
    protected BoolOperator unbreakable;
    protected ValueProvider<Integer> customModelData;
    protected Map<String, ValueProvider<Integer>> enchants = new HashMap<>();

    /* ********************
     * Unhandled NBT Tags
     * ********************/

    protected NBTTagConfigCompound nbt;

    public ItemStackConfig(WolfyUtils wolfyUtils, String itemId) {
        this.itemId = itemId;
        this.amount = new ValueProviderIntegerConst(wolfyUtils, 1);
        this.name = new ValueProviderStringConst(wolfyUtils, null);
        this.repairCost = new ValueProviderIntegerConst(wolfyUtils, 0);
        this.damage = new ValueProviderIntegerConst(wolfyUtils, 0);
        this.unbreakable = new BoolOperatorConst(wolfyUtils, false);
        this.customModelData = new ValueProviderIntegerConst(wolfyUtils, 0);
        this.nbt = new NBTTagConfigCompound(wolfyUtils, "", null);
        this.wolfyUtils = wolfyUtils;
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

    public ValueProvider<String> getName() {
        return name;
    }

    public void setName(ValueProvider<String> name) {
        this.name = name;
    }

    public List<ValueProvider<String>> getLore() {
        return List.copyOf(lore);
    }

    public void setLore(List<ValueProvider<String>> lore) {
        this.lore = lore;
    }

    public ValueProvider<Integer> getAmount() {
        return amount;
    }

    public void setAmount(ValueProvider<Integer> amount) {
        this.amount = amount;
    }

    public ValueProvider<Integer> getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(ValueProvider<Integer> repairCost) {
        this.repairCost = repairCost;
    }

    public ValueProvider<Integer> getDamage() {
        return damage;
    }

    public void setDamage(ValueProvider<Integer> damage) {
        this.damage = damage;
    }

    public BoolOperator getUnbreakable() {
        return unbreakable;
    }

    public void setUnbreakable(BoolOperator unbreakable) {
        this.unbreakable = unbreakable;
    }

    public ValueProvider<Integer> getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(ValueProvider<Integer> customModelData) {
        this.customModelData = customModelData;
    }

    public Map<String, ValueProvider<Integer>> getEnchants() {
        return Map.copyOf(enchants);
    }

    public void setEnchants(Map<String, ValueProvider<Integer>> enchants) {
        this.enchants = enchants;
    }

    @JsonGetter("nbt")
    public NBTTagConfigCompound getNbt() {
        return nbt;
    }

    public void setNbt(NBTTagConfigCompound nbt) {
        this.nbt = nbt;
    }
}
