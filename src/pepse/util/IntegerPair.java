package pepse.util;

import java.util.Objects;

public class IntegerPair {

    private int a;
    private int b;

    public IntegerPair(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return this.a;
    }

    public int getB() {
        return this.b;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IntegerPair)) {
            return false;
        }

        IntegerPair other = (IntegerPair) object;

        return other.getA() == this.getA() && other.getB() == this.getB();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.a, this.b);
    }
}
