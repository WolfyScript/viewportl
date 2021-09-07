package me.wolfyscript.utilities.util.particles.timer;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Provides a linear increasing value by a specified increment.<br>
 */
public class TimeSupplierRandom extends TimeSupplier {

    private final long seed;
    private final double multiplier;

    public TimeSupplierRandom() {
        this(1, 1, 1);
    }

    public TimeSupplierRandom(long seed, double multiplier, double stopValue) {
        super(NamespacedKey.wolfyutilties("linear"));
        this.seed = seed;
        this.multiplier = multiplier;
        setStopValue(stopValue);
    }

    public long getSeed() {
        return seed;
    }

    @Override
    public void setStopValue(double stopValue) {
        Preconditions.checkArgument(stopValue >= 0, "Random time supplier cannot have negative stop value!");
        super.setStopValue(stopValue);
    }

    @Override
    public TimeSupplier.Runner createRunner() {
        return new Runner();
    }

    private class Runner extends TimeSupplier.Runner {

        private final Random random;
        private int counter = 0;

        private Runner() {
            this.random = seed != -1 ? new Random(seed) : new Random();
        }

        @Override
        public double increase() {
            time += random.nextDouble() * multiplier;
            counter++;
            return time;
        }

        @Override
        public boolean shouldStop() {
            return counter >= getStopValue();
        }
    }
}
