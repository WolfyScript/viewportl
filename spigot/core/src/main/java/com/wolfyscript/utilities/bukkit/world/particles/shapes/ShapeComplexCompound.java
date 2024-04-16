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

package com.wolfyscript.utilities.bukkit.world.particles.shapes;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A complex shape that combines multiple shapes together.<br>
 * There is no longer limit of how many shapes it can contain.<br>
 * However, there is a lower limit of at least 2 shapes, to discourage the use of this shape for single shapes! Single shapes should be used separately!
 */
public class ShapeComplexCompound extends Shape {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("complex/compound");

    private final List<Shape> shapes;

    /**
     * Only used for Jackson deserialization.
     */
    ShapeComplexCompound() {
        this(new ShapeCircle(), new ShapeCircle());
    }

    public ShapeComplexCompound(@NotNull Shape... shapes) {
        super(KEY);
        Preconditions.checkArgument(shapes != null && shapes.length > 1, "Shapes array cannot be null and must contain at least 2 shapes! (For single shapes use the shape directly!)");
        this.shapes = Arrays.asList(shapes);
    }

    @Override
    public void drawVectors(double time, Consumer<Vector> drawVector) {
        shapes.forEach(shape -> shape.drawVectors(time, drawVector));
    }
}
