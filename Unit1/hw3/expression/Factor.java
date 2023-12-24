package expression;

public interface Factor {

    Factor deepClone();

    Expr simplify();

    Expr diff(String var);

}
