import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class ArrangeLibrarian {
    private HashMap<Book,Integer> bookShelf;

    public ArrangeLibrarian(HashMap<Book,Integer> bookShelf) {
        this.bookShelf = bookShelf;
    }

    public HashMap<Book,Integer> arrangeBooks(HashMap<Book,Integer> books1,
                                              HashMap<Book,Integer> books2,
                                              HashMap<Book,Integer> books3,
                                              LinkedList<Order> orderList) {
        HashMap<Book,Integer> collectBooks = new HashMap<>();
        for (Book book : bookShelf.keySet()) {
            collectBooks.put(book,books1.get(book) + books2.get(book) + books3.get(book));
        }
        HashMap<Book,Integer> booksToOrder = new HashMap<>();
        if (collectBooks.size() == 0) {
            return booksToOrder;
        }
        HashSet<String> markBType = new HashSet<>();
        for (Order order:orderList) {
            Book book = order.getBook();
            if (order.getBook().getType().equals("C")) { //C类书
                if (collectBooks.get(book) > 0) {
                    if (booksToOrder.containsKey(book)) {
                        booksToOrder.put(book,booksToOrder.get(book) + 1);
                    } else {
                        booksToOrder.put(book,1);
                    }
                    collectBooks.put(book,collectBooks.get(book) - 1);
                }
            } else if (order.getBook().getType().equals("B")) { //借的是B类书
                String sid = order.getSid();
                if (collectBooks.get(book) > 0) { //有这本B书
                    if (markBType.contains(sid)) { //已经有过B书
                        continue;
                    }
                    if (booksToOrder.containsKey(book)) {
                        booksToOrder.put(book,booksToOrder.get(book) + 1);
                    } else {
                        booksToOrder.put(book,1);
                    }
                    collectBooks.put(book,collectBooks.get(book) - 1);
                    markBType.add(sid);
                }
            }
        }
        for (Book book:bookShelf.keySet()) {
            bookShelf.put(book,bookShelf.get(book) + collectBooks.get(book));
        }
        return booksToOrder;
    }
}
