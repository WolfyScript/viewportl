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

package com.wolfyscript.utilities.common.gui;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public interface BranchComponent {

    /**
     * The children of this Component; or an empty Set if there are no children.
     *
     * @return The child Components of this Component.
     */
    Set<? extends Component> childComponents();

    /**
     * Gets the child at the relative path from this Component.<br>
     * When the path is null or empty then it returns this Component instead.
     *
     * @param path The path to the child Component.
     * @return The child at the specified path; or this Component when the path is null or empty.
     */
    default Optional<? extends Component> getChild(String... path) {
        if (path == null || path.length == 0) return Optional.empty();
        return getChild(path[0]).flatMap(component -> {
            if (component instanceof BranchComponent branchComponent) {
                return branchComponent.getChild(Arrays.copyOfRange(path, 1, path.length));
            }
            return Optional.empty();
        });
    }

    /**
     * Gets the direct child Component, or an empty Optional when it wasn't found.
     *
     * @param id The id of the child Component.
     * @return The child Component; or empty Component.
     */
    Optional<? extends Component> getChild(String id);

}
