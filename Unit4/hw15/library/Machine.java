package library;

import utils.Book;
import utils.RequestTag;
import utils.Request;
import utils.Student;
import utils.Calender;
import java.util.HashMap;

public class Machine {
    private String libraryId;
    private HashMap<Book,Integer> bookShelf;
    private HashMap<Book,Integer> books;  //暂存的书-->数量

    public Machine(String libraryId,HashMap<Book,Integer> bookShelf) {
        this.libraryId = libraryId;
        this.bookShelf = bookShelf;
        this.books = new HashMap<>();
        for (Book book:bookShelf.keySet()) {
            books.put(book,0);
        }
    }

    public RequestTag serveQuery(Request request) {
        Student student = request.getStudent();
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        Book book = new Book(type,bid,null,-1);
        String sid = student.getId();
        System.out.printf("[%04d-%02d-%02d] %s-%s queried %s-%s from self-service machine\n",
                request.getYear(),request.getMonth(),request.getDay(),
                student.getSchoolId(),sid,
                book.getType(),book.getId());
        System.out.printf("(Sequence) [%04d-%02d-%02d] Library sends a message to Machine\n",
                request.getYear(),request.getMonth(),request.getDay());
        System.out.printf("[%04d-%02d-%02d] self-service machine provided information of %s-%s\n",
                request.getYear(),request.getMonth(),request.getDay(),book.getType(),book.getId());
        System.out.printf("(Sequence) [%04d-%02d-%02d] Machine sends a message to Library\n",
                request.getYear(),request.getMonth(),request.getDay());
        if (bookShelf.containsKey(book) && bookShelf.get(book) > 0) {
            if (type.equals("B")) {
                return RequestTag.MACHINE_LOAN_LIB_BORROW;
            } else if (type.equals("C")) {
                return RequestTag.MACHINE_MACHINE_BORROW;
            }
        } else if (!bookShelf.containsKey(book) ||
                (bookShelf.containsKey(book) && bookShelf.get(book) == 0)) {
            return RequestTag.MACHINE_PURCHASE_INTERSCHOOL_QUERY;
        }
        return RequestTag.MACHINE_QUERY_SUCCESS; //是A书，不需要进一步处理
    }

    public RequestTag serveBorrow(Request request) { //处理C类图书的借书登记
        Student student = request.getStudent();
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        int currentDays = Calender.calculateDays(String.valueOf(request.getYear())
                ,String.valueOf(request.getMonth()),String.valueOf(request.getDay()));
        Book book = new Book(type,bid,libraryId,currentDays);
        bookShelf.put(book,bookShelf.get(book) - 1); //这里保证一定至少有一本
        if (student.hasBook(book)) { //该同学已经借了这本C类书
            System.out.printf("[%04d-%02d-%02d] self-service machine refused lending " +
                            "%s-%s-%s to %s-%s\n",
                    request.getYear(),request.getMonth(),request.getDay(),book.getLibraryId(),
                    book.getType(),book.getId(), student.getSchoolId(),student.getId());
            books.merge(book,1,Integer::sum);
            /*状态输出*/
            System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                            "AvailableForBorrowing to NotAvailableForBorrowing\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    book.getType(),book.getId());
            System.out.printf("(Sequence) [%04d-%02d-%02d] Machine sends a message to Library\n",
                    request.getYear(),request.getMonth(),request.getDay());
            return RequestTag.MACHINE_BORROW_FAILURE;
        }  else {
            student.addBook(book);
            System.out.printf("[%04d-%02d-%02d] self-service machine lent %s-%s-%s to %s-%s\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    book.getLibraryId(),book.getType(),book.getId(),
                    student.getSchoolId(),student.getId());
            /*状态输出*/
            System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                            "AvailableForBorrowing to BorrowedByStudent\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    book.getType(),book.getId());
            System.out.printf("(Sequence) [%04d-%02d-%02d] Machine sends a message to Library\n",
                    request.getYear(),request.getMonth(),request.getDay());
            System.out.printf("[%04d-%02d-%02d] %s-%s borrowed %s-%s-%s " +
                            "from self-service machine\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    student.getSchoolId(),student.getId(),
                    book.getLibraryId(),book.getType(),book.getId());
            return RequestTag.MACHINE_BORROW_SUCCESS;
        }
    }

    public RequestTag serveReturn(Request request) {
        Student student = request.getStudent();
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        Book book = student.getBook(type,bid);
        int currentDays = Calender.calculateDays(String.valueOf(request.getYear())
                ,String.valueOf(request.getMonth()),String.valueOf(request.getDay()));
        if (book.isSmeared()) { //坏了，先去管理员
            return RequestTag.MACHINE_LOAN_LIB_NEED_REPAIR;
        } else if (currentDays - book.getBorrowedDays() > 60) { //超期，先去管理员
            return RequestTag.MACHINE_LOAN_LIB_SERVE_RETURN_DELAY;
        } else { //正常还书
            student.removeBook(type,bid);
            System.out.printf("[%04d-%02d-%02d] %s-%s returned %s-%s-%s to self-service machine\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    request.getStudent().getSchoolId(),request.getStudent().getId(),
                    book.getLibraryId(),book.getType(),book.getId());
            /*状态输出*/
            System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                            "BorrowedByStudent to NotAvailableForBorrowing\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    book.getType(),book.getId());
            System.out.printf("[%04d-%02d-%02d] self-service machine " +
                            "collected %s-%s-%s from %s-%s\n",
                    request.getYear(),request.getMonth(),request.getDay(),book.getLibraryId(),
                    book.getType(),book.getId(),request.getStudent().getSchoolId(),
                    request.getStudent().getId());
            if (book.getLibraryId().equals(libraryId)) { //自己学校的书
                books.merge(book,1, Integer::sum);
                return RequestTag.MACHINE_RETURN_SUCCESS;
            } else { //别人学校的书
                return RequestTag.MACHINE_PURCHASE_RETURN_OTHER_SCHOOL;
            }
        }
    }

    public void serveReturnAfterPunishment(Request request,boolean needCollect) {
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        Student student = request.getStudent();
        Book book = student.getBook(type,bid);
        student.removeBook(type,bid);
        System.out.printf("[%04d-%02d-%02d] %s-%s returned %s-%s-%s to self-service machine\n",
                request.getYear(),request.getMonth(),request.getDay(),
                request.getStudent().getSchoolId(),request.getStudent().getId(),
                book.getLibraryId(),book.getType(),book.getId());
        System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +  //状态输出
                        "BorrowedByStudent to NotAvailableForBorrowing\n",
                request.getYear(),request.getMonth(),request.getDay(),
                book.getType(),book.getId());
        System.out.printf("[%04d-%02d-%02d] self-service machine collected %s-%s-%s from %s-%s\n",
                request.getYear(),request.getMonth(),request.getDay(),
                book.getLibraryId(),book.getType(),book.getId(),
                request.getStudent().getSchoolId(),request.getStudent().getId());
        if (needCollect) { //true的时候，逾期还书且是自己学校的书
            books.merge(book,1, Integer::sum);
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
