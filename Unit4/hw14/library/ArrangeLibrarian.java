package library;

import utils.Book;
import utils.Calender;
import utils.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class ArrangeLibrarian {
    private HashMap<Book,Integer> bookShelf;

    private HashMap<Book,Integer> booksToOrder;

    public ArrangeLibrarian(HashMap<Book,Integer> bookShelf) {
        this.bookShelf = bookShelf;
    }

    public void arrangeBooks(int currentDays,HashMap<Book,Integer> books1,
                                              HashMap<Book,Integer> books2,
                                              HashMap<Book,Integer> books3,
                                              HashMap<Book,Integer> books4, //新购入的书
                                              LinkedList<Order> orderList) {
        HashMap<Book,Integer> collectBooks = new HashMap<>();
        for (Book book : bookShelf.keySet()) {
            collectBooks.put(book,books1.getOrDefault(book,0) +
                    books2.getOrDefault(book,0) + books3.getOrDefault(book,0));
        }
        ArrayList<Integer> date = Calender.calculateDate(currentDays); //从三个地方收集到的书
        for (Book book : collectBooks.keySet()) {
            int times = books1.getOrDefault(book,0) +
                    books2.getOrDefault(book,0) + books3.getOrDefault(book,0);
            for (int i = 0; i < times; i++) {
                System.out.printf("(State) [%04d-%02d-%02d] %s-%s transfers from " +
                                "NotAvailableForBorrowing to AvailableForBorrowing\n",
                        date.get(0),date.get(1),date.get(2),
                        book.getType(),book.getId());
            }
        }
        for (Book book : books4.keySet()) {
            if (collectBooks.containsKey(book)) {
                collectBooks.put(book,collectBooks.get(book) + books4.get(book));
            } else {
                collectBooks.put(book,books4.get(book));
            }
        }
        booksToOrder = new HashMap<>();
        if (collectBooks.size() == 0) { return; }
        HashSet<String> markBType = new HashSet<>();
        for (Order order:orderList) {
            Book book = order.getBook();
            if (order.getBook().getType().equals("C")) { //C类书
                if (collectBooks.get(book) > 0) {
                    if (booksToOrder.containsKey(book)) {
                        booksToOrder.put(book,booksToOrder.get(book) + 1);
                    } else { booksToOrder.put(book,1); }
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
                    } else { booksToOrder.put(book,1); }
                    collectBooks.put(book,collectBooks.get(book) - 1);
                    markBType.add(sid);
                }
            }
        }
        for (Book book:collectBooks.keySet()) {
            if (bookShelf.containsKey(book)) {
                bookShelf.put(book,bookShelf.get(book) + collectBooks.get(book));
            } else {
                bookShelf.put(book,collectBooks.get(book));
            }
        }
    }

    public HashMap<Book,Integer> getAndclearBooksToOrder() {
        HashMap<Book,Integer> temp = new HashMap<>();
        for (Book book:booksToOrder.keySet()) {
            temp.put(book,booksToOrder.get(book));
        }
        booksToOrder.replaceAll((b, v) -> 0);
        return temp;
    }
}
