public class Lexer {
    private final String input;
    private int pos = 0;
    private String curToken;

    public Lexer(String input) {
        this.input = input;
        this.next();
    }

    public void next() {
        if (pos == input.length()) {
            return;
        }
        char c = input.charAt(pos);
        if (Character.isDigit(c)) {
            curToken = getNumber();
        } else if ("()+-*^xyz".indexOf(c) != -1) {
            pos += 1;
            curToken = String.valueOf(c);
        } else if ("scfgh".indexOf(c) != -1) {
            curToken = getFunc();
        }
    }

    private String getNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length()
                && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            ++pos;
        }
        return sb.toString();
    }

    private String getFunc() {
        StringBuilder sb = new StringBuilder();
        if ("fgh".indexOf(input.charAt(pos)) != -1) { // custom defined function
            sb.append(input.charAt(pos)); //store f
            pos++;
            sb.append(input.charAt(pos)); //store f(
            pos++;
            for (int i = 1;i > 0;pos++) {
                if (input.charAt(pos) == ')') {
                    i--;
                } else if (input.charAt(pos) == '(') {
                    i++;
                }
                sb.append(input.charAt(pos));
            }
        } else { //sin cos
            while (pos < input.length()
                    && Character.isAlphabetic(input.charAt(pos))) {
                sb.append(input.charAt(pos));
                ++pos;
            }
        }
        return sb.toString();
    }

    public String peek() {
        return this.curToken;
    }

}