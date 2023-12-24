package library;

import utils.Student;
import utils.Book;
import utils.Order;
import utils.Calender;
import utils.Request;
import utils.RequestTag;

import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class PurchasingDepartment {
    private String libraryId;
    private HashMap<Book,Integer> bookShelf;
    private HashMap<String, Student> students;

    private HashMap<String,HashMap<Book,Integer>> allBooks; //所有学校的书
    private HashMap<String,HashMap<Book,String>> permissionMap; //所有学校的书的许可
    private HashMap<String,LinkedList<Order>> transportList;
    private LinkedList<Order> orderRecord; //用来记录学生的校际order情况，避免出现浪费

    private HashMap<Book,Integer> books;

    public PurchasingDepartment(String libraryId, HashMap<String, Student> students,
                                HashMap<Book, Integer> bookShelf,
                                LinkedHashMap<String,HashMap<Book,Integer>> allBooks,
                                HashMap<String,HashMap<Book,String>> permissionMap,
                                HashMap<String,LinkedList<Order>> transportList) {
        this.libraryId = libraryId;
        this.students = students;
        this.allBooks = allBooks;
        this.bookShelf = bookShelf;
        this.permissionMap = permissionMap;
        this.transportList = transportList;
        this.orderRecord = new LinkedList<>();
        this.books = new HashMap<>();
    }

    public RequestTag serveQueryOtherSchool(Request request) {
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        Book temp = new Book(type, bid, null,-1);
        Student student = request.getStudent();
        for (String otherLibrary : allBooks.keySet()) {
            if (allBooks.get(otherLibrary).containsKey(temp)  //存在可借的书
                    && allBooks.get(otherLibrary).get(temp) > 0 //有余本
                    && permissionMap.get(otherLibrary).get(temp).equals("Y")) { //允许外借
                return RequestTag.PURCHASE_CAN_BORROW_OTHER_SCHOOL;
            }
        }
        return RequestTag.PURCHASE_ORDER_HANDLE_ORDER;
    }

    public RequestTag serveOrderOtherSchool(Request request,
                                            ArrayList<String> transportationInformation) {
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        String sid = request.getStudent().getId();
        Book temp = new Book(type, bid, null,-1);
        Student student = request.getStudent();
        /*此刻已经持有的书籍*/
        if (student.hasBType() && type.equals("B")) {
            return RequestTag.ORDER_DENIED; //拒绝登记
        }
        if (student.hasBook(temp) && type.equals("C")) { //不满足借C
            return RequestTag.ORDER_DENIED;
        }
        /*本日已生效的校际借阅请求*/
        for (Order order : orderRecord) { //判断是否已经预约过
            if (order.getBook().equals(temp) &&
                    order.getSid().equals(student.getId())) {
                return RequestTag.ORDER_DENIED;
            } else if (type.equals("B") && order.getSid().equals(student.getId())) {
                return RequestTag.ORDER_DENIED;
            }
        }
        for (String otherLibrary : allBooks.keySet()) {
            if (allBooks.get(otherLibrary).containsKey(temp)  //存在可借的书
                    && allBooks.get(otherLibrary).get(temp) > 0 //有余本
                    && permissionMap.get(otherLibrary).get(temp).equals("Y")) { //允许外借
                Book book = new Book(type,bid,otherLibrary,-1);
                Order order = new Order(sid,book,otherLibrary,student.getSchoolId());
                orderRecord.add(order); //维护本日已生效的校际借阅请求
                transportList.get(this.libraryId).add(order);
                int number = allBooks.get(otherLibrary).get(book);
                allBooks.get(otherLibrary).put(book,number - 1); //别人的书下架
                transportationInformation.add(String.format("[%04d-%02d-%02d] %s-%s-%s " +
                                "got transported by purchasing department in %s",
                        request.getYear(),request.getMonth(),request.getDay(),
                        otherLibrary,book.getType(),book.getId(), otherLibrary));
                /*状态输出*/
                transportationInformation.add(String.format("(State) [%04d-%02d-%02d] %s-%s " +
                                "transfers from AvailableForBorrowing to InTransportation",
                        request.getYear(),request.getMonth(),request.getDay(),
                        book.getType(),book.getId()));
                return RequestTag.PURCHASE_ORDER_INTERSCHOOL_SUCCESS;
            }
        }
        return RequestTag.PURCHASE_ORDER_INTERSCHOOL_FAIL;
    }

    public RequestTag servePurchaseBook(int currentDays,LinkedList<Order> purchaseList) {
        LinkedHashMap<Book,Integer> purchaseMap = new LinkedHashMap<>(); //按照顺序买入
        for (Order order : purchaseList) {
            Book book = order.getBook();
            if (purchaseMap.containsKey(book)) {
                purchaseMap.put(book,purchaseMap.get(book) + 1);
            } else {
                purchaseMap.put(book,1);
            }
        }
        for (Book book:purchaseMap.keySet()) {
            int number = purchaseMap.get(book) > 3 ? purchaseMap.get(book) : 3;
            books.put(book,number);
            permissionMap.get(libraryId).put(book,"Y");
            ArrayList<Integer> date = Calender.calculateDate(currentDays);
            System.out.printf("[%4d-%02d-%02d] %s-%s-%s got purchased by " +
                            "purchasing department in %s\n",
                    date.get(0),date.get(1),date.get(2),libraryId,
                    book.getType(),book.getId(),libraryId);
        }
        return RequestTag.PURCHASE_SUCCESS;
    }

    public RequestTag serveReturnToOtherSchool(Request request,
                                               ArrayList<String> transportationInformation) {
        Order order = new Order(null,request.getBook(),request.getBook().getLibraryId(),null);
        transportList.get(request.getBook().getLibraryId()).add(order);
        transportationInformation.add(String.format("[%04d-%02d-%02d] %s-%s-%s " +
                        "got transported by purchasing department in %s",
                request.getYear(),request.getMonth(),request.getDay(),
                request.getBook().getLibraryId(),
                request.getBook().getType(),request.getBook().getId(),libraryId));
        /*状态输出*/
        transportationInformation.add(String.format("(State) [%04d-%02d-%02d] %s-%s transfers " +
                        "from NotAvailableForBorrowing to InTransportation",
                request.getYear(),request.getMonth(),request.getDay(),
                request.getBook().getType(),request.getBook().getId()));
        return null;
    }

    public void serveReceiveBooksFromOtherSchool(int currentDays) {
        orderRecord = new LinkedList<>();
        ArrayList<Integer> date = Calender.calculateDate(currentDays);
        for (Iterator<Order> iterator = transportList.get(libraryId).iterator();
             iterator.hasNext();) {
            Order order = iterator.next();
            String sid = order.getSid();
            Book book = order.getBook();
            System.out.printf("[%04d-%02d-%02d] %s-%s-%s got received " +
                            "by purchasing department in %s\n",
                    date.get(0), date.get(1), date.get(2), order.getBookSchool(),
                    book.getType(), book.getId(), libraryId);
            if (sid == null) { //还书回来，等待整理管理员来拿
                /*状态输出*/
                System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                                "InTransportation to NotAvailableForBorrowing\n",
                        date.get(0),date.get(1),date.get(2),
                        book.getType(),book.getId());
                books.merge(book,1,Integer::sum);
                iterator.remove();
            } else { //送书过来借出，可以直接派出
                /*状态输出*/
                System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                                "InTransportation to AvailableForBorrowing\n",
                        date.get(0),date.get(1),date.get(2),
                        book.getType(),book.getId());
            }
        }
    }

    public HashSet<String> serveDispatchInterSchoolBooks(int currentDays) {
        HashSet<String> studentGetBType = new HashSet<>();
        ArrayList<Integer> date = Calender.calculateDate(currentDays);
        for (Iterator<Order> iterator = transportList.get(libraryId).iterator();
             iterator.hasNext();) {
            Order order = iterator.next();
            String sid = order.getSid();
            Book book = order.getBook();
            if (sid != null) { //送书过来借出
                System.out.printf("[%04d-%02d-%02d] purchasing department lent %s-%s-%s to %s-%s\n",
                        date.get(0),date.get(1),date.get(2),order.getBookSchool(),
                        book.getType(),book.getId(),
                        libraryId,sid);
                /*状态输出*/
                System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                                "AvailableForBorrowing to BorrowedByStudent\n",
                        date.get(0),date.get(1),date.get(2),
                        book.getType(),book.getId());
                System.out.printf("[%04d-%02d-%02d] %s-%s borrowed " +
                                "%s-%s-%s from purchasing department\n",
                        date.get(0),date.get(1),date.get(2),libraryId,sid,
                        order.getBookSchool(),book.getType(),book.getId());
                if (book.getType().equals("B")) {
                    studentGetBType.add(sid);
                }
                Student student = students.get(sid);
                book.setBorrowedDays(currentDays); //记录下来日期
                student.addBook(book);
                iterator.remove();
            }
        }
        return studentGetBType;
    }

    public HashMap<Book,Integer> getAndclearBooks() {
        HashMap<Book,Integer> temp = new HashMap<>();
        for (Book book:books.keySet()) {
            temp.put(book,books.get(book));
        }
        books.replaceAll((b, v) -> 0);
        return temp;
    }
}

