package library;

import utils.Book;
import utils.Request;
import utils.RequestTag;

import java.util.HashMap;

public class LogisticsDivision {
    private String libraryId;
    private HashMap<Book,Integer> books;  //暂存的书-->数量

    public LogisticsDivision(String libraryId,HashMap<Book,Integer> bookShelf) {
        this.libraryId = libraryId;
        this.books = new HashMap<>();
        for (Book book:bookShelf.keySet()) {
            books.put(book,0);
        }
    }

    public RequestTag repairBook(Request request) {
        Book book = request.getBook();
        System.out.printf("[%4d-%02d-%02d] %s-%s-%s got repaired by logistics division in %s\n",
                request.getYear(),request.getMonth(),request.getDay(),
                book.getLibraryId(),book.getType(),book.getId(),libraryId);
        /*状态输出*/
        System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                        "NotAvailableForBorrowing to NotAvailableForBorrowing\n",
                request.getYear(),request.getMonth(),request.getDay(),
                book.getType(),book.getId());
        if (request.getBook().getLibraryId().equals(libraryId)) {
            books.merge(book,1,Integer::sum);
            return RequestTag.LOGISTICS_REPAIR_SUCCESS;
        } else {
            return RequestTag.LOGISTICS_NEED_TRANSPORT;
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
