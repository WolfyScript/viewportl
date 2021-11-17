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

package me.wolfyscript.utilities.api.inventory.custom_items.references;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public class MagicRef extends APIReference {

    private final String itemKey;

    public MagicRef(String itemKey) {
        super();
        this.itemKey = itemKey;
    }

    public MagicRef(MagicRef ref) {
        super(ref);
        this.itemKey = ref.itemKey;
    }

    @Override
    public ItemStack getLinkedItem() {
        return Parser.magicAPI.createItem(itemKey);
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return Objects.equals(Parser.magicAPI.getItemKey(itemStack), itemKey);
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("magic", itemKey);
    }

    @Override
    public MagicRef clone() {
        return new MagicRef(this);
    }

    public static class Parser extends PluginParser<MagicRef> {

        private static MagicAPI magicAPI = null;

        public Parser() {
            super("Magic", "magic");
        }

        @Override
        public void init(Plugin plugin) {
            if(plugin instanceof MagicAPI api) {
                magicAPI = api;
            }
        }

        @Override
        public @Nullable MagicRef construct(ItemStack itemStack) {
            String key = magicAPI.getItemKey(itemStack);
            if(key != null && !key.isBlank()) {
                return new MagicRef(key);
            }
            return null;
        }

        @Override
        public @Nullable MagicRef parse(JsonNode element) {
            return new MagicRef(element.asText());
        }
    }
}
