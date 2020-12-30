package me.wolfyscript.utilities.util;


import org.bukkit.util.Vector;

import java.util.Random;

public class RandomUtils {

    public static final Random random = new Random(System.currentTimeMillis());

    public static Vector getRandomVector() {
        double x = random.nextDouble();
        double y = random.nextDouble();
        double z = random.nextDouble();
        return (new Vector(x, y, z)).normalize();
    }

    public static Vector getRandomCircleVector() {
        double rnd = random.nextDouble() * 2.0D * Math.PI;
        double x = Math.cos(rnd);
        double z = Math.sin(rnd);
        return new Vector(x, 0.0D, z);
    }
}
