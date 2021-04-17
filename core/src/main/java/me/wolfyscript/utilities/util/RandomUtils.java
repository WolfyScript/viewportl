package me.wolfyscript.utilities.util;


import org.bukkit.util.Vector;

import java.util.Random;

public class RandomUtils {

    private RandomUtils() {
    }

    public static final Random random = new Random(System.currentTimeMillis());

    public static Vector getRandomVector() {
        double x = random.nextDouble() * 2 - 1;
        double y = random.nextDouble() * 2 - 1;
        double z = random.nextDouble() * 2 - 1;
        return (new Vector(x, y, z)).normalize();
    }

    public static Vector getRandomCircleVector() {
        double rnd = random.nextDouble() * 2.0D * Math.PI;
        double x = Math.cos(rnd);
        double z = Math.sin(rnd);
        return new Vector(x, 0.0D, z);
    }

    public static double nextDouble(double origin, double bound) {
        double r = random.nextDouble();
        r = r * (bound - origin) + origin;
        if (r >= bound) // correct for rounding
            r = Math.nextDown(bound);
        return r;
    }

    public static long nextLong(long origin, long bound) {
        long r = random.nextLong();
        if (origin < bound) {
            long n = bound - origin, m = n - 1;
            if ((n & m) == 0L)  // power of two
                r = (r & m) + origin;
            else if (n > 0L) {  // reject over-represented candidates
                for (long u = r >>> 1;            // ensure nonnegative
                     u + m - (r = u % n) < 0L;    // rejection check
                     u = random.nextLong() >>> 1) // retry
                    ;
                r += origin;
            } else {              // range not representable as long
                while (r < origin || r >= bound)
                    r = random.nextLong();
            }
        }
        return r;
    }
}
