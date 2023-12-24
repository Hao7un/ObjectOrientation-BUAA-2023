package expression;

import java.math.BigInteger;
import java.util.Objects;

public class Trigono implements Factor {
    private final String name;
    private final Expr expr;

    public Trigono(String name,Expr expr) {
        this.name = name;
        this.expr = expr;
    }

    public String getName() {
        return this.name;
    }

    public Expr getExpr() {
        return this.expr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trigono that = (Trigono) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(expr, that.expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, expr);
    }

    public Trigono deepClone() {
        return new Trigono(name, expr.deepClone());
    }

    @Override
    public Expr simplify() {  // create (cos(...))
        Expr expr = new Expr();
        Term term = new Term();
        Expr expr1 = this.expr.simplify();
        // coef
        BigInteger coef = new BigInteger("0");
        if (expr1.getTerms().size() != 0) { //coef is not 0
            coef = expr1.getCoef(
                    (Term) expr1.getTerms().keySet().toArray()[0]);
        } else {
            coef = new BigInteger("0");
        }

        //优化 cos(0),sin(0)
        if (name.equals("sin") && expr1.simplify().toString().equals("0")) {
            expr.addTerm(term,BigInteger.valueOf(0));
        } else if (name.equals("cos") && expr1.simplify().toString().equals("0")) {
            expr.addTerm(term,BigInteger.valueOf(1));
        } else if (coef.compareTo(BigInteger.valueOf(0)) < 0) {
            if (name.equals("sin")) { //sin((-x))=-sin(x)
                Expr exprneg = new Expr();
                exprneg.addTerm(new Term(),BigInteger.valueOf(-1));
                Trigono temp = new Trigono(name,expr1.multiply(exprneg));
                term.addFactor(temp,1);
                expr.addTerm(term,BigInteger.valueOf(-1));
            } else { //cos((-x)) = cos(x)
                Expr exprneg = new Expr();
                exprneg.addTerm(new Term(),BigInteger.valueOf(-1));
                Trigono temp = new Trigono(name,expr1.multiply(exprneg));
                term.addFactor(temp,1);
                expr.addTerm(term,BigInteger.valueOf(1));
            }
        } else {
            term.addFactor(new Trigono(name,expr1.simplify()),1);
            expr.addTerm(term,BigInteger.valueOf(1));
        }
        return expr;
    }

    public String toString() {
        //only one bracket: sin(x),sin(x**2),sin(3),sin(cos(...)) pow\num\tri
        if (expr.getTerms().size() == 1) {
            Term term = (Term) expr.getTerms().keySet().toArray()[0]; //get the first term
            BigInteger coef = expr.getCoef(term);//bug fix:coef==1 sin(5*cos(y))
            if (term.getFactors().size() == 1 &&  coef.equals(BigInteger.valueOf(1))) {
                Factor factor = (Factor) term.getFactors().keySet().toArray()[0];
                if (factor instanceof Pow && coef.equals(BigInteger.valueOf(1)) ||
                        factor instanceof Number ||
                        factor instanceof Trigono) {
                    return name + "(" + expr + ")";
                }
            } else if (term.getFactors().size() == 0) {
                return name + "(" + expr + ")";
            }
        }
        return name + "((" + expr + "))";
    }
}
