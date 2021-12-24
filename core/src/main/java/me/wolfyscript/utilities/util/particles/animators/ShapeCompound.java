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

package me.wolfyscript.utilities.util.particles.animators;

import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ShapeCompound extends Shape {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("complex/compound");

    private final List<Shape> shapes;

    public ShapeCompound() {
        this(new Shape[0]);
    }

    public ShapeCompound(@NotNull Shape... shapes) {
        super(KEY);
        this.shapes = Arrays.asList(shapes != null ? shapes : new Shape[0]);
    }

    @Override
    public void drawVectors(double time, Consumer<Vector> drawVector) {
        shapes.forEach(shape -> shape.drawVectors(time, drawVector));
    }
}
