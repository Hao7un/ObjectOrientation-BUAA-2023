package expression;

import java.math.BigInteger;
import java.util.Objects;

public class Pow implements Factor {
    private final String pow;

    public Pow(String c) {
        this.pow = c;
    }

    public String getPow() {
        return pow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pow that = (Pow) o;
        return Objects.equals(pow, that.pow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pow);
    }

    public String toString() {
        return this.pow;
    }

    public Pow deepClone() {
        return new Pow(this.pow);
    }

    @Override
    public Expr simplify() { //create (x)/ (y)
        Expr expr = new Expr();
        Term term = new Term();
        Pow temp = new Pow(this.pow);
        term.addFactor(temp,1);
        expr.addTerm(term,BigInteger.valueOf(1));
        return expr;
    }

}

