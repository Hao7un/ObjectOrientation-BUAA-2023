package expression;

public interface Factor {

    Factor deepClone();

    Expr simplify();

}
