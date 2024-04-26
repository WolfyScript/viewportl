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

package com.wolfyscript.utilities.eval.value_provider;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.WolfyCore;

public abstract class AbstractValueProvider<V> implements ValueProvider<V> {

    @JsonIgnore
    protected final NamespacedKey key;

    protected AbstractValueProvider(NamespacedKey key) {
        this.key = key;
    }

    protected AbstractValueProvider() {
        this.key = WolfyCore.getInstance().getWolfyUtils().getIdentifiers().getNamespaced(getClass());
    }

    @JsonIgnore
    @Override
    public NamespacedKey key() {
        return key;
    }
}
