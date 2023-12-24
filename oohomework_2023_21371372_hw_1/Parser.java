import expression.Expr;
import expression.Factor;
import expression.Term;
import expression.Var;
import expression.Number;

import java.math.BigInteger;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parseExpr() {
        Expr expr = new Expr();
        expr.addTerm(parseTerm());

        while (lexer.peek().equals("+") || lexer.peek().equals("-")) {
            boolean negative = false;
            if (lexer.peek().equals("+")) {   //读入正号
                lexer.next();
            } else if (lexer.peek().equals("-")) { //读入负号
                negative = true;
                lexer.next();
            }
            Term term = parseTerm();
            if (negative) {
                term.addFactor(new Number(BigInteger.valueOf(-1)));
            }
            expr.addTerm(term);
        }
        return expr;
    }

    public Term parseTerm() {
        Term term = new Term();
        if (lexer.peek().equals('+')) {
            lexer.next();
        } else if (lexer.peek().equals("-")) {
            term.addFactor(new Number(BigInteger.valueOf(-1)));
            lexer.next();
        }
        Factor factor = parseFactor(); //先对factor进行保存
        lexer.next();
        if (lexer.peek().equals("^")) { //遇到乘方
            lexer.next();
            int exponent;
            exponent = Integer.parseInt(lexer.peek());
            if (exponent == 0) {   //次方为0，直接返回1
                term.addFactor(new Number(BigInteger.valueOf(1)));
            } else {
                for (int i = 1; i <= exponent;i++) {
                    term.addFactor(factor);
                }
            }
            lexer.next();
        } else {
            term.addFactor(factor);
        }
        while (lexer.peek().equals("*")) {
            lexer.next();
            Factor factor1 = parseFactor();
            lexer.next();
            if (lexer.peek().equals("^")) { //遇到乘方
                lexer.next();
                int exponent;
                exponent = Integer.parseInt(lexer.peek());
                if (exponent == 0) {   //次方为0，直接返回1
                    term.addFactor(new Number(BigInteger.valueOf(1)));
                } else {
                    for (int i = 1; i <= exponent;i++) {
                        term.addFactor(factor1);
                    }
                }
                lexer.next();
            } else {
                term.addFactor(factor1);
            }
        }
        return term;
    }

    public Factor parseFactor() {
        if (lexer.peek().equals("(")) {      //表达式因子
            lexer.next();
            return exprFactor();
        } else if (lexer.peek().equals("x") ||
                lexer.peek().equals("y") ||
                lexer.peek().equals("z")) {  //变量因子
            return varFactor();
        } else {                             //常数因子
            return numberFactor();
        }
    }

    public Factor exprFactor() {
        Term term = parseTerm();

        Expr expr = new Expr();
        expr.addTerm(term);

        while (lexer.peek().equals("+") || lexer.peek().equals("-")) {
            boolean negative = false;
            if (lexer.peek().equals("+")) {
                lexer.next();
            } else if (lexer.peek().equals("-")) {
                negative = true;
                lexer.next();
            } else {
                break;
            }
            term = parseTerm();
            if (negative) {
                term.addFactor(new Number(BigInteger.valueOf(-1)));
            }
            expr.addTerm(term);
        }
        return expr;
    }

    public Factor numberFactor() {
        if (lexer.peek().equals("+")) {
            lexer.next();
            BigInteger num = new BigInteger(lexer.peek());
            return new Number(num);
        } else if (lexer.peek().equals("-")) {
            lexer.next();
            BigInteger num = new BigInteger((lexer.peek()));
            return new Number(num.negate());
        } else if (Character.isDigit((lexer.peek().charAt(0)))) {
            BigInteger num = new BigInteger(lexer.peek());
            return new Number(num);
        } else { //wrong
            lexer.next();
            return new Number(BigInteger.valueOf(1));
        }
    }

    public Factor varFactor() {
        if ("xyz".indexOf(lexer.peek()) != -1) {
            String var = lexer.peek();
            return new Var(var);
        } else { //wrong format
            return new Var("x");
        }
    }
}