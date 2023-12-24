package library;

import utils.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;

public class OrderLibrarian {  //student-->data-->order
    private String libraryId;

    private HashMap<String,Integer> dailyNumber;  //限制当日预约次数

    private LinkedList<Order> orderList;

    private HashMap<String, Student> students;
    private HashMap<Book,Integer> bookShelf;
    private LinkedList<Order> purchaseList;

    public OrderLibrarian(String libraryId,HashMap<String, Student> students,
                          HashMap<Book,Integer> bookShelf) {
        this.libraryId = libraryId;
        this.dailyNumber = new HashMap<>();
        this.orderList = new LinkedList<>();
        this.students = students;
        this.bookShelf = bookShelf;
        this.purchaseList = new LinkedList<>();
    }

    public void updateDailyNumber() {
        this.dailyNumber = new HashMap<>();
    }

    public RequestTag serveOrder(Request request,ArrayList<String> orderInformation) {
        Student student = request.getStudent();
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        String sid = student.getId();
        Book book = new Book(type,bid,libraryId,0);
        if (student.hasBType() && type.equals("B")) { //不满足借B
            return RequestTag.ORDER_DENIED; //拒绝登记
        }
        if (student.hasBook(book) && type.equals("C")) { //不满足借C
            return RequestTag.ORDER_DENIED;
        }
        if (dailyNumber.containsKey(sid) && dailyNumber.get(sid) >= 3) { //已经超过三次
            return RequestTag.ORDER_DENIED;
        }
        for (Order order:orderList) { //只处理第一次请求
            if (order.getBook().equals(book) && order.getSid().equals(sid)) {
                return RequestTag.ORDER_DENIED;
            }
        }
        orderInformation.add(String.format("[%4d-%02d-%02d] %s-%s ordered %s-%s-%s " +
                        "from ordering librarian",
                request.getYear(),request.getMonth(),request.getDay(),
                request.getStudent().getSchoolId(), request.getStudent().getId(),
                request.getStudent().getSchoolId(),
                request.getBook().getType(),request.getBook().getId()));
        orderInformation.add(String.format("[%4d-%02d-%02d] ordering librarian recorded " +
                        "%s-%s's order of %s-%s-%s",
                request.getYear(),request.getMonth(),request.getDay(),
                request.getStudent().getSchoolId(),request.getStudent().getId(),
                book.getLibraryId(),book.getType(),book.getId()));
        Order order = new Order(sid,book,libraryId,libraryId); //产生新的order，本校的
        orderList.add(order);
        /*成功预约才更新dailyNumber与record*/
        if (dailyNumber.containsKey(sid)) {
            dailyNumber.put(sid,dailyNumber.get(sid) + 1);
        } else {
            dailyNumber.put(sid, 1);
        }
        if (bookShelf.containsKey(book)) { //正常结束
            return RequestTag.ORDER_SUCCESS;
        } else { //要买书
            Order buyOrder = new Order(sid,book,libraryId,libraryId);
            purchaseList.add(buyOrder);
            return RequestTag.ORDER_PURCHASE_NEED_PURCHASE;
        }
    }

    public void notifyStudents(ArrayList<Integer> date,
                               HashMap<Book, Integer> orderedBooks) {
        HashSet<String> markBType = new HashSet<>();
        for (Iterator<Order> iterator = orderList.iterator(); iterator.hasNext();) {
            Order order = iterator.next();
            if (order.getBook().getId().equals("B") && markBType.contains(order.getSid())) {
                continue;
            }
            if (orderedBooks.containsKey(order.getBook())) {
                iterator.remove(); //从orderList删除order
                String sid = order.getSid();
                Book book = order.getBook();
                orderedBooks.put(book,orderedBooks.get(book) - 1);
                if (orderedBooks.get(book) == 0) {
                    orderedBooks.remove(book);
                }
                /*通知student来取*/
                int currentDays = Calender.calculateDays(String.valueOf(date.get(0)),
                        String.valueOf(date.get(1)),String.valueOf(date.get(2)));
                book.setBorrowedDays(currentDays);
                Student student = students.get(sid);
                student.addBook(book);
                String type = book.getType();
                if (type.equals("B")) { //如果取的是B书，清空请求
                    markBType.add(student.getId());
                }
                System.out.printf("[%04d-%02d-%02d] ordering librarian lent %s-%s-%s to %s-%s\n",
                        date.get(0),date.get(1),date.get(2),book.getLibraryId(),
                        book.getType(),book.getId(),
                        student.getSchoolId(),student.getId());
                /*状态输出*/
                System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                                "AvailableForBorrowing to BorrowedByStudent\n",
                        date.get(0),date.get(1),date.get(2),
                        book.getType(),book.getId());
                System.out.printf("[%04d-%02d-%02d] %s-%s borrowed %s-%s-%s " +
                                "from ordering librarian\n",
                        date.get(0),date.get(1),date.get(2),student.getSchoolId(),student.getId(),
                        book.getLibraryId(), book.getType(),book.getId());
            }
        }
        for (String sid : markBType) {
            clearBTypeOrder(sid);
        }
    }

    public void clearBTypeOrder(String sid) { //借到B书时，清空所有B类书预约
        orderList.removeIf(order -> order.getSid().equals(sid)
                && order.getBook().getType().equals("B"));
        purchaseList.removeIf(purchaseOrder -> purchaseOrder.getSid().equals(sid)
                && purchaseOrder.getBook().getType().equals("B"));
    }

    public LinkedList<Order> getOrderList() {
        return orderList;
    }

    public LinkedList<Order> getAndClearPurchaseList() {
        LinkedList<Order>  temp = purchaseList;
        this.purchaseList = new LinkedList<>();
        return temp;
    }

}
