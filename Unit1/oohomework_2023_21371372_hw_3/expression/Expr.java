package expression;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class Expr implements Factor {
    private final HashMap<Term, BigInteger> terms;

    public Expr() {
        this.terms = new HashMap<>();
    }

    public HashMap<Term,BigInteger> getTerms() {
        return this.terms;
    }

    public BigInteger getCoef(Term term) {
        return terms.get(term);
    }

    public void addTerm(Term term,BigInteger coef) {
        if (terms.containsKey(term)) {              //already exists
            BigInteger originalcoef = terms.get(term);
            if (originalcoef.add(coef).equals(BigInteger.ZERO)) {
                terms.remove(term);
            } else {
                terms.put(term,coef.add(originalcoef));
            }
        } else if (!coef.equals(BigInteger.ZERO)) { //create a new one
            terms.put(term,coef);
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
        Expr that = (Expr) o;
        return Objects.equals(terms, that.terms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terms);
    }

    public Expr deepClone() {
        Expr expr = new Expr();
        for (Term term : this.terms.keySet()) {
            expr.addTerm(term.deepClone(), this.terms.get(term));
        }
        return expr;
    }

    public Expr simplify() { //Expr
        Expr expr = new Expr();
        for (Term term : terms.keySet()) {
            Expr temp = term.simplify();
            Expr coef = new Expr();
            coef.addTerm(new Term(),terms.get(term));
            expr = expr.add(temp.multiply(coef));
        }
        return expr;
    }

    public Expr add(Expr expr) {  //expr+expr -->expr
        Expr result = this.deepClone();
        for (Term term:expr.terms.keySet()) {
            result.addTerm(term.deepClone(),expr.terms.get(term));
        }
        return result;
    }

    public Expr multiply(Expr that) { //expr*expr->expr
        Expr result = new Expr();
        for (Term term1:this.terms.keySet()) {
            for (Term term2:that.terms.keySet()) {
                Term newterm = term1.multiply(term2);
                BigInteger coef = this.terms.get(term1).multiply(
                        that.terms.get(term2));
                Expr temp = new Expr();
                temp.addTerm(newterm,coef);
                result = result.add(temp);
            }
        }
        return result;
    }

    public Expr diff(String var) { //Expr diff
        Expr result = new Expr();
        for (Term term : terms.keySet()) {    //for each term coef*term
            Expr coef = new Expr();
            coef.addTerm(new Term(),terms.get(term));
            Expr temp = term.diff(var);
            temp = temp.simplify();
            result = result.add(temp.multiply(coef));
        }
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Term term:terms.keySet()) {
            BigInteger coef = terms.get(term);
            if (coef.compareTo(BigInteger.valueOf(0)) > 0) { //positive
                if (coef.compareTo(BigInteger.valueOf(1)) == 0) { //coef == 1
                    sb.append("+");
                    if (term.getFactors().size() != 0) {
                        sb.append(term);
                    }
                    else {
                        sb.append(1);
                    }
                } else {
                    sb.append("+");
                    sb.append(coef);
                    if (term.getFactors().size() != 0) {
                        sb.append("*");
                        sb.append(term);
                    }
                }
            } else if (coef.compareTo(BigInteger.valueOf(0)) < 0) { ///negative
                if (coef.compareTo(BigInteger.valueOf(-1)) == 0) { //coef == -1
                    sb.append("-");
                    if (term.getFactors().size() != 0) {
                        sb.append(term);
                    }
                    else {
                        sb.append(1);
                    }
                } else {
                    sb.append(coef);
                    if (term.getFactors().size() != 0) {
                        sb.append("*");
                        sb.append(term);
                    }
                }
            }
        }
        String str = sb.toString();
        if (str.equals("")) {
            return "0";
        }
        if (str.charAt(0) == '+') {
            str = str.substring(1);
        }
        str = str.replaceAll("sin\\(x\\*x\\)","sin\\(x\\*\\*2\\)");  //x**2 is var,  but x*x is expr
        str = str.replaceAll("cos\\(x\\*x\\)","cos\\(x\\*\\*2\\)");
        str = str.replaceAll("sin\\(y\\*y\\)","sin\\(y\\*\\*2\\)");
        str = str.replaceAll("cos\\(y\\*y\\)","cos\\(y\\*\\*2\\)");
        str = str.replaceAll("sin\\(z\\*z\\)","sin\\(z\\*\\*2\\)");
        str = str.replaceAll("cos\\(z\\*z\\)","cos\\(z\\*\\*2\\)");
        return str;
    }
}
