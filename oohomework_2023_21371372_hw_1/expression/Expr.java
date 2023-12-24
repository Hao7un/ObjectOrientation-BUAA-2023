package expression;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Expr implements Factor {
    private final ArrayList<Term> terms;

    public Expr() {
        this.terms = new ArrayList<>();
    }

    public void addTerm(Term term) {
        this.terms.add(term);
    }

    public static HashMap<String, BigInteger> merge(Expr expr) {
        //构建HashMap，<系数，xyz的次数> e.g. 5*x^3 y^8---><380,5>
        HashMap<String,BigInteger> expression = new HashMap<String, BigInteger>();
        for (Term term1: expr.terms) { //对expr中每个term进行遍历
            ArrayList<Unit> units = new ArrayList<Unit>();
            Unit init = new Unit();  //每一个项创造一个新的units
            units.add(init);
            for (Factor factor1: term1.getFactors()) { //对每个factor进行遍历
                //此时factor可能有：Var,Number,Expr
                if (factor1 instanceof Var) { //如果是var,直接对units里每一个都乘上即可
                    for (Unit item : units) {
                        item.addExp(((Var) factor1).getVar());
                    }
                } else if (factor1 instanceof Number) { //如果是number，直接对units里每一个系数进行改变
                    for (Unit item: units) {
                        BigInteger num = new BigInteger(((Number) factor1).getNumber());
                        item.mulCoef(num);
                    }
                } else if (factor1 instanceof  Expr) { //是表达式因子
                    ArrayList<Unit> newunits = new ArrayList<Unit>();
                    for (Unit item : units) {  //对于原本中的每一个unit
                        for (Term term2 : ((Expr) factor1).terms) { //对因子中每一项
                            //这里要用深拷贝
                            Unit newitem = new Unit(item.getCoef(),
                                    item.getXexp(),
                                    item.getYexp(),
                                    item.getZexp());
                            for (Factor factor2 : term2.getFactors()) {
                                if (factor2 instanceof Var) {
                                    newitem.addExp(((Var) factor2).getVar());
                                } else if (factor2 instanceof Number) {
                                    BigInteger num = new BigInteger(((Number) factor2).getNumber());
                                    newitem.mulCoef(num);
                                }
                            }
                            newunits.add(newitem);
                        }
                    }
                    units = newunits; //更新units
                }
            }
            //至此，已经完全拆开一个term，并将unit存在units中，开始存入到map当中
            for (Unit unit:units) {
                String key = unit.getXexp() + " " + unit.getYexp() + " " + unit.getZexp();
                BigInteger value = unit.getCoef();
                if (expression.containsKey(key)) { //已经存在key
                    expression.put(key,expression.get(key).add(value));
                } else {  //未存在key,创建新的
                    expression.put(key,value);
                }
            }
        }
        return expression;
    }

    public static String print(HashMap<String, BigInteger> answer) {
        StringBuilder sb = new StringBuilder();
        for (String exp :answer.keySet()) {
            BigInteger coef = answer.get(exp);
            if (coef.compareTo(BigInteger.valueOf(0)) == 0) {
                continue;
            } else {  //系数不为0
                String [] explist = exp.split("\\s+");
                int xexp = Integer.parseInt(explist[0]);
                int yexp = Integer.parseInt(explist[1]);
                int zexp = Integer.parseInt(explist[2]);
                if (xexp == 0 && yexp == 0 && zexp == 0 &&
                        coef.compareTo(BigInteger.valueOf(0)) != 0) {  //常数项且不为0
                    if (coef.compareTo(BigInteger.valueOf(0)) > 0) { sb.append("+"); }
                    sb.append(coef);
                } else if (coef.compareTo(BigInteger.valueOf(1)) == 0
                        || coef.compareTo(BigInteger.valueOf(-1)) == 0) { //系数为1/-1，且不是常数项
                    if (coef.compareTo(BigInteger.valueOf(0)) > 0) { sb.append("+"); }
                    else { sb.append("-"); }
                    int flag = 0;
                    if (xexp == 1) {
                        sb.append("x");
                        flag = 1;
                    } else if (xexp > 1) {
                        sb.append("x**");
                        sb.append(xexp);
                        flag = 1;
                    }
                    if (yexp == 1) {
                        if (flag == 1) { sb.append("*y"); }
                        else {
                            sb.append("y");
                            flag = 1;
                        }
                    } else if (yexp > 1) {
                        if (flag == 1) { sb.append("*y**"); }
                        else {
                            sb.append("y**");
                            flag = 1;
                        }
                        sb.append(yexp);
                    }
                    if (zexp == 1) {
                        if (flag == 1) { sb.append("*z"); }
                        else {
                            sb.append("z");
                        }
                    } else if (zexp > 1) {
                        if (flag == 1) { sb.append("*z**"); }
                        else { sb.append("z**"); }
                        sb.append(zexp);
                    }
                } else { sb = print2(sb,coef,xexp,yexp,zexp); }
            }
        }
        String output = sb.toString();
        if (output.equals("")) { output = String.valueOf(0); }
        return output;
    }

    public static StringBuilder print2(StringBuilder sb, BigInteger coef,
                                       int xexp, int yexp, int zexp) {
        if (coef.compareTo(BigInteger.valueOf(0)) > 0) { sb.append("+"); }
        sb.append(coef);
        if (xexp == 1) { sb.append("*x"); }
        else if (xexp > 1) {
            sb.append("*x**");
            sb.append(xexp);
        }
        if (yexp == 1) { sb.append("*y"); }
        else if (yexp > 1) {
            sb.append("*y**");
            sb.append(yexp);
        }
        if (zexp == 1) { sb.append("*z"); }
        else if (zexp > 1) {
            sb.append("*z**");
            sb.append(zexp);
        }
        return sb;
    }
}

