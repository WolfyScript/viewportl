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

package me.wolfyscript.utilities.api.inventory.custom_items.meta;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MetaSettings {

    public static final String CHECKS_KEY = "checks";

    private final List<Meta> checks;

    /**
     * Creates a new settings object with an empty list of checks.
     */
    public MetaSettings() {
        checks = new ArrayList<>();
    }

    /**
     * @param meta The {@link Meta} to add to the list of checks. Cannot be null, and must not have the {@link Option#IGNORE}, or it will throw an exception!
     */
    public void addCheck(@NotNull Meta meta) {
        Objects.requireNonNull(meta, "Meta check cannot be null!");
        Preconditions.checkArgument(!meta.getOption().equals(Option.IGNORE), "Deprecated option! Ignored check cannot be added!");
        checks.add(meta);
    }

    public void clearChecks() {
        checks.clear();
    }

    public List<Meta> getChecks() {
        return List.copyOf(checks);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return checks.isEmpty();
    }

    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        return true;
    }

    public boolean check(CustomItem item, ItemBuilder itemOther) {
        return checks.stream().allMatch(meta -> meta.check(item, itemOther));
    }

    public enum Option {
        EXACT,
        /**
         * This option was originally used to indicate if the meta check should be active.
         * Now it is no longer used (Only to convert old data), because checks that are not added to the settings are well... not checked anyway.
         */
        @Deprecated IGNORE,
        HIGHER,
        HIGHER_EXACT,
        LOWER,
        LOWER_EXACT,
        HIGHER_LOWER
    }

}
