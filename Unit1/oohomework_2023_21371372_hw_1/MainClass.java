import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;
import expression.Expr;

public class MainClass
{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        //Pre-process
        input = preprocess(input);
        // Process
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Expr expr = parser.parseExpr();
        HashMap<String, BigInteger> poly =  Expr.merge(expr);
        String output = Expr.print(poly);
        if (output.charAt(0) == '+') {
            output = output.substring(1,output.length());
        }
        System.out.println(output);
    }

    private static String preprocess(String input) {
        String s = input;
        s = s.replaceAll(" ","");
        s = s.replaceAll("\t","");
        s = s.replaceAll("\\+\\+\\+","+");
        s = s.replaceAll("---","-");
        s = s.replaceAll("\\+\\+-","-");
        s = s.replaceAll("\\+-\\+","-");
        s = s.replaceAll("-\\+\\+","-");
        s = s.replaceAll("--\\+","\\+");
        s = s.replaceAll("-\\+-","\\+");
        s = s.replaceAll("\\+--","\\+");
        s = s.replaceAll("\\+\\+","\\+");
        s = s.replaceAll("--","\\+");
        s = s.replaceAll("\\+-","-");
        s = s.replaceAll("-\\+","-");
        s = s.replaceAll("\\*\\*","^");
        s = s.replaceAll("\\^\\+","^");
        //s = s.replaceAll("\\(\\+","\\(0\\+");
        //s = s.replaceAll("\\(-","\\(0-");
        s = s.replaceAll("\\*\\+","\\*");
        if (s.charAt(0) == '+') {
            s = s.substring(1,s.length());
        } else if (s.charAt(0) == '-') {
            s = "0" + s;
        }
        return s;
    }
}

