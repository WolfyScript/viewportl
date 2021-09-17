package me.wolfyscript.utilities.util.particles.timer;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.NamespacedKey;

/**
 * Provides a linear increasing value by a specified increment.<br>
 */
public class TimerLinear extends Timer {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("linear");

    private double increment;

    public TimerLinear() {
        this(1, 1);
    }

    public TimerLinear(double increment, double stopValue) {
        super(KEY);
        setIncrement(increment);
        setStopValue(stopValue);
    }

    public double getIncrement() {
        return increment;
    }

    public void setIncrement(double increment) {
        Preconditions.checkArgument(increment != 0, "Cannot divide by 0! Must be less or bigger than 0!");
        this.increment = increment;
    }

    @Override
    public Timer.Runner createRunner() {
        return new Runner();
    }

    private class Runner extends Timer.Runner {

        @Override
        public double increase() {
            time += increment;
            return time;
        }

        @Override
        public boolean shouldStop() {
            if (increment > 0) {
                return time > getStopValue();
            }
            return time < getStopValue();
        }
    }
}
