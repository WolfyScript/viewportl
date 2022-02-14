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

package me.wolfyscript.utilities.compatibility.plugins.eco;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.willfp.eco.core.items.Items;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class EcoRefImpl extends APIReference implements EcoRef {

    private final NamespacedKey itemKey;

    public EcoRefImpl(NamespacedKey itemKey) {
        super();
        this.itemKey = itemKey;
    }

    public EcoRefImpl(EcoRefImpl ecoRef) {
        super(ecoRef);
        this.itemKey = ecoRef.itemKey;
    }

    @Override
    public ItemStack getLinkedItem() {
        return Items.lookup(itemKey.toString()).getItem();
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return Items.isCustomItem(itemStack);
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("eco", itemKey.toString());
    }

    @Override
    public APIReference clone() {
        return new EcoRefImpl(this);
    }

    @Override
    public NamespacedKey getKey() {
        return itemKey;
    }

    public static class Parser extends PluginParser<EcoRefImpl> {

        public Parser() {
            super("ItemsAdder", "itemsadder");
        }

        @Override
        public @Nullable EcoRefImpl construct(ItemStack itemStack) {
            if (Items.isCustomItem(itemStack)) {
                var customStack = Items.getCustomItem(itemStack);
                if (customStack != null) {
                    return new EcoRefImpl(customStack.getKey());
                }
            }
            return null;
        }

        @Override
        public @Nullable EcoRefImpl parse(JsonNode element) {
            return new EcoRefImpl(NamespacedKey.fromString(element.asText()));
        }
    }
}
