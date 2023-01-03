package pepse.world.properties;

/**
 * A class that represents a numeric property with current value, min, max and a change factor
 */
public class NumericProperty implements Property<Double> {

    private final double factor;

    private final double max;
    private final double min;

    private double current;

    public NumericProperty(double factor, double max, double min, double current) {
        this.factor = factor;

        this.max = max;
        this.min = min;

        this.current = current;
    }

    @Override
    public Double getValue() {
        return this.current;
    }

    public double getMaxValue() {
        return this.max;
    }

    public void increase() {
        if (this.current + factor <= max) {
            this.current += factor;
        }
    }

    public void decrease() {
        if (this.current - factor >= min) {
            this.current -= factor;
        }
    }
}
