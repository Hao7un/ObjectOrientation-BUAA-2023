package expression;

public class Var implements Factor {
    private final String var;

    public Var(String c) {
        this.var = c;
    }

    public String getVar() {
        return var;
    }
}
