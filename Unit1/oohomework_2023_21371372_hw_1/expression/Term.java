package expression;

import java.util.ArrayList;

public class Term {
    private final ArrayList<Factor> factors;

    public Term() { this.factors = new ArrayList<>(); }

    public ArrayList<Factor> getFactors() {
        return this.factors;
    }

    public void addFactor(Factor factor) {
        this.factors.add(factor);
    }

}
