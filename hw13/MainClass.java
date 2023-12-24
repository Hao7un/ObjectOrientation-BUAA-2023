import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MainClass {

    public static void main(String[] args) {
        HashMap<Book,Integer> bookShelf = new HashMap<>();
        Scanner s = new Scanner(System.in);
        int n = Integer.parseInt(s.nextLine());
        for (int i = 0;i < n; i++) {
            String input = s.nextLine();
            ArrayList<String> results = parseBookInput(input);
            Book book = new Book(results.get(0),results.get(1));
            int number = Integer.parseInt(results.get(2));
            bookShelf.put(book,number);
        }
        Library library = new Library(bookShelf);
        int m = Integer.parseInt(s.nextLine());
        for (int i = 0; i < m; i++) {
            String input = s.nextLine();
            ArrayList<String> results = parseCommandInput(input);
            library.operate(results);
        }
    }

    private static ArrayList<String> parseBookInput(String input) {
        String [] parts = input.split(" ");
        //解析类别号-序列号
        String[] typeAndId = parts[0].split("-");
        String type = typeAndId[0];
        String id = typeAndId[1];

        //解析数量
        String number = parts[1];

        ArrayList<String> results = new ArrayList<>();
        results.add(type);
        results.add(id);
        results.add(number);
        return results;
    }

    private static ArrayList<String> parseCommandInput(String input) {
        String [] parts = input.split(" ");
        ArrayList<String> results = new ArrayList<>();

        // 解析日期
        String [] date = parts[0].substring(1, parts[0].length() - 1).split("-");
        results.add(date[0]);
        results.add(date[1]);
        results.add(date[2]);

        // 解析学号
        String sid = parts[1];
        results.add(sid);

        // 解析操作
        String operation = parts[2];
        results.add(operation);

        // 解析类别号-序列号
        String[] typeAndId = parts[3].split("-");
        String type = typeAndId[0];
        String id = typeAndId[1];
        results.add(type);
        results.add(id);
        return results;
    }
}

