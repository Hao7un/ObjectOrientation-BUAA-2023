import expression.Expr;
import expression.Term;
import expression.Factor;
import expression.Pow;
import expression.Trigono;
import expression.Number;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parseExpr() {
        Expr expr = new Expr();
        if (lexer.peek().equals("-")) {
            lexer.next();
            expr.addTerm(parseTerm(),BigInteger.valueOf(-1));
        } else if (lexer.peek().equals("+")) {
            lexer.next();
            expr.addTerm(parseTerm(),BigInteger.valueOf(1));
        } else {
            expr.addTerm(parseTerm(),BigInteger.valueOf(1));
        }

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
                expr.addTerm(term,BigInteger.valueOf(-1));
            } else {
                expr.addTerm(term,BigInteger.valueOf(1));
            }
        }
        return expr;
    }

    public Term parseTerm() {
        Term term = new Term();
        Factor factor = parseFactor(); //先对factor进行保存
        lexer.next();
        int exp = 1;
        if (lexer.peek().equals("^")) { //遇到乘方
            lexer.next();
            exp = Integer.parseInt(lexer.peek());
            lexer.next();
        }
        term.addFactor(factor,exp);
        while (lexer.peek().equals("*")) {
            lexer.next();
            Factor factor1 = parseFactor();
            lexer.next();
            int exp1 = 1;
            if (lexer.peek().equals("^")) { //遇到乘方
                lexer.next();
                exp1 = Integer.parseInt(lexer.peek());
                lexer.next();
            }
            term.addFactor(factor1,exp1);
        }
        return term;
    }

    public Factor parseFactor() {
        if (lexer.peek().equals("(")) {      //表达式因子
            lexer.next();
            return parseExpr();
        } else if (lexer.peek().equals("x") ||
                lexer.peek().equals("y") ||
                lexer.peek().equals("z")) {  //变量因子
            return parsePow();
        } else if (lexer.peek().equals("sin") ||  //三角函数因子
                lexer.peek().equals("cos")) {
            return parseTri();
        } else if ("fgh".indexOf(lexer.peek().charAt(0)) != -1) {
            return parseFunction();
        } else {                             //常数因子
            return parseNumber();
        }
    }

    public Factor parseNumber() {
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
        } else {
            lexer.next();
            return new Number(BigInteger.valueOf(1));
        }
    }

    public Factor parsePow() {
        if ("xyz".indexOf(lexer.peek()) != -1) {
            String pow = lexer.peek();
            return new Pow(pow);
        } else { //wrong format
            return new Pow("x");
        }
    }

    public Trigono parseTri() {
        String name = lexer.peek();
        lexer.next(); // pass (
        lexer.next();
        Trigono function = new Trigono(name,parseExpr());
        return function;
    }

    public Expr parseFunction() { // parse the custom function
        String function = lexer.peek(); //current function e.g. f(1,sin(x))
        HashMap<Character,String> functions = MainClass.getFunctions(); //get the functions hashmap
        String originalFunction = functions.get(function.charAt(0));
        // originalFunction: f(x,y)=x+y
        String name = originalFunction.split("=")[0]; //f(x,y)
        String funcExpr = originalFunction.split("=")[1]; //x+y
        ArrayList<String> originalParam = getParam(name); //[x,y]
        ArrayList<String> currentParam = getParam(function); //[1,sin(x)]
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < funcExpr.length(); i++) {
            if (funcExpr.charAt(i) == 'x' ||   // change
                    funcExpr.charAt(i) == 'y' ||
                    funcExpr.charAt(i) == 'z') {
                int index = originalParam.indexOf(Character.toString(funcExpr.charAt(i)));
                sb.append("(" + currentParam.get(index) + ")");
            } else {
                sb.append(funcExpr.charAt(i));
            }
        }
        return new Parser(new Lexer(sb.toString())).parseExpr();
    }

    private ArrayList<String> getParam(String function) {
        int temp = function.indexOf('(') + 1;
        int cnt = 1;
        ArrayList<String> parameter = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (;cnt > 0;temp++) {
            if (function.charAt(temp) == ')') {
                if (cnt != 1) {
                    sb.append(')');
                }
                cnt--;
            } else if (function.charAt(temp) == '(') {
                sb.append('(');
                cnt++;
            } else if (function.charAt(temp) == ','
                    && cnt == 1) {
                parameter.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(function.charAt(temp));
            }
        }
        parameter.add(sb.toString());
        return parameter;
    }
}
