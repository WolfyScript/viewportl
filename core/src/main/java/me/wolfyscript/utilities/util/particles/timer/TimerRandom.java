package me.wolfyscript.utilities.util.particles.timer;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Random;

/**
 * Provides a linear increasing value by a specified increment.<br>
 */
public class TimerRandom extends Timer {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("random");

    private final long seed;
    private final double multiplier;

    public TimerRandom() {
        this(1, 1, 1);
    }

    public TimerRandom(long seed, double multiplier, double stopValue) {
        super(KEY);
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
    public Timer.Runner createRunner() {
        return new Runner();
    }

    private class Runner extends Timer.Runner {

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
