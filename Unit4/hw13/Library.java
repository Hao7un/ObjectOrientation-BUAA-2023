import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.LinkedList;

public class Library {
    private  HashMap<Book,Integer> bookShelf;
    private  Machine machine;
    private  LoanLibrarian loanLibrarian;
    private  ArrangeLibrarian arrangeLibrarian;
    private  OrderLibrarian orderLibrarian;
    private  LogisticsDivision logisticsDivision;
    private  HashMap<String,Student> students;

    private int currentDays;

    public Library(HashMap<Book,Integer> bookShelf) {
        this.students = new HashMap<>();
        this.bookShelf = bookShelf;
        this.arrangeLibrarian = new ArrangeLibrarian(bookShelf);
        this.orderLibrarian = new OrderLibrarian(students);
        this.loanLibrarian = new LoanLibrarian(bookShelf);
        this.logisticsDivision = new LogisticsDivision(bookShelf);
        this.machine = new Machine(bookShelf);
        this.currentDays = 0;
    }

    public void operate(ArrayList<String> inputs) {
        String year = inputs.get(0);
        String month = inputs.get(1);
        String day = inputs.get(2);
        String sid = inputs.get(3);
        String operation = inputs.get(4);
        String type = inputs.get(5);
        String bid = inputs.get(6);
        int newDays = calculateDays(year,month,day); //转换为距离2023.1.1的日期
        /*更新日期*/
        while (newDays > currentDays) {
            currentDays++;
            orderLibrarian.updateDailyNumber();
            if (currentDays % 3 == 0) {
                ArrayList<String> currentDate = calculateDate(currentDays);
                LinkedList<Order> orderList = orderLibrarian.getOrderList();
                HashMap<Book,Integer> orderedBooks = arrangeLibrarian.arrangeBooks(
                        loanLibrarian.getAndclearBooks(),machine.getAndclearBooks(),
                        logisticsDivision.getAndclearBooks(),orderList);
                if (orderedBooks.size() == 0) { //没有要传过去的书
                    continue;
                }
                orderLibrarian.notifyStudents(currentDate.get(0),currentDate.get(1),
                        currentDate.get(2),orderedBooks);
            }
        }
        /*更新学生map*/
        Student student;
        if (!students.containsKey(sid)) {
            student = new Student(sid);
            students.put(sid,student);
        } else {
            student = students.get(sid);
        }
        Request request = new Request(year,month,day,student,type,bid);
        dispatchOperation(request,operation);
    }

    public void dispatchOperation(Request request,String operation) {
        RequestTag requestTag;
        String type = request.getType();
        String bid = request.getBid();
        String sid = request.getStudent().getId();
        Student student = request.getStudent();
        switch (operation) {
            case "borrowed":
                requestTag = machine.serveQuery(request);
                if (requestTag.equals(RequestTag.MACHINE_MACHINE_BORROW)) {
                    requestTag = machine.serveBorrow(request);
                } else if (requestTag.equals(RequestTag.MACHINE_LOAN_LIB_BORROW)) {
                    requestTag = loanLibrarian.serveBorrow(request);
                    if (requestTag.equals(RequestTag.LOAN_LIB_BORROW_SUCCESS) && type.equals("B")) {
                        orderLibrarian.clearBTypeOrder(sid);
                    }
                } else if (requestTag.equals(RequestTag.MACHINE_ORDER_LIB_ORDER)) {
                    orderLibrarian.serveOrder(request);
                }
                break;
            case "smeared":
                student.smearBook(type,bid);
                break;
            case "lost":
                student.lossBook(type,bid);
                requestTag = loanLibrarian.serveReturn(request);
                break;
            case "returned":
                if (type.equals("B")) {
                    requestTag = loanLibrarian.serveReturn(request);
                    if (requestTag.equals(RequestTag.LOAN_LIB_LOGISTICS_NEED_REPAIR)) {
                        logisticsDivision.repairBook(request);
                    }
                }
                else if (type.equals("C")) {
                    requestTag = machine.serveReturn(request);
                    if (requestTag.equals(RequestTag.MACHINE_LOAN_LIB_NEED_REPAIR)) {
                        requestTag = loanLibrarian.serveReturn(request);
                        if (requestTag.equals(RequestTag.LOAN_LIB_LOGISTICS_NEED_REPAIR)) {
                            machine.serveReturnSmeared(request);
                            logisticsDivision.repairBook(request);
                        }
                    }
                }
                break;
            default:
                System.out.println("Error:Unknown Command!");
        }
    }

    private int calculateDays(String year,String month,String day) { //根据年月日计算天数
        int inputYear = Integer.parseInt(year);
        int inputMonth = Integer.parseInt(month);
        int inputDay = Integer.parseInt(day);
        LocalDate inputDate = LocalDate.of(2023, 1, 1);
        LocalDate targetDate = LocalDate.of(inputYear, inputMonth, inputDay);
        long days = ChronoUnit.DAYS.between(inputDate, targetDate);
        return Integer.parseInt(String.valueOf(days));
    }

    private ArrayList<String> calculateDate(int days) { //根据天数计算年月日
        LocalDate today = LocalDate.of(2023,1,1).plusDays(days);
        int dayNum = today.getDayOfMonth();
        int monthNum = today.getMonthValue();
        String day;
        String month;
        if (dayNum < 10) {
            day = "0" + dayNum;
        } else {
            day = String.valueOf(dayNum);
        }
        if (monthNum < 10) {
            month = "0" + monthNum;
        } else {
            month = String.valueOf(monthNum);
        }
        String year = String.valueOf(today.getYear());
        ArrayList<String> results = new ArrayList<>();
        results.add(year);
        results.add(month);
        results.add(day);
        return results;
    }
}
