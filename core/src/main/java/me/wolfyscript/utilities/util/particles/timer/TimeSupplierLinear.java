package me.wolfyscript.utilities.util.particles.timer;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.NamespacedKey;

/**
 * Provides a linear increasing value by a specified increment.<br>
 */
public class TimeSupplierLinear extends TimeSupplier {

    private double increment;

    public TimeSupplierLinear() {
        this(1, 1);
    }

    public TimeSupplierLinear(double increment, double stopValue) {
        super(NamespacedKey.wolfyutilties("linear"));
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
    public TimeSupplier.Runner createRunner() {
        return new Runner();
    }

    private class Runner extends TimeSupplier.Runner {

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
