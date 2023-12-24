package expression;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class Term {
    private final HashMap<Factor,Integer> factors;

    public Term() {
        this.factors = new HashMap<>();
    }

    public HashMap<Factor,Integer> getFactors() {
        return this.factors;
    }

    public Integer getExponent(Factor factor) {
        return factors.get(factor);
    }

    public void addFactor(Factor factor,Integer exponent) {
        if (factors.containsKey(factor)) {              //already exists
            int originalexp = factors.get(factor);
            if (originalexp + exponent == 0) {
                factors.remove(factor);
            } else {
                factors.put(factor, originalexp + exponent);
            }
        } else if (exponent != 0) { //create a new one
            factors.put(factor,exponent);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Term that = (Term) o;
        return Objects.equals(factors, that.factors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factors);
    }

    public Term deepClone() {
        Term term = new Term();
        for (Factor factor : this.factors.keySet()) {
            term.addFactor(factor.deepClone(), this.factors.get(factor));
        }
        return term;
    }

    public Expr simplify() { //Term
        Expr expr = new Expr();
        Term term = new Term();
        expr.addTerm(term,BigInteger.valueOf(1)); //create expr=(1)
        for (Factor factor : factors.keySet()) {
            Expr temp = factor.simplify(); //获取化简过的factor
            int exp = factors.get(factor);
            for (int i = 1; i <= exp; i++) {
                expr = expr.multiply(temp);
            }
        }
        return expr;
    }

    public Expr diff(String var) {   //base ^ exp
        Expr result = new Expr();
        for (Factor factor : factors.keySet()) { // d(f*g) = df*g+f*dg
            Term others = this.deepClone();
            others.factors.remove(factor); //remove one factor, multiply the rest
            Expr diffFactor = factor.diff(var); // this is (expr)'
            int k = factors.get(factor);        //this is k
            Expr coef = new Expr();
            coef.addTerm(new Term(),new BigInteger(String.valueOf(k))); //create (k)
            Expr expr1 = new Expr();
            expr1 = coef.multiply(diffFactor);
            for (int i = 1; i <= k - 1; i++) {
                Expr hold = new Expr();
                Term hold1 = new Term();
                hold1.addFactor(factor,1);
                hold.addTerm(hold1,BigInteger.valueOf(1));
                expr1 = expr1.multiply(hold);
            }
            for (Factor factor1: others.factors.keySet()) {
                Expr hold = new Expr();
                Term hold1 = new Term();
                hold1.addFactor(factor1,1);
                hold.addTerm(hold1,BigInteger.ONE);
                int exp = factors.get(factor1);
                for (int i = 1; i <= exp; i++) {
                    expr1 = expr1.multiply(hold);
                }
            }
            result = result.add(expr1);
        }
        return result;
    }

    public Term multiply(Term that) {  //term*term ->term
        Term result = this.deepClone();
        for (Factor factor:that.factors.keySet()) {
            result.addFactor(factor.deepClone(),that.getExponent(factor));
        }
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        for (Factor factor:factors.keySet()) {
            if (flag) { //starts from the second factor
                sb.append("*");
            }
            flag = true;
            sb.append(factor);
            if (factors.get(factor) == 2 &&
                    factor instanceof Pow) {
                switch (((Pow) factor).getPow()) {
                    case "x":
                        sb.append("*x");
                        break;
                    case "y":
                        sb.append("*y");
                        break;
                    case "z":
                        sb.append("*z");
                        break;
                    default:
                        break;
                }
            } else if (factors.get(factor) >= 2) {
                sb.append("**");
                sb.append(factors.get(factor));
            }
        }
        return sb.toString();
    }
}


