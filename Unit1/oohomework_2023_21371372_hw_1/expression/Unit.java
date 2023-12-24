package expression;

import java.math.BigInteger;

public class Unit {
    private BigInteger coef;
    private int xexp;
    private int yexp;
    private int zexp;

    public Unit() {
        this.coef = BigInteger.valueOf(1);
        this.xexp = 0;
        this.yexp = 0;
        this.zexp = 0;
    }

    public Unit(BigInteger coef, String xexp,String yexp, String zexp) {
        this.coef = coef;
        this.xexp = Integer.parseInt(xexp);
        this.yexp = Integer.parseInt(yexp);
        this.zexp = Integer.parseInt(zexp);
    }

    public void addExp(String var) {
        if (var.equals("x")) {
            this.xexp++;
        } else if (var.equals("y")) {
            this.yexp++;
        } else if (var.equals("z")) {
            this.zexp++;
        }
    }

    public void mulCoef(BigInteger num) {
        this.coef  = this.coef.multiply(num);
    }

    public String getXexp() {
        return String.valueOf(this.xexp);
    }

    public String getYexp() {
        return String.valueOf(this.yexp);
    }

    public String getZexp() {
        return String.valueOf(this.zexp);
    }

    public BigInteger getCoef() {
        return this.coef;
    }
}
