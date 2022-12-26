package pepse.world.properties;

public class Energy {

    private final double factor;
    private final double maxEnergy;

    private double currentEnergy;

    public Energy(double factor, double maxEnergy, double currentEnergy) {
        this.factor = factor;
        this.maxEnergy = maxEnergy;

        this.currentEnergy = currentEnergy;
    }

    public double getCurrentEnergy() {
        return currentEnergy;
    }

    public void increase() {
        if (this.currentEnergy < maxEnergy) {
            this.currentEnergy += factor;
        }
    }

    public void decrease() {
        if (this.currentEnergy > 0) {
            this.currentEnergy -= factor;
        }
    }
}
