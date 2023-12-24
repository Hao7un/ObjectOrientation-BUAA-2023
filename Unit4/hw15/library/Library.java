package library;

import utils.Student;
import utils.Book;
import utils.Order;
import utils.Calender;
import utils.Request;
import utils.RequestTag;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Library {
    private String libraryId;
    private  Machine machine;
    private  LoanLibrarian loanLibrarian;
    private  ArrangeLibrarian arrangeLibrarian;
    private  OrderLibrarian orderLibrarian;
    private  LogisticsDivision logisticsDivision;
    private PurchasingDepartment purchasingDepartment;
    private  HashMap<String, Student> students;
    private ArrayList<String> orderInformation;
    private ArrayList<String> transportationInformation;

    public Library(String libraryId, LinkedHashMap<String, HashMap<Book,Integer>> allBooks,
                   HashMap<String, HashMap<Book,String>> permissionMap,
                   HashMap<String,LinkedList<Order>> transportList) {
        this.libraryId = libraryId;
        this.students = new HashMap<>();
        this.arrangeLibrarian = new ArrangeLibrarian(allBooks.get(libraryId));
        this.orderLibrarian = new OrderLibrarian(libraryId,students,allBooks.get(libraryId));
        this.loanLibrarian = new LoanLibrarian(libraryId,allBooks.get(libraryId));
        this.logisticsDivision = new LogisticsDivision(libraryId,allBooks.get(libraryId));
        this.machine = new Machine(libraryId,allBooks.get(libraryId));
        this.purchasingDepartment = new PurchasingDepartment(libraryId,students,allBooks.
                get(libraryId),allBooks,permissionMap,transportList);
        this.orderInformation = new ArrayList<>();
        this.transportationInformation = new ArrayList<>();
    }

    public void dailyUpdate() {
        orderLibrarian.updateDailyNumber();
        this.transportationInformation = new ArrayList<>();
        this.orderInformation = new ArrayList<>();
    }

    public void receiveBooksFromOtherSchool(int currentDays) {
        purchasingDepartment.serveReceiveBooksFromOtherSchool(currentDays);
    }

    public void dispatchInterSchoolBooks(int currentDays) {
        HashSet<String> studentsGetBType = purchasingDepartment.serveDispatchInterSchoolBooks(
                currentDays);
        for (String sid : studentsGetBType) {
            orderLibrarian.clearBTypeOrder(sid); //从外校借到了B
        }
    }

    public void purchaseBooks(int currentDays) {
        purchasingDepartment.servePurchaseBook(currentDays,
                orderLibrarian.getAndClearPurchaseList());
    }

    public void arrangeLibrary(int currentDays) {
        LinkedList<Order> orderList = orderLibrarian.getOrderList();
        arrangeLibrarian.arrangeBooks(currentDays,
                loanLibrarian.getAndclearBooks(),machine.getAndclearBooks(),
                logisticsDivision.getAndclearBooks(),
                purchasingDepartment.getAndclearBooks(),orderList);
    }

    public void notifyStudents(int currentDays) {
        HashMap<Book,Integer> orderedBooks = arrangeLibrarian.getAndclearBooksToOrder();
        if (orderedBooks.size() == 0) { //没有要传过去的书
            return;
        }
        ArrayList<Integer> currentDate = Calender.calculateDate(currentDays);
        orderLibrarian.getOrderedBook(currentDate,orderedBooks);
    }

    public RequestTag open(ArrayList<String> command) {
        //0:days(天数) 1:year 2:month 3:day 4:libraryId 5:sid 6:op 7:bookType 8:bookId
        String year = command.get(1);
        String month = command.get(2);
        String day = command.get(3);
        String libraryId = command.get(4);
        String sid = command.get(5);
        String operation = command.get(6);
        String type = command.get(7);
        String bid = command.get(8);
        /*更新学生map*/
        Student student;
        if (!students.containsKey(sid)) {
            student = new Student(sid, libraryId);
            students.put(sid, student);
        } else {
            student = students.get(sid);
        }
        Book book = student.getBook(type, bid);
        Request request;
        if (book == null) { //该学生此时并不拥有这本书-->borrow
            request = new Request(Integer.parseInt(year), Integer.parseInt(month),
                    Integer.parseInt(day), student, new Book(type, bid, null,-1));
        } else { //该学生此时有这本书-->smear,lost,returned
            request = new Request(Integer.parseInt(year), Integer.parseInt(month),
                    Integer.parseInt(day), student, book);
        }
        switch (operation) {
            case "borrowed":
                return handleBorrow(request);
            case "smeared":
                return handleSmear(request);
            case "lost":
                return handleLost(request);
            case "returned":
                return handleReturn(request);
            default:
                System.out.printf("Unknown Command!\n");
                return RequestTag.LIBRARY_CONTROLLER_COMMAND_FINNISH;
        }
    }

    private RequestTag handleBorrow(Request request) {
        RequestTag requestTag;
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        String sid = request.getStudent().getId();
        Student student = request.getStudent();
        requestTag = machine.serveQuery(request);
        if (requestTag.equals(RequestTag.MACHINE_MACHINE_BORROW)) { //校内借阅C类书籍
            requestTag = machine.serveBorrow(request);
        } else if (requestTag.equals(RequestTag.MACHINE_LOAN_LIB_BORROW)) { //校内借阅B类书籍
            requestTag = loanLibrarian.serveBorrow(request);
            if (requestTag.equals(RequestTag.LOAN_LIB_BORROW_SUCCESS) && type.equals("B")) {
                orderLibrarian.clearBTypeOrder(sid); //进行B书预定删除
            }
        }
        else if (requestTag.equals(RequestTag.MACHINE_PURCHASE_INTERSCHOOL_QUERY)) {
            return RequestTag.LIBRARY_CONTROLLER_NEED_POST_SCHOOL;
        }
        return RequestTag.LIBRARY_CONTROLLER_COMMAND_FINNISH;
    }

    private RequestTag handleSmear(Request request) {
        RequestTag requestTag;
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        String sid = request.getStudent().getId();
        Student student = request.getStudent();
        student.smearBook(type,bid);
        return RequestTag.LIBRARY_CONTROLLER_COMMAND_FINNISH;
    }

    private RequestTag handleLost(Request request) {
        RequestTag requestTag;
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        String sid = request.getStudent().getId();
        Student student = request.getStudent();
        student.lossBook(type,bid);
        requestTag = loanLibrarian.serveLost(request);
        return RequestTag.LIBRARY_CONTROLLER_COMMAND_FINNISH;
    }

    private RequestTag handleReturn(Request request) {
        RequestTag requestTag;
        String type = request.getBook().getType();
        if (type.equals("B")) {
            requestTag = loanLibrarian.serveReturn(request);
            if (requestTag.equals(RequestTag.LOAN_LIB_LOGISTICS_NEED_REPAIR)) {
                requestTag = logisticsDivision.repairBook(request);
                if (requestTag.equals(RequestTag.LOGISTICS_NEED_TRANSPORT)) { //修好了，但是不是本校的书
                    purchasingDepartment.serveReturnToOtherSchool(request,
                            transportationInformation);
                }
            }  else if (requestTag.equals(RequestTag.LOAN_PURCHASE_RETURN_OTHER_SCHOOL)) {
                purchasingDepartment.serveReturnToOtherSchool(request,
                        transportationInformation);
            }
        }
        else if (type.equals("C")) {
            requestTag = machine.serveReturn(request);
            if (requestTag.equals(RequestTag.MACHINE_LOAN_LIB_NEED_REPAIR)) { //书坏了
                requestTag = loanLibrarian.serveReturn(request);
                machine.serveReturnAfterPunishment(request,false); //书籍损坏，书不需要缓存在machine
                requestTag = logisticsDivision.repairBook(request);
                if (requestTag.equals(RequestTag.LOGISTICS_NEED_TRANSPORT)) { //判断是否要归还
                    purchasingDepartment.serveReturnToOtherSchool(request,
                            transportationInformation);
                }
            } else if (requestTag.equals(RequestTag.MACHINE_LOAN_LIB_SERVE_RETURN_DELAY)) {
                type = request.getBook().getType();
                String bid = request.getBook().getId();
                String libraryId = request.getStudent().getBook(type,bid).getLibraryId();
                loanLibrarian.serveDelayReturn(request); //交罚款
                if (!libraryId.equals(this.libraryId)) { //不是自己学校的书
                    machine.serveReturnAfterPunishment(request,false); //不需要存books里
                    purchasingDepartment.serveReturnToOtherSchool(request,
                            transportationInformation);
                } else { //自己学校的书，要存books里
                    machine.serveReturnAfterPunishment(request,true);
                }
            } else if (requestTag.equals(RequestTag.MACHINE_PURCHASE_RETURN_OTHER_SCHOOL)) {
                purchasingDepartment.serveReturnToOtherSchool(request,
                        transportationInformation);
            }
        }
        return RequestTag.LIBRARY_CONTROLLER_COMMAND_FINNISH;
    }

    public void postopen(ArrayList<String> command) {
        String year = command.get(1);
        String month = command.get(2);
        String day = command.get(3);
        String libraryId = command.get(4);
        String sid = command.get(5);
        String operation = command.get(6);
        String type = command.get(7);
        String bid = command.get(8);
        Student student;
        if (!students.containsKey(sid)) {
            student = new Student(sid,libraryId);
            students.put(sid,student);
        } else {
            student = students.get(sid);
        }
        Book book = student.getBook(type,bid);
        Request request;
        if (book == null) { //该学生此时并不拥有这本书-->borrow
            request = new Request(Integer.parseInt(year),Integer.parseInt(month),
                    Integer.parseInt(day),student,new Book(type,bid,null,-1));
        } else { //该学生此时有这本书-->smear,lost,returned
            request = new Request(Integer.parseInt(year),Integer.parseInt(month),
                    Integer.parseInt(day),student,book);
        }
        RequestTag requestTag;
        requestTag = purchasingDepartment.serveQueryOtherSchool(request);
        if (requestTag.equals(RequestTag.PURCHASE_CAN_BORROW_OTHER_SCHOOL)) { //进行校际借阅
            purchasingDepartment.serveOrderOtherSchool(request,transportationInformation);
        } else if (requestTag.equals(RequestTag.PURCHASE_ORDER_HANDLE_ORDER)) { //进行预定
            orderLibrarian.orderNewBook(request,orderInformation);
        }
    }

    public void printOrderInformation() {
        for (String output : orderInformation) {
            System.out.println(output);
        }
    }

    public void printTransportationInformation() {
        for (String output : transportationInformation) {
            System.out.println(output);
        }
    }
}
