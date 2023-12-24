package expression;

import java.math.BigInteger;
import java.util.Objects;

public class Number implements Factor {
    private final BigInteger num;

    public Number(BigInteger num) {
        this.num = num;
    }

    public String getNumber() {
        return this.num.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Number that = (Number) o;
        return Objects.equals(num, that.num);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num);
    }

    public String toString() {
        return this.num.toString();
    }

    public Number deepClone() {
        return new Number(this.num);
    }

    public BigInteger getNum() {
        return num;
    }

    @Override
    public Expr simplify() { //create (3)
        Expr expr = new Expr();
        Term term = new Term();
        expr.addTerm(term,this.num);
        return expr;
    }

    public Expr diff(String var) {
        Expr expr = new Expr();
        Term term = new Term();
        expr.addTerm(term,BigInteger.ZERO);
        return expr;
    }
}