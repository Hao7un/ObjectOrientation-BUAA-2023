import java.util.HashMap;
import java.util.Scanner;
import expression.Expr;

public class MainClass
{
    private static HashMap<Character,String> functions = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashMap<Character,String> functions = new HashMap<>();
        int number = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0;i < number; i++) {
            String function = scanner.nextLine();
            function = preprocess(function);
            String name = function.split("=")[0];
            String body = function.split("=")[1];
            Lexer lexer = new Lexer(body);
            Parser parser = new Parser(lexer);
            Expr expr = parser.parseExpr().simplify();
            String last = preprocess(expr.toString());
            functions.put(function.charAt(0),name + "=" + last);
            MainClass.functions = functions;
        }

        String input = scanner.nextLine();
        input = preprocess(input);
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Expr expr = parser.parseExpr();
        expr = expr.simplify();
        System.out.println(expr.toString());
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
        s = s.replaceAll("\\*\\+","\\*");
        if (s.charAt(0) == '+') {
            s = s.substring(1);
        } else if (s.charAt(0) == '-') {
            s = "0" + s;
        }
        return s;
    }

    public static HashMap<Character,String> getFunctions() {
        return functions;
    }
}
