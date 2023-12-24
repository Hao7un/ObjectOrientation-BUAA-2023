import utils.Book;
import utils.Calender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        LinkedHashMap<String,HashMap<Book,Integer>> allBooks = new LinkedHashMap<>();        //书与副本
        HashMap<String,HashMap<Book,String>> permissionMap = new HashMap<>();    //书与许可
        Scanner s = new Scanner(System.in);
        int n = Integer.parseInt(s.nextLine()); //n所学校
        for (int i = 0;i < n; i++) {
            String input1 = s.nextLine();
            String [] temp1 = input1.split(" ");
            String libraryId = temp1[0];
            int bookNumber = Integer.parseInt(temp1[1]);
            HashMap<Book,Integer> bookShelf = new HashMap<>();
            HashMap<Book,String> permission = new HashMap<>();
            for (int j = 0; j < bookNumber; j++) {
                String input2 = s.nextLine(); //B-0002 2 N
                ArrayList<String> temp2 = parseBookInput(input2); //type id number permission
                Book book = new Book(temp2.get(0),temp2.get(1),null,0);
                bookShelf.put(book,Integer.parseInt(temp2.get(2)));
                permission.put(book,temp2.get(3));
            }
            allBooks.put(libraryId,bookShelf);
            permissionMap.put(libraryId,permission);
        }
        Controller controller = new Controller(allBooks,permissionMap);
        int m = Integer.parseInt(s.nextLine());
        ArrayList<ArrayList<String>> commands = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            String input = s.nextLine();
            commands.add(parseCommand(input));
        }
        controller.operate(commands);
    }

    private static ArrayList<String> parseBookInput(String input) {
        String [] parts = input.split(" ");
        //解析类别号-序列号
        String[] typeAndId = parts[0].split("-");
        String type = typeAndId[0];
        String id = typeAndId[1];
        //解析数量
        String number = parts[1];
        //解析是否允许外借
        String permission = parts[2];

        ArrayList<String> results = new ArrayList<>();
        results.add(type);
        results.add(id);
        results.add(number);
        results.add(permission);
        return results;
    }

    private static ArrayList<String> parseCommand(String command) {
        String [] parts = command.split(" ");
        ArrayList<String> results = new ArrayList<>();

        // 解析日期
        String [] date = parts[0].substring(1, parts[0].length() - 1).split("-");
        int days =  Calender.calculateDays(date[0],date[1],date[2]);
        results.add(String.valueOf(days));
        results.add(date[0]);
        results.add(date[1]);
        results.add(date[2]);

        // 解析学校与学号
        String schoolId = parts[1].split("-")[0];
        String sid = parts[1].split("-")[1];
        results.add(schoolId);
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

