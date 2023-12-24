import library.Library;
import utils.Book;
import utils.Calender;
import utils.Order;
import utils.RequestTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Controller {
    private LinkedHashMap<String, HashMap<Book,Integer>> allBooks;
    private HashMap<String,HashMap<Book,String>> permissionMap;
    private LinkedHashMap<String, Library> libraries;

    public Controller(LinkedHashMap<String, HashMap<Book,Integer>> allBooks,
                      HashMap<String,HashMap<Book,String>> permissionMap) {
        this.allBooks = allBooks;
        this.permissionMap = permissionMap;
        this.libraries = new LinkedHashMap<>();
    }

    //0:days(天数) 1:year 2:month 3:day 4:libraryId 5:sid 6:op 7:bookType 8:bookId
    public void operate(ArrayList<ArrayList<String>> parsedCommands) {
        HashMap<String, LinkedList<Order>> transportList = new HashMap<>();
        for (String libraryId: allBooks.keySet()) {
            transportList.put(libraryId,new LinkedList<>());
        }
        for (String libraryId : allBooks.keySet()) {
            Library library = new Library(libraryId,allBooks,permissionMap,transportList);
            libraries.put(libraryId,library);
        }
        for (int days = 0; days < 365; days++) { //距离2023.1.1的天数
            if (parsedCommands.size() == 0) { //已经处理完所有命令，退出程序
                break;
            }
            for (String libraryId : libraries.keySet()) {
                libraries.get(libraryId).dailyUpdate();
            }
            for (String libraryId : libraries.keySet()) {
                libraries.get(libraryId).receiveBooksFromOtherSchool(days); //图书管理处接受运进图书
            }
            for (String libraryId : libraries.keySet()) {
                libraries.get(libraryId).dispatchInterSchoolBooks(days); //图书管理处发放校际借阅图书
            }
            if (days % 3 == 0) {
                for (String libraryId : libraries.keySet()) {
                    libraries.get(libraryId).purchaseBooks(days); //图书管理处确认本校购入图书
                }
                ArrayList<Integer> date = Calender.calculateDate(days);
                System.out.printf("[%4d-%02d-%02d] arranging librarian arranged all the books\n",
                        date.get(0),date.get(1),date.get(2));
                for (String libraryId : libraries.keySet()) {
                    libraries.get(libraryId).arrangeLibrary(days); //整理管理员整理图书
                }
                for (String libraryId : libraries.keySet()) {
                    libraries.get(libraryId).notifyStudents(days); //预定管理员发放校内预定图书
                }
            }
            ArrayList<ArrayList<String>> postSchoolCommands = new ArrayList<>();
            while (!parsedCommands.isEmpty() && //       开馆ing
                    Integer.parseInt(parsedCommands.get(0).get(0)) == days) { //循环处理
                String libraryId = parsedCommands.get(0).get(4);
                RequestTag requestTag = libraries.get(libraryId).open(parsedCommands.get(0)); //处理请求
                if (requestTag.equals(RequestTag.LIBRARY_CONTROLLER_NEED_POST_SCHOOL)) { //需要等待图书馆闭馆
                    postSchoolCommands.add(parsedCommands.get(0)); //加入等待队列
                }
                parsedCommands.remove(0);
            }
            while (!postSchoolCommands.isEmpty()) { //闭馆
                ArrayList<String> command = postSchoolCommands.get(0);
                String libraryId = command.get(4);
                libraries.get(libraryId).postopen(command);
                postSchoolCommands.remove(0);
            }
            for (String libraryId : libraries.keySet()) {
                libraries.get(libraryId).printOrderInformation(); //整理管理员整理图书
            }
            for (String libraryId : libraries.keySet()) {
                libraries.get(libraryId).printTransportationInformation(); //整理管理员整理图书
            }
        }
    }

}
