import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class OrderLibrarian {  //student-->data-->order

    private HashMap<String,Integer> dailyNumber;  //限制当日预约次数

    private LinkedList<Order> orderList;

    private HashMap<String,Student> students;

    public OrderLibrarian(HashMap<String,Student> students) {
        this.dailyNumber = new HashMap<>();
        this.orderList = new LinkedList<>();
        this.students = students;
    }

    public void updateDailyNumber() {
        this.dailyNumber = new HashMap<>();
    }

    public void serveOrder(Request request) {
        Student student = request.getStudent();
        String type = request.getType();
        String bid = request.getBid();
        String sid = student.getId();
        Book book = new Book(type,bid);
        if (student.hasBType() && type.equals("B")) { //不满足借B
            return; //拒绝登记
        }
        if (student.hasBook(book) && type.equals("C")) { //不满足借C
            return;
        }
        if (dailyNumber.containsKey(sid) && dailyNumber.get(sid) >= 3) { //已经超过三次
            return;
        }
        for (Order order:orderList) { //只处理第一次请求
            if (order.getBook().equals(book) && order.getSid().equals(sid)) {
                return;
            }
        }
        System.out.println("[" + request.getYear() + "-" + request.getMonth() + "-"
                + request.getDay() + "] " + sid + " ordered " +
                type + "-" + bid + " from ordering librarian");

        Order order = new Order(sid,book); //产生新的order
        orderList.add(order);
        /*成功预约才更新dailyNumber与record*/
        if (dailyNumber.containsKey(sid)) {
            dailyNumber.put(sid,dailyNumber.get(sid) + 1);
        } else {
            dailyNumber.put(sid, 1);
        }
    }

    public void notifyStudents(String year,String month,String day,
                               HashMap<Book,Integer> orderedBooks) {
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
                Student student = students.get(sid);
                student.addBook(book);
                String type = book.getType();
                if (type.equals("B")) { //如果取的是B书，清空请求
                    markBType.add(student.getId());
                }
                String bid = book.getId();
                System.out.println("[" + year + "-" + month + "-" + day + "] " +
                        sid + " borrowed " + type + "-" + bid + " from ordering librarian");
            }
        }
        for (Iterator<Order> iterator = orderList.iterator(); iterator.hasNext();) {
            Order order = iterator.next();
            if (order.getBook().getType().equals("B") && markBType.contains(order.getSid())) {
                iterator.remove();
            }
        }
    }

    public void clearBTypeOrder(String sid) { //借到B书时，清空所有B类书预约
        orderList.removeIf(order -> order.getSid().equals(sid)
                && order.getBook().getType().equals("B"));
    }

    public LinkedList<Order> getOrderList() {
        return orderList;
    }

}
