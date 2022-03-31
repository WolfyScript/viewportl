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

package me.wolfyscript.utilities.compatibility.plugins.mythicmobs;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.nms.nbt.NBTItem;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagString;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public abstract class AbstractMythicMobsRef extends APIReference implements MythicMobsRef {

    protected static final String ITEM_KEY = "MYTHIC_TYPE";

    protected final String itemName;

    public AbstractMythicMobsRef(String itemName) {
        super();
        this.itemName = itemName;
    }

    public AbstractMythicMobsRef(AbstractMythicMobsRef mythicMobsRefImpl) {
        super(mythicMobsRefImpl);
        this.itemName = mythicMobsRefImpl.itemName;
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        var nbtItem = WolfyUtilities.getWUCore().getNmsUtil().getNBTUtil().getItem(itemStack);
        if (nbtItem != null && nbtItem.hasKey(ITEM_KEY) && nbtItem.getTag(ITEM_KEY) instanceof NBTTagString nbtTagString) {
            return Objects.equals(this.itemName, nbtTagString.asString());
        }
        return false;
    }

    /**
     * Used to serialize the APIReferenc Object to Json
     *
     * @param gen      the initial JsonGenerator containing the custom amount field
     * @param provider
     */
    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("mythicmobs", itemName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MythicMobsRefImpl that = (MythicMobsRefImpl) o;
        return Objects.equals(itemName, that.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemName);
    }

    protected static abstract class Parser<T extends AbstractMythicMobsRef> extends APIReference.PluginParser<T> {

        public Parser() {
            super("MythicMobs", "mythicmobs");
        }

        @Override
        public @Nullable
        T construct(ItemStack itemStack) {
            NBTItem nbtItem = WolfyUtilities.getWUCore().getNmsUtil().getNBTUtil().getItem(itemStack);
            if (nbtItem != null && nbtItem.hasKey(ITEM_KEY) && nbtItem.getTag(ITEM_KEY) instanceof NBTTagString nbtTagString) {
                String name = nbtTagString.asString();
                return construct(name);
            }
            return null;
        }

        protected abstract T construct(String value);

    }

}
