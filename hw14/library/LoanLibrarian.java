package library;

import utils.*;

import java.util.Calendar;
import java.util.HashMap;

public class LoanLibrarian {
    private  String libraryId;
    private HashMap<Book,Integer> bookShelf;
    private HashMap<Book,Integer> books;  //暂存的书-->数量

    public LoanLibrarian(String libraryId,HashMap<Book,Integer> bookShelf) {
        this.libraryId = libraryId;
        this.bookShelf = bookShelf;
        this.books = new HashMap<>();
        for (Book book:bookShelf.keySet()) {
            books.put(book,0);
        }
    }

    public RequestTag serveBorrow(Request request) { //处理B类图书的借书登记
        Student student = request.getStudent();
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        int currentDays = Calender.calculateDays(String.valueOf(request.getYear())
                ,String.valueOf(request.getMonth()),String.valueOf(request.getDay()));
        Book book = new Book(type,bid,libraryId,currentDays); //本图书馆自己的书
        String sid = student.getId();
        bookShelf.put(book,bookShelf.get(book) - 1); //这里保证一定至少有一本
        if (student.hasBType()) { //拒接借书
            System.out.printf("[%04d-%02d-%02d] borrowing and returning librarian " +
                            "refused lending %s-%s-%s to %s-%s\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    book.getLibraryId(),book.getType(),book.getId(), student.getSchoolId(),sid);
            books.merge(book,1,Integer::sum);
            System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                            "AvailableForBorrowing to NotAvailableForBorrowing\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    book.getType(),book.getId());
            return RequestTag.LOAN_LIB_BORROW_FAILURE;
        }  else { //成功借书
            student.addBook(book);
            System.out.printf("[%04d-%02d-%02d] borrowing and " +
                            "returning librarian lent %s-%s-%s to %s-%s\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    book.getLibraryId(),book.getType(),book.getId(),
                    student.getSchoolId(),sid);
            System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                            "AvailableForBorrowing to BorrowedByStudent\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    book.getType(),book.getId());
            System.out.printf("[%04d-%02d-%02d] %s-%s borrowed %s-%s-%s " +
                            "from borrowing and returning librarian\n",
                    request.getYear(),request.getMonth(),request.getDay(),student.getSchoolId(),sid,
                    book.getLibraryId(),book.getType(),book.getId());
            return RequestTag.LOAN_LIB_BORROW_SUCCESS;
        }
    }

    public RequestTag serveLost(Request request) {
        Student student = request.getStudent();
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        Book book = student.getBook(type,bid);
        student.removeBook(type,bid);
        System.out.printf("[%04d-%02d-%02d] %s-%s got punished " +
                        "by borrowing and returning librarian\n",
                request.getYear(),request.getMonth(),request.getDay(),
                request.getStudent().getSchoolId(),request.getStudent().getId());
        System.out.printf("[%04d-%02d-%02d] borrowing and " +
                        "returning librarian received %s-%s's fine\n",
                request.getYear(),request.getMonth(),request.getDay(),
                student.getSchoolId(),student.getId());
        return RequestTag.LOAN_LIB_BOOK_LOST;
    }

    public void serveDelayReturn(Request request) { //处理延迟还书
        Student student = request.getStudent();
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        Book book = student.getBook(type,bid);
        System.out.printf("[%04d-%02d-%02d] %s-%s got punished " +
                        "by borrowing and returning librarian\n",
                request.getYear(),request.getMonth(),request.getDay(),
                request.getStudent().getSchoolId(),request.getStudent().getId());
        System.out.printf("[%04d-%02d-%02d] borrowing and returning " +
                        "librarian received %s-%s's fine\n",
                request.getYear(),request.getMonth(),request.getDay(),
                student.getSchoolId(),student.getId());
    }

    public RequestTag serveReturn(Request request) {
        Student student = request.getStudent();
        String type = request.getBook().getType();
        String bid = request.getBook().getId();
        Book book = student.getBook(type,bid);
        if (book.isSmeared()) { //书已经损坏
            System.out.printf("[%04d-%02d-%02d] %s-%s got punished " +
                            "by borrowing and returning librarian\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    request.getStudent().getSchoolId(),request.getStudent().getId());
            System.out.printf("[%04d-%02d-%02d] borrowing and returning " +
                            "librarian received %s-%s's fine\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    student.getSchoolId(),student.getId());
            if (type.equals("B")) {
                student.removeBook(type,bid); //B书才删
                System.out.printf("[%04d-%02d-%02d] %s-%s returned %s-%s-%s to " +
                                "borrowing and returning librarian\n",
                        request.getYear(),request.getMonth(),request.getDay(),
                        request.getStudent().getSchoolId(),request.getStudent().getId(),
                        book.getLibraryId(),book.getType(),book.getId());
                System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +  //状态输出
                                "BorrowedByStudent to NotAvailableForBorrowing\n",
                        request.getYear(),request.getMonth(),request.getDay(),
                        book.getType(),book.getId());
                System.out.printf("[%04d-%02d-%02d] borrowing and returning " +
                                "librarian collected %s-%s-%s from %s-%s\n",
                        request.getYear(),request.getMonth(),request.getDay(),book.getLibraryId(),
                        book.getType(),book.getId(),request.getStudent().getSchoolId(),
                        request.getStudent().getId());
            }
            return RequestTag.LOAN_LIB_LOGISTICS_NEED_REPAIR;
        } else { //书没损坏
            student.removeBook(type,bid);
            int currentDays = Calender.calculateDays(String.valueOf(request.getYear())
                    ,String.valueOf(request.getMonth()),String.valueOf(request.getDay()));
            if (currentDays - book.getBorrowedDays() > 30) { //B书期限30
                serveDelayReturn(request);
            }
            System.out.printf("[%04d-%02d-%02d] %s-%s returned %s-%s-%s to " +
                            "borrowing and returning librarian\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    student.getSchoolId(),student.getId(),
                    book.getLibraryId(),book.getType(),book.getId());
            System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                            "BorrowedByStudent to NotAvailableForBorrowing\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    book.getType(),book.getId());
            System.out.printf("[%04d-%02d-%02d] borrowing and returning librarian collected " +
                            "%s-%s-%s from %s-%s\n",
                    request.getYear(),request.getMonth(),request.getDay(),
                    book.getLibraryId(),book.getType(),book.getId(),
                    student.getSchoolId(),student.getId());
            if (book.getLibraryId().equals(libraryId)) {
                books.merge(book,1,Integer::sum);
                return RequestTag.LOAN_LIB_RETURN_SUCCESS;
            } else { //外校的书
                return RequestTag.LOAN_PURCHASE_RETURN_OTHER_SCHOOL;
            }
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
