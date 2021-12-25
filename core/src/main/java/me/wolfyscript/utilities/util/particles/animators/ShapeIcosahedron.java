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

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.math.MathUtil;
import me.wolfyscript.utilities.util.math.Triangle;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;

public class ShapeIcosahedron extends Shape {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("icosahedron");
    public static final double X = 0.525731112119133606f;
    public static final double Z = 0.850650808352039932f;
    /**
     * Vertices of the triangles
     */
    public static final double[][] V_DATA = {{-X, +0, +Z}, {+X, +0, +Z}, {-X, +0, -Z}, {+X, +0, -Z}, {+0, +Z, +X}, {+0, +Z, -X},
            {+0, -Z, +X}, {+0, -Z, -X}, {+Z, +X, +0}, {-Z, +X, +0}, {+Z, -X, +0}, {-Z, -X, +0}};
    public static final int[][] TINDX = {{0, 4, 1}, {0, 9, 4}, {9, 5, 4}, {4, 5, 8}, {4, 8, 1}, {8, 10, 1}, {8, 3, 10},
            {5, 3, 8}, {5, 2, 3}, {2, 7, 3}, {7, 10, 3}, {7, 6, 10}, {7, 11, 6}, {11, 0, 6}, {0, 1, 6}, {6, 1, 10},
            {9, 0, 11}, {9, 11, 2}, {9, 2, 5}, {7, 2, 11}};

    private final double radius;
    private final double particleIncrease;
    private final int depth;
    @JsonIgnore
    private final List<Triangle> triangles = new ArrayList<>();
    /**
     * The cached vectors of the specified settings (So we don't recalculate them each run).
     */
    @JsonIgnore
    private final List<Vector> vectors = new ArrayList<>();

    public ShapeIcosahedron() {
        this(1, 0, 6);
    }

    @JsonCreator
    public ShapeIcosahedron(@JsonProperty("radius") double radius, @JsonProperty("depth") int depth, @JsonProperty("particleIncrease") double particleIncrease) {
        super(KEY);
        Preconditions.checkArgument(particleIncrease > 0, "Particle increase must be higher than 0!");
        Preconditions.checkArgument(depth >= 0, "Depth must be higher than or equal 0!");
        this.radius = radius;
        this.depth = depth;
        this.particleIncrease = particleIncrease;

        calcIcosahedron(this.depth, this.radius);
    }

    @Override
    public void drawVectors(double time, Consumer<Vector> drawVector) {
        vectors.forEach(vector -> drawVector.accept(vector.clone()));
    }

    private void calcIcosahedron(int depth, double radius) {
        for (int[] tindx : TINDX) {
            subdivide(V_DATA[tindx[0]], V_DATA[tindx[1]], V_DATA[tindx[2]], depth, radius);
        }
        // Cache vectors
        for (Triangle triangle : triangles) {
            vectors.addAll(MathUtil.getLineVectors(triangle.getPoint1(), triangle.getPoint2(), particleIncrease));
            vectors.addAll(MathUtil.getLineVectors(triangle.getPoint3(), triangle.getPoint2(), particleIncrease));
        }
    }

    private void calcTriangle(double[] vA0, double[] vB1, double[] vC2, double radius) {
        Triangle triangle = new Triangle(
                new Vector(vA0[0], vA0[1],vA0[2]).multiply(radius),
                new Vector(vB1[0], vB1[1],vB1[2]).multiply(radius),
                new Vector(vC2[0], vC2[1],vC2[2]).multiply(radius));
        triangles.add(triangle);
    }

    private void subdivide(double[] vA0, double[] vB1, double[] vC2, int depth, double radius) {
        double[] vAB = new double[3];
        double[] vBC = new double[3];
        double[] vCA = new double[3];

        int i;

        if (depth == 0) {
            calcTriangle(vA0, vB1, vC2, radius);
            return;
        }

        for (i = 0; i < 3; i++) {
            vAB[i] = (vA0[i] + vB1[i]) / 2;
            vBC[i] = (vB1[i] + vC2[i]) / 2;
            vCA[i] = (vC2[i] + vA0[i]) / 2;
        }

        double modAB = mod(vAB);
        double modBC = mod(vBC);
        double modCA = mod(vCA);

        for (i = 0; i < 3; i++) {
            vAB[i] /= modAB;
            vBC[i] /= modBC;
            vCA[i] /= modCA;
        }
        subdivide(vA0, vAB, vCA, depth - 1, radius);
        subdivide(vB1, vBC, vAB, depth - 1, radius);
        subdivide(vC2, vCA, vBC, depth - 1, radius);
        subdivide(vAB, vBC, vCA, depth - 1, radius);
    }

    private static double mod(double[] v) {
        return Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }

}
