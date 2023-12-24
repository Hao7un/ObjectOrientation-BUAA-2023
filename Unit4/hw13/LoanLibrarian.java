import java.util.HashMap;

public class LoanLibrarian {

    private HashMap<Book,Integer> bookShelf;
    private HashMap<Book,Integer> books;  //暂存的书-->数量

    public LoanLibrarian(HashMap<Book,Integer> bookShelf) {
        this.bookShelf = bookShelf;
        this.books = new HashMap<>();
        for (Book book:bookShelf.keySet()) {
            books.put(book,0);
        }
    }

    public RequestTag serveBorrow(Request request) { //处理B类图书的借书登记
        Student student = request.getStudent();
        String type = request.getType();
        String bid = request.getBid();
        Book book = new Book(type,bid);
        String sid = student.getId();
        bookShelf.put(book,bookShelf.get(book) - 1); //这里保证一定至少有一本
        if (student.hasBType()) { //拒接借书
            books.put(book,books.get(book) + 1);
            return RequestTag.LOAN_LIB_BORROW_FAILURE;
        }  else { //成功借书
            student.addBook(book);
            System.out.println("[" + request.getYear() + "-" + request.getMonth() +
                    "-" + request.getDay() + "] " + sid + " borrowed " +
                    type + "-" + bid + " from borrowing and returning librarian");
            return RequestTag.LOAN_LIB_BORROW_SUCCESS;
        }
    }

    public RequestTag serveReturn(Request request) {
        Student student = request.getStudent();
        String type = request.getType();
        String bid = request.getBid();
        String year = request.getYear();
        String month = request.getMonth();
        String day = request.getDay();
        Book book = student.getBook(type,bid);
        String sid = student.getId();
        if (book.isLost()) { //书丢了
            student.removeBook(type,bid);
            System.out.println("[" + year + "-" + month + "-" + day + "] " +
                    sid + " got punished by " +
                    "borrowing and returning librarian");
            return RequestTag.LOAN_LIB_BOOK_LOST;
        }
        else if (book.isSmeared()) { //书已经损坏
            System.out.println("[" + year + "-" + month + "-" + day + "] " +
                    sid + " got punished by " +
                    "borrowing and returning librarian");
            if (type.equals("B")) {
                student.removeBook(type,bid); //B书才删
                System.out.println("[" + year + "-" + month + "-" + day + "] " +
                        sid + " returned " +
                        type + "-" + bid + " to borrowing and returning librarian");
            }
            return RequestTag.LOAN_LIB_LOGISTICS_NEED_REPAIR;
        } else { //书没损坏
            student.removeBook(type,bid);
            System.out.println("[" + year + "-" + month + "-" + day + "] " + sid + " returned " +
                    type + "-" + bid + " to borrowing and returning librarian");
            books.put(book,books.get(book) + 1);

            return RequestTag.LOAN_LIB_RETURN_SUCCESS;
        }
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
