package expression;

public class Derivation implements Factor {

    private final String var;

    private final Expr expr;

    public Derivation(String var, Expr expr) {
        this.var = var;
        this.expr = expr;
    }

    public String getVar() {
        return this.var;
    }

    public Expr getExpr() {
        return this.expr;
    }

    @Override
    public Derivation deepClone() {
        return new Derivation(var, expr.deepClone());
    }

    @Override
    public Expr simplify() {
        Expr expr = this.expr;
        expr = expr.simplify();
        return expr;
    }

    @Override
    public Expr diff(String var) {
        return null;
    }

    public String toString() { //return the expression after derivation
        String s = new String();
        s = this.expr.toString();
        return s;
    }
}
