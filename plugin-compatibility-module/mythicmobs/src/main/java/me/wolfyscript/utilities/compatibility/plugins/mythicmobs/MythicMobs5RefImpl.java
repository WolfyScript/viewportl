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

import com.fasterxml.jackson.databind.JsonNode;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Links to MythicMobs items and saves the specified item type.
 * <p>
 * For items to be detected by plugins in-game you need to add an additional Option to your MythicMobs item!
 * <pre>
 * Options:
 *     AppendType: true
 * </pre>
 */
public class MythicMobs5RefImpl extends AbstractMythicMobsRef {

    public MythicMobs5RefImpl(String itemName) {
        super(itemName);
    }

    public MythicMobs5RefImpl(MythicMobs5RefImpl mythicMobsRefImpl) {
        super(mythicMobsRefImpl);
    }

    @Override
    public ItemStack getLinkedItem() {
        return MythicBukkit.inst().getItemManager().getItemStack(itemName);
    }

    @Override
    public MythicMobs5RefImpl clone() {
        return new MythicMobs5RefImpl(this);
    }

    public static class Parser extends AbstractMythicMobsRef.Parser<MythicMobs5RefImpl> {

        @Override
        protected MythicMobs5RefImpl construct(String value) {
            if (MythicBukkit.inst().getItemManager().getItem(value).isPresent()) {
                return new MythicMobs5RefImpl(value);
            }
            return null;
        }

        @Override
        public @Nullable
        MythicMobs5RefImpl parse(JsonNode element) {
            return new MythicMobs5RefImpl(element.asText());
        }

    }
}
