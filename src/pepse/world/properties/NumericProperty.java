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
        this.current = Math.min(this.current + this.factor, this.max);
    }

    public void increase(double number) {
        this.current = Math.min(this.current + number, this.max);
    }

    public void decrease() {
        this.current = Math.max(this.current - this.factor, this.min);
    }

    public void decrease(double number) {
        this.current = Math.max(this.current - number, this.min);
    }
}
