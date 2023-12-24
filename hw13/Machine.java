import java.util.HashMap;

public class Machine {
    private HashMap<Book,Integer> bookShelf;
    private HashMap<Book,Integer> books;  //暂存的书-->数量

    public Machine(HashMap<Book,Integer> bookShelf) {
        this.bookShelf = bookShelf;
        this.books = new HashMap<>();
        for (Book book:bookShelf.keySet()) {
            books.put(book,0);
        }
    }

    public RequestTag serveQuery(Request request) {
        Student student = request.getStudent();
        String type = request.getType();
        String bid = request.getBid();
        Book book = new Book(type,bid);
        String sid = student.getId();
        System.out.println("[" + request.getYear() + "-" + request.getMonth() +
                "-" + request.getDay() + "] " + sid + " queried " +
                type + "-" + bid + " from self-service machine");
        if (bookShelf.get(book) == 0) { //没书了 请您去order吧
            return RequestTag.MACHINE_ORDER_LIB_ORDER;
        } else {
            if (type.equals("B")) {
                return RequestTag.MACHINE_LOAN_LIB_BORROW;
            } else if (type.equals("C")) {
                return RequestTag.MACHINE_MACHINE_BORROW;
            }
        }
        return RequestTag.MACHINE_QUERY_SUCCESS; //是A书，不需要进一步处理
    }

    public RequestTag serveBorrow(Request request) { //处理C类图书的借书登记
        Student student = request.getStudent();
        String type = request.getType();
        String bid = request.getBid();
        Book book = new Book(type,bid);
        bookShelf.put(book,bookShelf.get(book) - 1); //这里保证一定至少有一本
        if (student.hasBook(book)) { //该同学已经借了这本C类书
            books.put(book,books.get(book) + 1);
            return RequestTag.MACHINE_BORROW_FAILURE;
        }  else {
            student.addBook(book);
            String sid = student.getId();
            System.out.println("[" + request.getYear() + "-" + request.getMonth() +
                    "-" + request.getDay() + "] " + sid + " borrowed " +
                    type + "-" + bid + " from self-service machine");
            return RequestTag.MACHINE_BORROW_SUCCESS;
        }
    }

    public RequestTag serveReturn(Request request) {
        Student student = request.getStudent();
        String type = request.getType();
        String bid = request.getBid();
        Book book = student.getBook(type,bid);
        String sid = student.getId();
        if (book.isSmeared()) {
            return RequestTag.MACHINE_LOAN_LIB_NEED_REPAIR;
        } else { //需要保证lost的书不丢进来
            student.removeBook(type,bid);
            System.out.println("[" + request.getYear() + "-" + request.getMonth() + "-"
                    + request.getDay() + "] " + sid + " returned " +
                    type + "-" + bid + " to self-service machine");
            books.put(book,books.get(book) + 1);
            return RequestTag.MACHINE_RETURN_SUCCESS;
        }
    }

    public void serveReturnSmeared(Request request) {
        String type = request.getType();
        String bid = request.getBid();
        Student student = request.getStudent();
        student.removeBook(type,bid);
        String sid = student.getId();
        System.out.println("[" + request.getYear() + "-" + request.getMonth() +
                "-" + request.getDay() + "] " + sid + " returned " +
                type + "-" + bid + " to self-service machine");
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
